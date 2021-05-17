package utils;

import res.MediaInfo;
import java.io.File;
import java.io.FilenameFilter;

public class VideoFileNameFilter implements FilenameFilter {

    private final String videoTitle;

    public VideoFileNameFilter(String videoTitle) {
        this.videoTitle = videoTitle;
    }

    @Override
    // Checks every combination of container and resolution for one filename
    public boolean accept(File dir, String name) {
        for (Integer res : MediaInfo.getResolutions()) {
            for (String container : MediaInfo.getContainers()) {
                if(name.equals(videoTitle + "-" + String.valueOf(res) + "p." + container)) {
                    return true;
                }
            }
        }
        return false;
    }
}
