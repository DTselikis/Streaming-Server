package services;

import res.MediaInfo;
import res.VideoInfo;
import streaming.StreamingServerSocket;

import javax.print.attribute.standard.Media;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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

    private short startStreaming(String filePath, String format, String protocol) {
        ProcessBuilder cmd = null;
        ArrayList<String> args = new ArrayList<String>();
        args.add("C:\\Users\\Louk\\Downloads\\ffmpeg\\ffmpeg.exe");

        if (protocol.equals("UDP")) {
            args.add("-re");
        }

        args.add("-i " + filePath);
        args.add("-f" + format);

        switch (protocol) {
            case "UDP": {
                args.add("udp://" + LOCALHOST + String.valueOf(this.port));
                break;
            }
            case "TCP": {
                args.add("tcp://" + LOCALHOST + String.valueOf(this.port) + "?listen");
                break;
            }
        }

        return 1;
    }

    private VideoInfo getVideoObjectFromTitle(String title) {
        for (VideoInfo video : server.getVideoList()) {
            if (video.getTitle().equals("title")) {
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
            msg = (String[]) input.readObject().toString().split("#");
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
            msg = (String[]) input.readObject().toString().split("#");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        String fileName = msg[0];
        String res = msg[1];
        String protocol = msg[2];

        startStreaming(getVideoObjectFromTitle(fileName).toFileName(res, format), format, protocol);
    }
}
