package services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import res.MediaInfo;
import res.VideoInfo;
import streaming.StreamingServerSocket;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class ClientHandle implements Runnable {
    private final StreamingServerSocket server;
    private final Socket socket;
    private final int port;

    private final String LOCALHOST = "127.0.0.1";

    private static final Logger LOGGER = LogManager.getLogger(ClientHandle.class);

    public ClientHandle(StreamingServerSocket server, Socket socket, int port) {
        this.server = server;
        this.socket = socket;
        this.port = port;
    }

    private HashMap<String, ArrayList<Integer>> getSupportedFiles(ArrayList<Integer> supportedResolutions, String format) {
        HashMap<String, ArrayList<Integer>> supportedFiles = new HashMap<String, ArrayList<Integer>>();

        for (VideoInfo video : server.getVideoList()) {
            supportedFiles.put(video.getTitle(), video.getSupportedResolutions(supportedResolutions, format));
        }

        return supportedFiles;
    }

    private String startStreaming(String filePath, String format, String protocol) {
        ProcessBuilder cmd;
        ArrayList<String> args = new ArrayList<String>();
        args.add("C:\\Users\\Louk\\Downloads\\ffmpeg\\ffmpeg.exe");

        if (protocol.equals("UDP") || protocol.equals("RTP")) {
            args.add("-re");
        }

        args.add("-i");
        args.add("C:\\Users\\Louk\\IdeaProjects\\StreamingServer\\vid\\" + filePath);

        String response = "";
        switch (protocol) {
            case "UDP": {
                args.add("-f");
                args.add("mpegts");
                args.add("udp://" + LOCALHOST + ":" +  this.port);

                response = String.valueOf(this.port);
                break;
            }
            case "TCP": {
                args.add("-f");
                args.add("mpegts");
                args.add("tcp://" + LOCALHOST + ":" + this.port + "?listen");

                response = String.valueOf(this.port);
                break;
            }
            case "RTP": {
                args.add("-an");
                args.add("-c:v");
                args.add("copy");
                args.add("-f");
                args.add("rtp");

                // Full path to the file in file system
                int pos = filePath.lastIndexOf('\\');
                int extPos = filePath.indexOf('.');
                String sdpFile = server.getWorkingDirectory() + "\\" + filePath.substring(pos+1, extPos) + "_" + this.port + ".sdp";

                args.add("-sdp_file");
                args.add(sdpFile);
                args.add("\"rtp://" + LOCALHOST + ":" + this.port + "\"");

                response = sdpFile;
            }
        }

        cmd = new ProcessBuilder(args);

        try {
            cmd.inheritIO().start();
            LOGGER.info("Streaming started for port: " + this.port + " : " + args.toString());
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            e.printStackTrace();
        }

        return response;
    }

    private VideoInfo getVideoObjectFromTitle(String title) {
        for (VideoInfo video : server.getVideoList()) {
            if (video.getTitle().equals(title)) {
                return video;
            }
        }

        return null;
    }

    @Override
    public void run() {
        LOGGER.info("Client on port " + this.port + "initialized!");
        ObjectInputStream input = null;
        ObjectOutputStream output = null;

        try {
            input = new ObjectInputStream(socket.getInputStream());
            output = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            e.printStackTrace();
        }

        String[] msg = null;

        // Receive bitrate and format from client
        try {
            msg = input.readObject().toString().split("#");
            LOGGER.info("Received bitrate and format for port: " + this.port);
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            LOGGER.error(e.getMessage());
            e.printStackTrace();
        }

        int bitrate = Integer.parseInt(msg[0]);
        String format = msg[1];

        // Send list of supported files
        try {
            output.writeObject(getSupportedFiles(MediaInfo.getResFromBitrate(bitrate), format));
            LOGGER.info("Sent list of supported files for port: " + this,port);
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            e.printStackTrace();
        }

        // Receive file name to be streamed and protocol
        try {
            msg = input.readObject().toString().split("#");
            LOGGER.info("Received file name to be streamed for port: " + this.port);
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            LOGGER.error(e.getMessage());
            e.printStackTrace();
        }

        String fileName = msg[0];
        String res = msg[1];
        String protocol = msg[2];

        String response;
        response = startStreaming(getVideoObjectFromTitle(fileName).toFileName(res, format), format, protocol);

        // For RTP protocol the rest of the communication will
        // be handled by FileServer
        if (protocol.equals("RTP")) {
            LOGGER.info("RTP protocol detected for port: " + this.port);
            OutputStream outStream = null;
            try {
                outStream = socket.getOutputStream();
                new FileServer(response, output, outStream).transmit();
                outStream.close();
            }
            catch (IOException ex) {
                LOGGER.error(ex.getMessage());
                ex.printStackTrace();
            }
        }
        else {
            // Notify client to start playing
            try {
                output.writeObject(response);
                LOGGER.info("Notified client to start playing for port: " + this.port);
            } catch (IOException e) {
                LOGGER.error(e.getMessage());
                e.printStackTrace();
            }
        }

        try {
            input.close();
            output.close();
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            e.printStackTrace();
        }

    }
}
