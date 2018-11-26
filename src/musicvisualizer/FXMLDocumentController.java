package musicvisualizer;

import java.awt.Color;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;
import javafx.util.Duration;

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
    
    ObservableList<CustomListViewItem> playListData = FXCollections.observableArrayList();
    ObservableList<CustomListViewItem> fileListViewData = FXCollections.observableArrayList();
    Integer numDir;
    
    CategoryAxis xAxis = new CategoryAxis();
    NumberAxis yAxis = new NumberAxis();
    BarChart.Series spectrumDataSeries = new BarChart.Series();
    final int NUMBARS = 128;  // number of bars in the barchart
    final int UPDATERATE = 20; // refresh rate in hertz
    
    @FXML
    private Label label;
    @FXML
    private ListView fileList;
    @FXML
    private ListView<String> filePathList;
    @FXML
    private ListView playList;
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
        // If index selected in file explorer is a valid file, add to new list for passing to playlist
        ObservableList<Integer> fileSelection = fileList.getSelectionModel().getSelectedIndices();
        
        // Listview shows folders first, so subtract number of 
        // folders from selected indices to get actual selected file indices  
        Integer actualIndex;
        ObservableList<Integer> subFileSelection = FXCollections.observableArrayList();
        for (Integer index : fileSelection)
        {
            actualIndex = index - numDir;
            // only add files, not directories
            if (actualIndex >= 0)
            {
                subFileSelection.add(actualIndex);                
            }
        }
        ObservableList<File> newfiles = fileExplorer.getFilesAtIndices(subFileSelection);
      
        // If any files are selected, add them to end of playlist
        if (newfiles.size() > 0)
        {
            playlist.AddTracks(newfiles);
            updatePlaylistListView();
        }
    }
    
    @FXML
    private void handleDeleteButtonAction(ActionEvent event) 
    {
        // Get selected items and call playlist delete method, then update GUI
        ObservableList<Integer> playlistSelection = playList.getSelectionModel().getSelectedIndices();
        playlist.delTracks(playlistSelection);
        updatePlaylistListView();
        
        label.setText("delete");
    }
    
    @FXML
    private void handleSkipButtonAction(ActionEvent event) 
    {
        // Pass next file from playlist to player
        File newTrack = playlist.getNext();
        
        if (newTrack != null)
        {
            playNewTrack(newTrack);
        }
        
        label.setText("skip");
    }

    @FXML
    private void handlePrevButtonAction(ActionEvent event) 
    {
        // Pass last file from playlist history to player
        File newTrack = playlist.getLast();
        
        if (newTrack != null)
        {
            playNewTrack(newTrack);
        }
        
        label.setText("previous");
    }
    
    @FXML
    private void handleTrackUpButtonAction(ActionEvent event) 
    {
        int selectedIndex = playList.getSelectionModel().getSelectedIndex();
        if (selectedIndex > 0)
        {
            playlist.moveTrackUp(selectedIndex);
            updatePlaylistListView();
            playList.getSelectionModel().select(selectedIndex-1);
        }
        
        label.setText("move track up");
    }
    
    @FXML
    private void handleTrackDownButtonAction(ActionEvent event) 
    {
        int selectedIndex = playList.getSelectionModel().getSelectedIndex();
        
        if (selectedIndex < 0)
        {
            return;
        }
       
        if (selectedIndex < playList.getItems().size()-1)
        {
            playlist.moveTrackDown(selectedIndex);
            updatePlaylistListView();
            playList.getSelectionModel().select(selectedIndex+1);
        }
        label.setText("move track down");
    }
    
    
    @FXML
    private void handleShuffleButtonAction(ActionEvent event) 
    {
        if (playlist.mode.toString().equals("SHUFFLE"))
        {
            label.setText("shuffle off");
            playlist.setMode("NORMAL");
        }
        else
        {
            label.setText("shuffle on");
            playlist.setMode("SHUFFLE");
        }
    }
    
    @FXML
    private void handleRepeatButtonAction(ActionEvent event) 
    {
        
        if (playlist.mode.toString().equals("REPEAT"))
        {
            label.setText("repeat off");
            playlist.setMode("NORMAL");
        }
        else
        {
            label.setText("repeat on");
            playlist.setMode("REPEAT");
        }
    }

        @FXML
    private void handlePlayButtonAction(ActionEvent event) 
    {
        System.out.println("play/pause button");    
        if (player.mp != null)
        {
            player.PlayPause();
        }
    }
    
    @FXML
    private void handlePlaylistDblClickAction(MouseEvent event) 
    {
        if(event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) 
        {
            int selIndex = playList.getSelectionModel().getSelectedIndex();
            if (selIndex >= 0)
            {
                File newTrack = playlist.tracks.get(selIndex);
                playlist.setCurTrack(selIndex);
                playNewTrack(newTrack);
            }
        }    
    }
    
    @FXML
    private void handlePathListDblClickAction(MouseEvent event) 
    {
        if(event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) 
        {
            int selIndex = filePathList.getSelectionModel().getSelectedIndex();
            if (selIndex >= 0)
            {
                // Go to selected path in FileExplorer and update listviews
                fileExplorer.upDirectory(-1 + filePathList.getItems().size() - filePathList.getSelectionModel().getSelectedIndex());
                fileListViewData.clear();
                updateFileExplorerListViews();
            }
        }    
    }
    
    @FXML
    private void handleFileListDblClickAction(MouseEvent event) 
    {
        if(event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) 
        {
            // Ignore double clicks on files
            // If a directory is selected, open it and update file explorer
            int selIndex = fileList.getSelectionModel().getSelectedIndex();
            if (selIndex >= 0 && selIndex < numDir)
            {
                fileExplorer.openDirectory(fileExplorer.childDirs.get(fileList.getSelectionModel().getSelectedIndex()));
                fileListViewData.clear();
                updateFileExplorerListViews();
            }
        }    
    }
    

    public void keyListener(KeyEvent event)
    {
        if(event.getCode() == KeyCode.ENTER) 
        {
            if (playList.isFocused())
            {
                // Play selected track -- same as playlist dbl click
                int selIndex = playList.getSelectionModel().getSelectedIndex();
                if (selIndex >= 0)
                {
                    File newTrack = playlist.tracks.get(selIndex);
                    playlist.setCurTrack(selIndex);
                    playNewTrack(newTrack);
                } 
            }
            else if (fileList.isFocused())
            {   
                // if focused on file explorer add the selected tracks
                handleAddButtonAction(null);
            }
            else
            {
                // if focus is off lists, skip to next track
                handleSkipButtonAction(null);
            }
         }
        else if (event.getCode() == KeyCode.BACK_SPACE)
            {
                // Previous track
                handlePrevButtonAction(null);
                event.consume();
            }     
        else if (event.getCode() == KeyCode.SPACE)
            {
                // Play/pause
                handlePlayButtonAction(null);
            }        
        else if (event.getCode() == KeyCode.DELETE && playList.isFocused())
            {
                // Delete selected tracks if playlist is focused
                handleDeleteButtonAction(null);
            }
        else if (event.getCode() == KeyCode.F1)
            {
                // set volume down
                VolumeSlider.setValue(VolumeSlider.getValue()-5);
            }
        else if (event.getCode() == KeyCode.F2)
            {
                // set volume up
                VolumeSlider.setValue(VolumeSlider.getValue()+5);
            }
        else if(!TimeSlider.disabledProperty().getValue())
            {
                if (event.getCode() == KeyCode.RIGHT)
                {
                    // Seek forward
                    TimeSlider.setValue(TimeSlider.getValue()+1);
                    player.setTime(TimeSlider.getValue());
                    event.consume(); 
                }
                else if (event.getCode() == KeyCode.LEFT)
                {
                    // Seek back
                    TimeSlider.setValue(TimeSlider.getValue()-1);
                    player.setTime(TimeSlider.getValue());
                    event.consume(); 
                }
            }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {
        // Initialize FileExplorer
        fileExplorer = new FileExplorer();
   
        // Configure and populate file explorer listviews
        fileList.setItems(fileListViewData);
        fileList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        fileList.setCellFactory(new Callback<ListView<CustomListViewItem>, ListCell<CustomListViewItem>>() {
            @Override
            public ListCell<CustomListViewItem> call(ListView<CustomListViewItem> listView)
            {
                return new CustomListViewCell();
            }
        });
        updateFileExplorerListViews();

        // Initialize Playlist object and configure listview
        playlist = new Playlist();
        playList.setItems(playListData);
        playList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        playList.setCellFactory(new Callback<ListView<CustomListViewItem>, ListCell<CustomListViewItem>>() {
            @Override
            public ListCell<CustomListViewItem> call(ListView<CustomListViewItem> listView)
            {
                return new CustomListViewCell();
            }
        });
        updatePlaylistListView();
            
        // Initialize Player object
        player = new Player();
        
        // Initialize slider listeners
        TimeSlider.disableProperty().set(true);
        TimeSlider.setOnMousePressed(event -> {
            player.mp.pause();
            player.setTime(TimeSlider.getValue());
        });
        TimeSlider.setOnMouseDragged(event -> {
            player.mp.pause();
            player.setTime(TimeSlider.getValue());
        });
        TimeSlider.setOnMouseReleased(event -> {
            player.mp.play();
        });
        
        VolumeSlider.setValue(100);
        VolumeSlider.valueProperty().addListener(new InvalidationListener() {
            public void invalidated(Observable ov) {
                   if (player.mp != null)
                   {
                       player.mp.setVolume(VolumeSlider.getValue() / 100.0);
                   }
            }
        });
        
        // give user a way to take focus off of lists
        anchorPane.setOnMouseClicked(event ->
        {
            anchorPane.requestFocus();
        });
        
        // don't set focus with tab or arrow keys on any controls except lists or main pane
        VolumeSlider.setFocusTraversable(false);
        TimeSlider.setFocusTraversable(false);
        addButton.setFocusTraversable(false);
        DeleteButton.setFocusTraversable(false);
        TrackUpButton.setFocusTraversable(false);
        TrackDownButton.setFocusTraversable(false);
        playButton.setFocusTraversable(false);
        skipButton.setFocusTraversable(false);
        prevButton.setFocusTraversable(false);
        RepeatButton.setFocusTraversable(false);
        ShuffleButton.setFocusTraversable(false);
        filePathList.setFocusTraversable(false);
        
        // setup the animation timeline and data for the chart
        // chart aesthetic settings could be in css file
        chart.setAnimated(false);
        chart.setHorizontalGridLinesVisible(false);
        chart.setVerticalGridLinesVisible(false);
        chart.setLegendVisible(false);
        yAxis.setOpacity(0);
        
        Timeline animation = new Timeline();
        animation.getKeyFrames().add(new KeyFrame(Duration.millis(UPDATERATE), new    EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent actionEvent) {
                
              //update graph
              Integer index = 0;
              for (Float value : player.track.spectrumData)
              {
                  spectrumDataSeries.getData().set(index,(new XYChart.Data(index.toString(), value.floatValue())));
                  index++;
              }
              
        }
        }));
        animation.setCycleCount(Animation.INDEFINITE);
        animation.play();
        
        
        float init = 0;
        for (Integer i =0; i<NUMBARS;i++ )
        {
            spectrumDataSeries.getData().add(new XYChart.Data(i.toString(), init));
        }
        chart.getData().addAll(spectrumDataSeries);
        
        // Initialize bindings to track properties
        ArtistLabel.textProperty().bind(player.track.artist);
        AlbumNameLabel.textProperty().bind(player.track.album);
        TrackNameLabel.textProperty().bind(player.track.title);
        DurationLabel.textProperty().bind(player.track.durationString);
        CurrentTimeLabel.textProperty().bind(player.track.playbackTime);
        AlbumImage.imageProperty().bind(player.track.albumImage);
        
        setupFileDragDrop();
        
    }    

    private void updateFileExplorerListViews()
    {
        // Create custom items for directorys and add to fileListViewData 
        numDir = 0;
        for (String dir : fileExplorer.getAllDirNames())
        {
                CustomListViewItem ci = new CustomListViewItem();
                ci.setString(dir);
                ci.setLabelGlyph("/Resource/foldericon.gif");
                fileListViewData.add(ci);
                numDir++;
        }
        //... and repeat for each file
        for (String filename : fileExplorer.getAllFileNames())
        {
                CustomListViewItem ci = new CustomListViewItem();
                ci.setString(filename);
                ci.setLabelGlyph("/Resource/musicicon.gif");
                fileListViewData.add(ci);
        }
        // Populate path listview with items and scroll to end
        filePathList.setItems(fileExplorer.getPathList());
        filePathList.scrollTo(fileExplorer.getPathList().size());
    }
    
    private void updatePlaylistListView()
    {
        playListData.clear();
        for (String track : playlist.GetNames())
        {
            CustomListViewItem ci = new CustomListViewItem();
            ci.setString(track);
            playListData.add(ci);
        }
        updatePlaylistCurTrackItem();
    }
    
    private void updatePlaylistCurTrackItem()
    {
        int ctr = 0;
        for (CustomListViewItem item : playListData)
        {
            if (ctr != playlist.curTrackIndex)
            {
                item.setNormal();
            }
            else
            {
                item.setBold();
            }
            ctr++;
        }
    }
    
    private void playNewTrack(File newFile)
    {
        AlbumImage.setOpacity(0); // hide so default image doesn't flash during update
        player.PlayNew(newFile);
        updatePlaylistCurTrackItem();
        player.mp.setVolume(VolumeSlider.getValue() / 100.0);
        TimeSlider.disableProperty().set(false);
        player.mp.setAudioSpectrumInterval((1.0/UPDATERATE));
        player.mp.setAudioSpectrumNumBands(NUMBARS);
        
        player.mp.currentTimeProperty().addListener(new InvalidationListener() 
        {
            public void invalidated(Observable ov) 
            {
                if (!TimeSlider.isValueChanging())
                {
                    TimeSlider.setValue(player.track.progress.doubleValue());
                }
                if (AlbumImage.opacityProperty().getValue() == 0)
                {
                    // When cur time starts changing, metadata should be loaded,
                    // so make it visible
                    AlbumImage.setOpacity(100);
                }
            }
        });
        
        player.mp.setOnEndOfMedia(new Runnable() {
            public void run() 
            {
                handleSkipButtonAction(null);
            }
       });
        
    }
    
    private void setupFileDragDrop()
    {
        fileList.setOnDragDetected(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                Dragboard db = fileList.startDragAndDrop(TransferMode.ANY);
                ClipboardContent content = new ClipboardContent();
                content.putString(fileList.getSelectionModel().toString());
                db.setContent(content);
                event.consume();
            }
        });
        
        
        playList.setOnDragOver(new EventHandler<DragEvent>() {
            public void handle(DragEvent event) {
                // If file list data is dragged over playlist, accept the drag
                if (event.getGestureSource() != playList && event.getDragboard().hasString()) {
                    event.acceptTransferModes(TransferMode.MOVE);
                }

                event.consume();
            }
        });
        
        playList.setOnDragDropped(new EventHandler<DragEvent>() {
            public void handle(DragEvent event) {
                // if there is a file selection string data on dragboard, add selected files
                Dragboard db = event.getDragboard();
                boolean success = false;
                if (db.hasString()) {
                   success = true;
                   handleAddButtonAction(null);
                }
                event.setDropCompleted(success);

                event.consume();
             }
        });
    }
    
}
