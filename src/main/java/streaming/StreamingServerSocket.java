package streaming;

import res.VideoInfo;
import services.ClientHandle;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class StreamingServerSocket {
    private ServerSocket server = null;
    private final int port;
    private final ArrayList<VideoInfo> videosInfo;

    public StreamingServerSocket(int port, ArrayList<VideoInfo> videosInfo) {
        this.port = port;
        this.videosInfo = videosInfo;
    }
    public StreamingServerSocket(ArrayList<VideoInfo> videosInfo) {
        this(5000, videosInfo);
    }

    public ArrayList<VideoInfo> getVideoList() {
        return videosInfo;
    }

    public int getPort() {
        return this.port;
    }

    public void start() {
        int port = 50000;

        try {
            server = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (true) {
            Socket clientSocket = null;

            try {
                clientSocket = server.accept();
            } catch (IOException e) {
                e.printStackTrace();
            }

            new Thread(new ClientHandle(this, clientSocket, port)).start();

            // In case there will be an RTP streaming
            port += 2;
        }

    }
}
