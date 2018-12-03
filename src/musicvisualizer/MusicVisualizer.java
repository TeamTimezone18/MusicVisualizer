package musicvisualizer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * @author Tristan Hunter
 */
public class MusicVisualizer extends Application 
{
    
    @Override
    public void start(Stage stage) throws Exception 
    {
        Parent root = FXMLLoader.load(getClass().getResource("FXMLDocument.fxml"));
        
        Scene scene = new Scene(root);
        
        scene.getStylesheets().add(getClass().getResource("/Resource/GUIStyle.css").toExternalForm());
        
        stage.setScene(scene);
        stage.setMinHeight(450);
        stage.setMinWidth(525);
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
