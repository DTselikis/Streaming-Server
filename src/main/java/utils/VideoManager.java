package utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import res.MediaInfo;
import res.VideoInfo;
import services.ServerServiceCallback;
import services.StreamingServerService;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class VideoManager implements Runnable {

    private final String videoTitle;
    private final File workingDirectory;
    private final String ffmpegDirectory;
    private final ServerServiceCallback callback;
    private File sourceFile;
    private HashMap<Integer, ArrayList<String>> formats = null;

    private static final Logger LOGGER = LogManager.getLogger(VideoManager.class);

    private ArrayList<String[]> missingFiles = new ArrayList<String[]>() {
        {
            add(new String[]{"240", "avi"});
            add(new String[]{"360", "avi"});
            add(new String[]{"480", "avi"});
            add(new String[]{"720", "avi"});
            add(new String[]{"1080", "avi"});

            add(new String[]{"240", "mp4"});
            add(new String[]{"360", "mp4"});
            add(new String[]{"480", "mp4"});
            add(new String[]{"720", "mp4"});
            add(new String[]{"1080", "mp4"});

            add(new String[]{"240", "mkv"});
            add(new String[]{"360", "mkv"});
            add(new String[]{"480", "mkv"});
            add(new String[]{"720", "mkv"});
            add(new String[]{"1080", "mkv"});
        }
    };

    public VideoManager(String videoTitle, File workingDirectory, ServerServiceCallback callback, String ffmpegDirectory) {
        this.videoTitle = videoTitle;
        this.workingDirectory = workingDirectory;
        this.callback = callback;
        this.ffmpegDirectory = ffmpegDirectory;
    }

    private void checkMissing() {
        File[] dirListing = workingDirectory.listFiles(new VideoFileNameFilter(videoTitle));
        formats = new HashMap<Integer, ArrayList<String>>();

        int maxRes = 0;

        // Removes files that exists
        for (File file : dirListing) {
            int resPos = file.getName().indexOf('-');
            int dotPos = file.getName().lastIndexOf('.');
            String res = file.getName().substring(resPos + 1, dotPos - 1);
            String container = file.getName().substring(dotPos + 1);

            try {
                missingFiles.remove(findIndex(res, container));
            }
            catch (IndexOutOfBoundsException ex) {
                LOGGER.error(ex.getMessage());
                ex.printStackTrace();
            }


            if (maxRes < Integer.parseInt(res)) {
                maxRes = Integer.parseInt(res);
                sourceFile = file;
            }

            addFormat(res, container);
        }

        // Remove all higher resolutions
        switch (maxRes) {
            case 240: {
                missingFiles.remove(findIndex("360", "avi"));
                missingFiles.remove(findIndex("360", "mp4"));
                missingFiles.remove(findIndex("360", "mkv"));
            }
            case 360: {
                missingFiles.remove(findIndex("480", "avi"));
                missingFiles.remove(findIndex("480", "mp4"));
                missingFiles.remove(findIndex("480", "mkv"));
            }
            case 480: {
                missingFiles.remove(findIndex("720", "avi"));
                missingFiles.remove(findIndex("720", "mp4"));
                missingFiles.remove(findIndex("720", "mkv"));
            }
            case 720: {
                missingFiles.remove(findIndex("1080", "avi"));
                missingFiles.remove(findIndex("1080", "mp4"));
                missingFiles.remove(findIndex("1080", "mkv"));
            }
        }
    }

    private void addFormat(String res, String container) {
        // For each available resolution create a new ArrayList with its available
        // containers
        if (!formats.containsKey(MediaInfo.valueOf(res))) {
            formats.put(MediaInfo.valueOf(res), new ArrayList<String>());
        }
        formats.get(MediaInfo.valueOf(res)).add(container);
    }

    private int findIndex(String res, String container) {
        int i = 0;
        for (String[] missingFile : missingFiles) {
            if (missingFile[0].equals(res) && missingFile[1].equals(container)) {
                return i;
            }
            i++;
        }

        return -1;
    }

    @Override
    public void run() {
        checkMissing();
        Thread[] threads = new Thread[missingFiles.size()];

        int i = 0;
        // For each missing file create a new thread for the convertion
        for (String[] missingFile : missingFiles) {
            threads[i] = new Thread(new VideoConverter(Integer.valueOf(missingFile[0]), missingFile[1], sourceFile, ffmpegDirectory));
            threads[i].start();

            addFormat(missingFile[0], missingFile[1]);

            i++;
        }

        callback.callback(new VideoInfo(videoTitle, formats));

        // Wait for each thread to finish
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                LOGGER.error(e.getMessage());
                e.printStackTrace();
            }
        }

        LOGGER.info("All video convertions ended.");
    }
}