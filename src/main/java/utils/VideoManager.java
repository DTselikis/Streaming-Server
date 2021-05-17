package utils;

import res.VideoInfo;

import java.io.File;
import java.util.ArrayList;

public class VideoManager implements Runnable {

    private final String name;
    private final File workingDirectory;
    private File sourceFile;

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
            add(new String[]{"1080", "avi"});
        }
    };

    public VideoManager(String name, File workingDirectory) {
        this.name = name;
        this.workingDirectory = workingDirectory;
    }

    private void checkMissing() {
        File[] dirListing = workingDirectory.listFiles(new VideoFileNameFilter());
        ArrayList<String[]> unsupported = new ArrayList<String[]>();
        int maxRes = 0;

        // Removes files that exists
        for (File file : dirListing) {
            int resPos = file.getName().indexOf('-');
            int dotPos = file.getName().lastIndexOf('.');
            String res = file.getName().substring(resPos, dotPos - 2);
            String container = file.getName().substring(dotPos);

            missingFiles.remove(new String[]{res,container});

            if (maxRes < Integer.parseInt(res)) {
                maxRes = Integer.parseInt(res);
                sourceFile = file;
            }
        }
        
        // Remove all higher resolutions
        switch (maxRes) {
            case 240: {
                unsupported.add(new String[]{"360", "avi"});
                unsupported.add(new String[]{"360", "mp4"});
                unsupported.add(new String[]{"360", "mkv"});
            }
            case 360: {
                unsupported.add(new String[]{"480", "avi"});
                unsupported.add(new String[]{"480", "mp4"});
                unsupported.add(new String[]{"480", "mkv"});
            }
            case 480: {
                unsupported.add(new String[]{"720", "avi"});
                unsupported.add(new String[]{"720", "mp4"});
                unsupported.add(new String[]{"720", "mkv"});
            }
            case 720: {
                unsupported.add(new String[]{"1080", "avi"});
                unsupported.add(new String[]{"1080", "mp4"});
                unsupported.add(new String[]{"1080", "mkv"});
            }
        }

        missingFiles.removeAll(unsupported.subList(0, unsupported.size() - 1));
    }

    @Override
    public void run() {
        checkMissing();
        Thread[] threads = new Thread[missingFiles.size()];

        // For each missing file create a new thread for the convertion
        for (String[] missingFile : missingFiles) {
            int i = 0;
            threads[i] = new Thread(new VideoConverter(Integer.valueOf(missingFile[0]), missingFile[1], sourceFile));
            threads[i].start();
        }

        // Wait for each thread to finish
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}