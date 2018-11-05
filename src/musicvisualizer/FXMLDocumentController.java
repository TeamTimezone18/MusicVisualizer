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
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
    private Button ShuffleButton;
    @FXML
    private Button RepeatButton;
    @FXML
    private Button DeleteButton;
    @FXML
    private Button TrackUpButton;
    @FXML
    private Button TrackDownButton;
    @FXML
    private ImageView AlbumImage;
    @FXML
    private Slider TimeSlider;
    @FXML
    private Slider VolumeSlider;
    @FXML
    private Label CurrentTimeLabel;
    @FXML
    private Label DurationLabel;
    @FXML
    private Label TrackNameLabel;
    @FXML
    private Label ArtistLabel;
    @FXML
    private Label AlbumNameLabel;

    @FXML
    private void handleAddButtonAction(ActionEvent event) 
    {    
        System.out.println("add to playlist");
        ObservableList<Integer> fileSelection = fileList.getSelectionModel().getSelectedIndices();
        ObservableList<File> newfiles = fileExplorer.GetFilesAtIndices(fileSelection);
        playlist.AddTracks(newfiles);
        playList.getItems().setAll(playlist.GetNames());
    }
    
    @FXML
    private void handleDeleteButtonAction(ActionEvent event) 
    {
        // Get selected items and call playlist delete method, then update GUI
        // ObservableList<Integer> playlistSelection = playList.getSelectionModel().getSelectedIndices();
        // playlist.delTracks(playlistSelection)
        // playList.getItems().setAll(playlist.GetNames());
        
        System.out.println("Delete button");
        label.setText("delete");
    }
    
    @FXML
    private void handleSkipButtonAction(ActionEvent event) 
    {
        // Pass next file from playlist to player
        // player.playNew(playlist.getNext());
        
        System.out.println("Skip button");
        label.setText("skip");
    }

    @FXML
    private void handlePrevButtonAction(ActionEvent event) 
    {
        // Pass last file from playlist history to player
        // player.playNew(playlist.getLast());
        
        System.out.println("Previous button");
        label.setText("previous");
    }
    
    @FXML
    private void handleTrackUpButtonAction(ActionEvent event) 
    {
        System.out.println("Track up button");
        label.setText("move track up");
    }
    
    @FXML
    private void handleTrackDownButtonAction(ActionEvent event) 
    {
        System.out.println("Track down button");
        label.setText("move track down");
    }
    
    
    @FXML
    private void handleShuffleButtonAction(ActionEvent event) 
    {
        System.out.println("Shuffle button");
        label.setText("shuffle");
    }
    
    @FXML
    private void handleRepeatButtonAction(ActionEvent event) 
    {
        System.out.println("Repeat button");
        label.setText("repeat");
    }

        @FXML
    private void handlePlayButtonAction(ActionEvent event) 
    {
        System.out.println("play/pause button");    
        player.PlayPause();
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
        
        // Initialize slider listeners
        TimeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            CurrentTimeLabel.setText(Double.toString(newValue.intValue()));
        });
            
        VolumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            label.setText("Vol: " + Double.toString(newValue.intValue()));
        });
        
        // Initialize metadata image
        File file = new File("src/Vinyl.gif");
        Image image = new Image(file.toURI().toString());
        AlbumImage.setImage(image);
        
       // TODO: Initialize chart
        
       // TODO: Initialize listener on a player attribute to update chart, time labels/slider -- not sure how to do this
       
        
    }    

    
}
