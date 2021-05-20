package res;

import java.util.ArrayList;
import java.util.HashMap;

public class VideoInfo {
    private final String title;
    private final HashMap<Integer, ArrayList<String>> formats;

    public VideoInfo(String title, HashMap<Integer, ArrayList<String>> formats) {
        this.title = title;
        this.formats = formats;
    }

    public String getTitle() {
        return this.title;
    }

    public HashMap<Integer, ArrayList<String>> getFormats() {
        return this.formats;
    }

    public ArrayList<Integer> getSupportedResolutions(ArrayList<Integer> supportedResolutions, String format) {
        ArrayList<Integer> resolutions = new ArrayList<Integer>();

        for (Integer res : supportedResolutions) {
            ArrayList<String> resFormats = formats.get(res);

            if (resFormats != null) {
                int pos;
                if ((pos = resFormats.indexOf(format)) != -1) {
                    resolutions.add(res);
                }
            }
        }

        return resolutions;
    }
}
