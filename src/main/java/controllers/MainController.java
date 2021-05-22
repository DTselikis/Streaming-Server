package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import services.StreamingServerService;

import java.io.File;

public class MainController {

    @FXML
    private Button root_browse_btn;
    @FXML
    private Button ffmpeg_browse_btn;
    @FXML
    private TextField rootDir_tf;
    @FXML
    private TextField ffmpeg_tf;

    public void ffmpeg_browse_btn_onClick(ActionEvent e) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Choose the directory that includes ffmpeg and ffprob executables");
        File file = directoryChooser.showDialog(ffmpeg_browse_btn.getScene().getWindow());

        if (file != null) {
            ffmpeg_tf.setText(file.getAbsolutePath());
        }
    }

    public void root_browse_btn_onClick(ActionEvent e) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Choose the videos directory");
        File file = directoryChooser.showDialog(root_browse_btn.getScene().getWindow());

        if (file != null) {
            rootDir_tf.setText(file.getAbsolutePath());
        }
    }

    public void start_btn_onClick(ActionEvent e) {
        String rootDirectory = rootDir_tf.getText();

        if (ffmpeg_tf.getText().equals("")) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("ffmpeg/ffprobe directory not provided");
            alert.setHeaderText(null);
            alert.setContentText("Please provide ffmpeg/ffprobe directory!");
            alert.showAndWait();
        }

        if (rootDirectory.equals("")) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Root directory not provided");
            alert.setHeaderText(null);
            alert.setContentText("Please provide a root directory!");
            alert.showAndWait();
        }
        else {
            StreamingServerService service = new StreamingServerService(rootDirectory);
            service.start();
        }
    }
}
