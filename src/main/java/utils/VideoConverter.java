package utils;

import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import res.VideoInfo;

import java.io.File;
import java.io.IOException;

public class VideoConverter implements Runnable {

    private final Integer targerRes;
    private final String targerContainer;
    private final File source;

    public VideoConverter(Integer targerRes, String targerContainer, File source) {
        this.targerRes = targerRes;
        this.targerContainer = targerContainer;
        this.source = source;
    }

    public VideoConverter(String targetRes, String targerContainer, File source) {
        this(Integer.valueOf(targetRes), targerContainer, source);
    }

    @Override
    public void run() {
        FFmpeg ffmpeg = null;
        FFmpegBuilder builder = null;
        FFprobe ffprobe = null;

        int resPos = source.getName().indexOf('-');
        StringBuilder dest = new StringBuilder();
        dest.append(source.getName().substring(0, resPos)).append(targerRes).append("p.").append(targerContainer);

        Integer[] resolution = VideoInfo.getResolution(targerRes);

        try {
             ffmpeg = new FFmpeg("C:\\Users\\Louk\\Downloads\\ffmpeg\\ffmpeg.exe");
             ffprobe = new FFprobe("C:\\Users\\Louk\\Downloads\\ffmpeg\\ffprobe.exe");
        } catch (IOException e) {
            e.printStackTrace();
        }

        builder = new FFmpegBuilder()
                .setInput(source.getName())
                .addOutput(dest.toString())
                .setFormat(targerContainer)
                .setVideoResolution(resolution[0], resolution[1])
                .done();

        FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);
    }
}