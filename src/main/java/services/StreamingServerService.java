package services;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import res.VideoInfo;
import streaming.StreamingServerSocket;
import utils.VideoExtensionFileFilter;
import utils.VideoManager;

import java.io.File;
import java.util.ArrayList;

public class StreamingServerService implements Runnable{
    private final File workingDirectory;
    private ArrayList<String> videoTitles;
    private ArrayList<VideoInfo> videosInfo;

    private static final Logger LOGGER = LogManager.getLogger(StreamingServerService.class);

    public StreamingServerService(String rootDirectory) {
        workingDirectory = new File(rootDirectory);

        this.videosInfo = new ArrayList<VideoInfo>();
    }

    // Callback method
    public void addVideoInfo(VideoInfo videoInfo) {
        this.videosInfo.add(videoInfo);
    }

    @Override
    public void run() {
        videoTitles = new ArrayList<String>();
        try {
            String[] filters = {".avi", ".mp4", ".mkv"};
            File[] dirListing = workingDirectory.listFiles(new VideoExtensionFileFilter(filters));

            for (File video : dirListing) {
                String fileName = video.getName();
                int pos = fileName.lastIndexOf('-');
                String videoTitle = fileName.substring(0, pos);

                if (!videoTitles.contains(videoTitle)) {
                    videoTitles.add(videoTitle);
                }
            }
        }
        catch (Exception ex) {
            LOGGER.error(ex.getMessage());
        }

        ServerServiceCallback callback = new ServerServiceCallback(this);

        Thread[] videoManagers = new Thread[videoTitles.size()];

        int i = 0;
        // Start a VideoManager for each unique video title
        for (String videoTitle : videoTitles) {
            videoManagers[i] = new Thread(new VideoManager(videoTitle, workingDirectory, callback));
            videoManagers[i].start();

            i++;

            LOGGER.info("Video manager " + i + "started.");
        }

        for (Thread thread : videoManagers) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        LOGGER.info("All video managers threads was ended.");

        // Server will listen for clients after all convertions have ended
        new StreamingServerSocket(5000, videosInfo, workingDirectory).start();
    }
}
