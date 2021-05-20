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

    public ClientHandle(StreamingServerSocket server, Socket socket) {
        this.server = server;
        this.socket = socket;
    }

    private HashMap<String, ArrayList<Integer>> getSupportedFiles(ArrayList<Integer> supportedResolutions, String format) {
        HashMap<String, ArrayList<Integer>> supportedFiles = new HashMap<String, ArrayList<Integer>>();

        for (VideoInfo video : server.getVideoList()) {
            supportedFiles.put(video.getTitle(), video.getSupportedResolutions(supportedResolutions, format));
        }

        return supportedFiles;
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
        try {
            msg = (String[]) input.readObject().toString().split("#");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        int bitrate = Integer.parseInt(msg[0]);
        String format = msg[1];

        try {
            output.writeObject(getSupportedFiles(MediaInfo.getResFromBitrate(bitrate), format));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            msg = (String[]) input.readObject().toString().split("#");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }




    }
}
