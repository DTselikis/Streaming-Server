package services;

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
                args.add(format);
                args.add("udp://" + LOCALHOST + ":" +  this.port);

                response = String.valueOf(this.port);
                break;
            }
            case "TCP": {
                args.add("-f");
                args.add(format);
                args.add("tcp://" + LOCALHOST + ":" + this.port + "?listen");

                response = String.valueOf(this.port);
                break;
            }
            case "RTP": {
                args.add("-c:v");
                args.add("copy");
                args.add("-f rtp");

                int pos = filePath.lastIndexOf('\\');
                int extPos = filePath.indexOf('.');
                String sdpFile = filePath.substring(pos+1, extPos) + "_" + this.port + ".sdp";

                args.add("-sdp_file");
                args.add(sdpFile);
                args.add("\"" + LOCALHOST + ":" + this.port + "\"");

                response = sdpFile;
            }
        }

        cmd = new ProcessBuilder(args);

        try {
            cmd.start();
        } catch (IOException e) {
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
        ObjectInputStream input = null;
        ObjectOutputStream output = null;

        try {
            input = new ObjectInputStream(socket.getInputStream());
            output = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        String[] msg = null;

        // Receive bitrate and format from client
        try {
            msg = input.readObject().toString().split("#");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        int bitrate = Integer.parseInt(msg[0]);
        String format = msg[1];

        // Send list of supported files
        try {
            output.writeObject(getSupportedFiles(MediaInfo.getResFromBitrate(bitrate), format));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Receive file name to be streamed and protocol
        try {
            msg = input.readObject().toString().split("#");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        String fileName = msg[0];
        String res = msg[1];
        String protocol = msg[2];

        String response;
        response = startStreaming(getVideoObjectFromTitle(fileName).toFileName(res, format), format, protocol);

        // Notify client to start playing
        try {
            output.writeObject(response);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (protocol.equals("RTP")) {
            try {
                new FileServer(server.getWorkingDirectory() + response, socket.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            input.close();
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
