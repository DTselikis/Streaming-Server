package streaming;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import res.VideoInfo;
import services.ClientHandle;
import services.StreamingServerService;

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

    private static final Logger LOGGER = LogManager.getLogger(StreamingServerSocket.class);

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
        LOGGER.info("Streaming server socket started!");
        LOGGER.info("Base client port: " + clientPort);

        try {
            server = new ServerSocket(this.port);
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (true) {
            Socket clientSocket = null;

            try {
                clientSocket = server.accept();
                LOGGER.info("New client accepted!");
            } catch (IOException e) {
                LOGGER.error(e.getMessage());
                e.printStackTrace();
            }

            new Thread(new ClientHandle(this, clientSocket, clientPort)).start();

            // In case there will be an RTP streaming
            clientPort += 2;
        }

    }
}
