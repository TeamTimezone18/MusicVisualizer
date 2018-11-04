/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package musicvisualizer;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

/**
 * @author Benjamin Wasserman
 * @author Dan Sharp
 * @author Tristan Hunter
 */
public class FXMLDocumentController implements Initializable 
{    
    FileExplorer fileExplorer;
    Player player;
    Playlist playlist;
    
    @FXML
    private Label label;
    @FXML
    private ListView<String> fileList;
    @FXML
    private ListView<String> playList;
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private Button prevButton;
    @FXML
    private Button playButton;
    @FXML
    private Button skipButton;
    @FXML
    private Button addButton;
    @FXML
    private BarChart<String, Number> chart;
    
    @FXML
    private void handleSkipButtonAction(ActionEvent event) 
    {
        System.out.println("Skip button");
        label.setText("skip");
    }

    @FXML
    private void handlePrevButtonAction(ActionEvent event) 
    {
        System.out.println("Previous button");
        label.setText("previous");
    }
    
    @FXML
    private void handlePlayButtonAction(ActionEvent event) 
    {
        System.out.println("play/pause button");    
        player.PlayPause();
    }
    
    @FXML
    private void handleAddButtonAction(ActionEvent event) 
    {    
        System.out.println("add to playlist");
        ObservableList<Integer> fileSelection = fileList.getSelectionModel().getSelectedIndices();
        ObservableList<File> newfiles = fileExplorer.GetFilesAtIndices(fileSelection);
        playlist.AddTracks(newfiles);
        playList.getItems().addAll(fileExplorer.GetNamesAtIndices(fileSelection));
    }
    
    @FXML
    private void handlePlaylistDblClickAction(MouseEvent event) 
    {
        if(event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) 
        {
            player.PlayNew(playlist.tracks.get(playList.getSelectionModel().getSelectedIndex()));
        }    
    }
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {
        
        // Initialize FileExplorer object and populate listview
        fileExplorer = new FileExplorer();
        fileList.setItems(fileExplorer.GetAllNames());

        // Initialize Playlist object and make listview empty
        playlist = new Playlist();
            
            
        // Initialize Player object
        player = new Player();
            
    }    

    
}
