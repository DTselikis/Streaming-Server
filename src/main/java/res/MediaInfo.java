package res;

import java.util.HashMap;
import java.util.Set;

public class MediaInfo {
    public static final Integer FHD = 1080; // Full High Definition
    public static final Integer HD = 720; // High Definition
    public static final Integer ED = 480; // Enhanced Definition
    public static final Integer SD = 360; // Standard Definition
    public static final Integer LD = 240; // Low Definition

    public static final int MAX_BITRATE = 2;
    public static final int MEDIUM_BITRATE = 1;
    public static final int LOW_BITRATE = 0;

    private static final HashMap<Integer, Integer[]> resolutions = new HashMap<Integer, Integer[]>() {{
        put(LD, new Integer[]{426, 240});
        put(SD, new Integer[]{640, 360});
        put(ED, new Integer[]{854, 480});
        put(HD, new Integer[]{1280, 720});
        put(FHD, new Integer[]{1920, 1080});
    }};

    private static final HashMap<Integer, Integer[]> bitrates = new HashMap<Integer, Integer[]>() {{
        put(LD, new Integer[]{300, 400, 700});
        put(SD, new Integer[]{400, 750, 1000});
        put(ED, new Integer[]{500, 1000, 2000});
        put(HD, new Integer[]{1500, 2500, 4000});
        put(FHD, new Integer[]{3000, 4500, 6000});
    }};

    private static final String[] containers = new String[] {"avi", "mp4", "mkv"};

    public static Integer[] getResBitrates(Integer res) {
        return bitrates.get(res);
    }

    public static Integer getResBitrate(Integer res, int bitrate) {
        return bitrates.get(res)[bitrate];
    }

    public static Set<Integer> getResolutions() {
        return resolutions.keySet();
    }

    public static Integer[] getResolution(Integer res) {
        return resolutions.get(res);
    }

    public static String[] getContainers() {
        return containers;
    }

    public static Integer valueOf(String res) {
        Integer IntRes = null;

        switch (res) {
            case "240": IntRes =  LD; break;
            case "360": IntRes =  SD; break;
            case "480": IntRes =  ED; break;
            case "720": IntRes =  HD; break;
            case "1080": IntRes =  FHD; break;
        }

        return IntRes;
    }
}
