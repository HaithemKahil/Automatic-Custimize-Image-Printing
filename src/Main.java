import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("fxmlfiles/mainscreen.fxml"));
        primaryStage.setTitle("Printing Tool");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }


    public static void main(String[] args) throws IOException {
	// write your code here

        /*BufferedImage img = ImageIO.read(new File(args[0]));
        construct(img);

        new Thread(new PrintActionListener());

        new Thread(new ImageDownloaderManager()).start();
        new Thread(new ImageDownloaderManager()).start();
        new Thread(new ImageDownloaderManager()).start();

        new Thread(new ImageManipulationManager()).start();
        new Thread(new ImageManipulationManager()).start();
        new Thread(new ImageManipulationManager()).start();
        while(true){}
        */
        launch(args);
    }
}
