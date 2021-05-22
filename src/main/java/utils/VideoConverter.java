package utils;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import res.MediaInfo;

import java.io.File;
import java.io.IOException;

public class VideoConverter implements Runnable {

    private final Integer targetRes;
    private final String targetContainer;
    private final File source;

    @FXML
    private TextField ffmpeg_tf;

    public VideoConverter(Integer targetRes, String targetContainer, File source) {
        this.targetRes = targetRes;
        this.targetContainer = targetContainer;
        this.source = source;
    }

    public VideoConverter(String targetRes, String targetContainer, File source) {
        this(Integer.valueOf(targetRes), targetContainer, source);
    }

    @Override
    public void run() {
        FFmpeg ffmpeg = null;
        FFmpegBuilder builder = null;
        FFprobe ffprobe = null;

        int resPos = source.toString().indexOf('-');
        StringBuilder dest = new StringBuilder();
        dest.append(source.toString().substring(0, resPos + 1)).append(targetRes).append("p.").append(targetContainer);

        Integer[] resolution = MediaInfo.getResolution(targetRes);

        try {
             ffmpeg = new FFmpeg(ffmpeg_tf.getText() + "\\ffmpeg.exe");
             ffprobe = new FFprobe(ffmpeg_tf.getText() + "\\ffprobe.exe");
        } catch (IOException e) {
            e.printStackTrace();
        }

        builder = new FFmpegBuilder()
                .setInput(source.toString())
                .addOutput(dest.toString())
                .setFormat((targetContainer.equals("mkv") ? "matroska" : targetContainer))
                .setVideoResolution(resolution[0], resolution[1])
                .done();

        FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);

        // Run a one-pass encode
        executor.createJob(builder).run();
    }
}