package musicvisualizer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * @author Tristan Hunter
 * 
 * This class configures the application window, applies a CSS document to the GUI, and launches the app
 * 
 */
public class MusicVisualizer extends Application 
{
    
    @Override
    public void start(Stage stage) throws Exception 
    {
        Parent root = FXMLLoader.load(getClass().getResource("FXMLDocument.fxml"));
        
        Scene scene = new Scene(root);
        
        scene.getStylesheets().add(getClass().getResource("/Resource/GUIStyle.css").toExternalForm());
        stage.setTitle("JavaFX Audio Player");
        stage.getIcons().add(new Image("/Resource/Vinyl.gif"));
        stage.setScene(scene);
        stage.setMinHeight(450);
        stage.setMinWidth(520);
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) 
    {
        launch(args);
    }
    
}
