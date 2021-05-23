import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.TextAreaAppender;

public class StreamingServer extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("StreamingServer.fxml"));
        Scene scene = new Scene(loader.load());
        TextAreaAppender.textArea = (TextArea) scene.lookup("#loggingTA");

        stage.setScene(scene);
        stage.setTitle("Streaming server");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
