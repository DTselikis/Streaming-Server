package utils;

import java.io.File;
import java.io.FileFilter;

public class VideoExtensionFileFilter implements FileFilter {
    private final String[] filters;

    public VideoExtensionFileFilter(String[] filters) {
        this.filters = filters;
    }

    @Override
    public boolean accept(File pathname) {
        for (String filter : filters) {
            if (pathname.getName().endsWith(filter)) {
                return true;
            }
        }
        return false;
    }
}
