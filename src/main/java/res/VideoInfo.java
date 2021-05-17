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
}
