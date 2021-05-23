package utils;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import res.MediaInfo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class VideoConverter implements Runnable {

    private final Integer targetRes;
    private final String targetContainer;
    private final File source;
    private final String ffmpegDirectory;

    private static final Logger LOGGER = LogManager.getLogger(VideoConverter.class);

    @FXML
    private TextField ffmpeg_tf;

    public VideoConverter(Integer targetRes, String targetContainer, File source, String ffmpegDirectory) {
        this.targetRes = targetRes;
        this.targetContainer = targetContainer;
        this.source = source;
        this.ffmpegDirectory = ffmpegDirectory;
    }

    public VideoConverter(String targetRes, String targetContainer, File source, String ffmpegDirectory) {
        this(Integer.valueOf(targetRes), targetContainer, source, ffmpegDirectory);
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
             ffmpeg = new FFmpeg(Paths.get(ffmpegDirectory, "ffmpeg.exe").toString());
             ffprobe = new FFprobe(Paths.get(ffmpegDirectory, "ffprobe.exe").toString());
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

        LOGGER.info("Converting " + dest.toString());

        // Run a one-pass encode
        executor.createJob(builder).run();

        LOGGER.info("Convertion for " + dest.toString() + "ended");
    }
}