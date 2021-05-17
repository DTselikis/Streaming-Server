package services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import res.VideoInfo;
import utils.VideoExtensionFileFilter;
import utils.VideoManager;

import java.io.File;
import java.util.ArrayList;

public class StreamingServerService {
    private final File workingDirectory;
    private ArrayList<String> videoTitles;
    private ArrayList<VideoInfo> videosInfo;

    private static final Logger LOGGER = LogManager.getLogger(StreamingServerService.class);

    public StreamingServerService(String[] args) {
        String dir = "";
        try {
            dir = args[0];
        }
        catch (IndexOutOfBoundsException ex) {
            LOGGER.info("Working directory was not provided. Using launch directory...");
            dir = System.getProperty("user.dir");
        }
        finally {
            workingDirectory = new File(dir);
        }

        this.videosInfo = new ArrayList<VideoInfo>();
    }

    public void addVideoInfo(VideoInfo videoInfo) {
        this.videosInfo.add(videoInfo);
    }

    public int start() {
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

            return 1;
        }

        ServerServiceCallback callback = new ServerServiceCallback(this);

        // Start a VideoManager for each unique video title
        for (String videoTitle : videoTitles) {
            new Thread(new VideoManager(videoTitle, workingDirectory, callback)).start();
        }


        return 0;
    }
}
