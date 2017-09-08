package GUIControl;

import ImageDownloadTask.ImageDownloaderManager;
import ImageProcessing.ImageManipulationManager;
import imageprinting.PrintActionListener;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * Created by haithemkahil on 30/08/17.
 */
public class MainScreenController implements Initializable{
    @FXML
    private TextField frameImageTextField;

    @FXML
    private Button configSubmitButton;

    @FXML
    private Button startButton;

    @FXML
    private TextField jsonUrlTextField;

    @FXML
    private Button configModifyButton;

    @FXML
    private MenuButton printerMenuButton;

    @FXML
    private TextArea processArea;

    @FXML
    private Button cancelButton;

    @FXML
    private Button changeButton;

    public Stage secondaryStage = null ;
    Thread t;

    ArrayList<Thread> downloadThreads = new ArrayList<>();
    ArrayList<Thread> imageProcessThreads = new ArrayList<>();
    Thread printingThread ;
    public static void construct(){
        ImageManipulationManager.mainImages = new ArrayList<>();

        ImageManipulationManager.NEXT_MAIN_IMAGE_INDEX = -1;

        PrintActionListener.readyToBePrintedImages = new ArrayList<>();
        PrintActionListener.NEXT_IMAGE_TO_PRINT = -1;
        PrintActionListener.NEXT_IMAGE_TO_DISPLAY = -1;
        ImageManipulationManager.size=0;
        ImageDownloaderManager.mainImageUrls = new ArrayList<>();
        ImageDownloaderManager.NEXT_DOWNLOADABLE_URL_INDEX = -1;
        ImageDownloaderManager.EXTRACTING_URLS = false;
    }

    public void submitConfiguration(ActionEvent actionEvent) {
        configModifyButton.setDisable(false);
        BufferedImage img = null;
        try {
            img = ImageIO.read(new File(frameImageTextField.getText()));
            ImageManipulationManager.frameImage = img;
            frameImageTextField.setDisable(true);
            printerMenuButton.setDisable(true);
            configSubmitButton.setDisable(true);
        } catch (IOException e) {
            processArea.appendText("Config Panel : FRAME_IMAGE NOT FOUND"+"\n");
        }
    }

    public void modifyConfiguration(ActionEvent actionEvent) {
        frameImageTextField.setDisable(false);
        printerMenuButton.setDisable(false);
        configSubmitButton.setDisable(false);
        cancelProcess(new ActionEvent());

    }

    public void startProcess(ActionEvent actionEvent) {


        if(secondaryStage == null ){
            ImageDownloaderManager.JSON_URL = jsonUrlTextField.getText();
            for(int i=0;i<4;i++){downloadThreads.add(new Thread(new ImageDownloaderManager()));downloadThreads.get(downloadThreads.size()-1).start();}
            for(int i=0;i<4;i++){imageProcessThreads.add(new Thread(new ImageManipulationManager()));imageProcessThreads.get(imageProcessThreads.size()-1).start();}
            printingThread = new Thread(new PrintActionListener());
            secondaryStage = new Stage();
            ScrollPane root = new ScrollPane();
            TilePane tile = new TilePane();
            root.setStyle("-fx-background-color: DAE6F3;");
            tile.setPadding(new Insets(15, 15, 15, 15));
            tile.setHgap(15);
            tile.setVgap(15);

            root.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); // Horizontal
            root.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED); // Vertical scroll bar
            root.setFitToWidth(true);
            root.setContent(tile);

            secondaryStage.setWidth(Screen.getPrimary().getVisualBounds().getWidth());
            secondaryStage.setHeight(Screen.getPrimary().getVisualBounds()
                    .getHeight());
            Scene scene = new Scene(root);
            secondaryStage.setScene(scene);
            secondaryStage.show();
            Task<Void> task = new Task<Void>() {

                @Override protected Void call() throws Exception {
                    String path = "/home/haithemkahil/IdeaProjects/PostcardPrinter/src/images";

                    File folder = new File(path);
                    File[] listOfFiles = folder.listFiles();

                    while(true){
                        Platform.runLater(new Runnable() {
                            @Override public void run() {
                                ImageView imageView;
                                BufferedImage bufferedImage = PrintActionListener.getNextImageToDisplay();
                                if(bufferedImage == null){}
                                else{
                                    imageView = createImageView(secondaryStage,bufferedImage);
                                    tile.getChildren().addAll(imageView);
                                }
                            }
                        });
                        Thread.sleep(2000);
                    }
                }
            };
            t = new Thread(task);
            t.setDaemon(true);
            t.start();
        }else {}

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        printerMenuButton.getItems().clear();
        construct();
        PrintActionListener.selectPrinter(printerMenuButton);
        configModifyButton.setDisable(true);
    }

    public void changeUrl(ActionEvent actionEvent) {
        for(Thread t:downloadThreads){
            try {
                t.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        ImageDownloaderManager.JSON_URL = jsonUrlTextField.getText();
        processArea.appendText("Json URL changed succefully to :"+ImageDownloaderManager.JSON_URL+"\n");


    }

    public void cancelProcess(ActionEvent actionEvent) {
        processArea.appendText("Process Canceled"+"\n");
        try {
            for(Thread t : downloadThreads){t.stop();}
        }catch (Exception ex){}
        try{
            for(Thread t : imageProcessThreads){t.stop();}
        }catch (Exception ex){}
        try{
            printingThread.stop();
        }catch(Exception ex){}

        try{
            secondaryStage.getScene().getWindow().hide();
            secondaryStage = null;

        }catch(Exception ex){}
        try{
            t.stop();
        }catch (Exception ex){}
        construct();

    }

    private ImageView createImageView(Stage stage, BufferedImage image) {
        // DEFAULT_THUMBNAIL_WIDTH is a constant you need to define
        // The last two arguments are: preserveRatio, and use smooth (slower)
        // resizing
        if(image==null){}
        else {
            Image fxImage = SwingFXUtils.toFXImage(image,null);

            ImageView imageView = null;
            imageView = new ImageView(fxImage);
            imageView.setFitWidth(200);
            imageView.setFitHeight(266);
            imageView.setOnMouseClicked(new EventHandler<MouseEvent>() {

                @Override
                public void handle(MouseEvent mouseEvent) {

                    if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){

                        if(mouseEvent.getClickCount() == 2){
                            BorderPane borderPane = new BorderPane();
                            ImageView imageView = new ImageView();
                            imageView.setImage(fxImage);
                            imageView.setStyle("-fx-background-color: BLACK");
                            imageView.setFitHeight(stage.getHeight() - 10);
                            imageView.setPreserveRatio(true);
                            imageView.setSmooth(true);
                            imageView.setCache(true);
                            borderPane.setCenter(imageView);
                            borderPane.setStyle("-fx-background-color: BLACK");
                            Stage newStage = new Stage();
                            newStage.setWidth(stage.getWidth());
                            newStage.setHeight(stage.getHeight());
                            Scene scene = new Scene(borderPane, Color.BLACK);
                            newStage.setScene(scene);
                            newStage.show();

                        }
                    }
                }
            });
            return imageView;
        }
        return null;
    }
}
