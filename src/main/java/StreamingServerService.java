import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.VideoExtensionFileFilter;
import utils.VideoManager;

import java.io.File;
import java.util.ArrayList;

public class StreamingServerService {
    private final File workingDirectory;
    private ArrayList<String> videoTitles;

    private static final Logger LOGGER = LogManager.getLogger(StreamingServer.class);

    public StreamingServerService(String[] args) {
        String dir = "";
        try {
            dir = args[1];
        }
        catch (IndexOutOfBoundsException ex) {
            LOGGER.info("Working directory was not provided. Using launch directory...");
            dir = args[0];
        }
        finally {
            workingDirectory = new File(dir);
        }
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

        // Start a VideoManager for each unique video title
        for (String videoTitle : videoTitles) {
            new Thread(new VideoManager(videoTitle, workingDirectory)).start();
        }


        return 0;
    }
}
