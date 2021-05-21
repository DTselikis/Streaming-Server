package streaming;

import res.VideoInfo;
import services.ClientHandle;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class StreamingServerSocket {
    private ServerSocket server = null;
    private final int port;
    private final ArrayList<VideoInfo> videosInfo;
    private final File workingDirectory;

    public StreamingServerSocket(int port, ArrayList<VideoInfo> videosInfo, File workingDirectory) {
        this.port = port;
        this.videosInfo = videosInfo;
        this.workingDirectory = workingDirectory;
    }

    public StreamingServerSocket(ArrayList<VideoInfo> videosInfo, File workingDirectory) {
        this(5000, videosInfo, workingDirectory);
    }

    public ArrayList<VideoInfo> getVideoList() {
        return videosInfo;
    }

    public int getPort() {
        return this.port;
    }

    public File getWorkingDirectory() {
        return  this.workingDirectory;
    }

    public void start() {
        int clientPort = 5001;

        try {
            server = new ServerSocket(this.port);
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

            new Thread(new ClientHandle(this, clientSocket, clientPort)).start();

            // In case there will be an RTP streaming
            clientPort += 2;
        }

    }
}
