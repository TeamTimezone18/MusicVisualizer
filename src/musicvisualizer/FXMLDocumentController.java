package musicvisualizer;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
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
    private Button shuffleButton;
    @FXML
    private Button repeatButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button trackUpButton;
    @FXML
    private Button trackDownButton;
    @FXML
    private ImageView albumImage;
    @FXML
    private Slider timeSlider;
    @FXML
    private Slider volumeSlider;
    @FXML
    private Label currentTimeLabel;
    @FXML
    private Label durationLabel;
    @FXML
    private Label titleLabel;
    @FXML
    private Label artistLabel;
    @FXML
    private Label albumLabel;

    @FXML
    private void handleAddButtonAction(ActionEvent event) 
    {    
        addSelectedFilesToPlaylist();
    }
    
    @FXML
    private void handleDeleteButtonAction(ActionEvent event) 
    {
        deleteSelectedFilesFromPlaylist();
    }
    
    @FXML
    private void handleSkipButtonAction(ActionEvent event) 
    {
        playNextTrack();
    }

    @FXML
    private void handlePrevButtonAction(ActionEvent event) 
    {
        playLastTrack();
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
    }
    
    
    @FXML
    private void handleShuffleButtonAction(ActionEvent event) 
    {
        if (playlist.mode.toString().equals("SHUFFLE"))
        {
            playlist.setMode("NORMAL");
        }
        else
        {
            playlist.setMode("SHUFFLE");
        }
    }
    
    @FXML
    private void handleRepeatButtonAction(ActionEvent event) 
    {
        
        if (playlist.mode.toString().equals("REPEAT"))
        {
            playlist.setMode("NORMAL");
        }
        else
        {
            playlist.setMode("REPEAT");
        }
    }

    @FXML
    private void handlePlayButtonAction(ActionEvent event) 
    {
        togglePlayPause();
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
    
    @FXML
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
                addSelectedFilesToPlaylist();
            }
            else
            {
                // if focus is off lists, skip to next track
                playNextTrack();
            }
         }
        else if (event.getCode() == KeyCode.BACK_SPACE)
            {
                // Previous track
                playLastTrack();
                event.consume();
            }     
        else if (event.getCode() == KeyCode.SPACE)
            {
                // Play/pause
                togglePlayPause();
            }        
        else if (event.getCode() == KeyCode.DELETE && playList.isFocused())
            {
                // Delete selected tracks if playlist is focused
                deleteSelectedFilesFromPlaylist();
            }
        else if (event.getCode() == KeyCode.F1)
            {
                // set volume down
                volumeSlider.setValue(volumeSlider.getValue()-5);
            }
        else if (event.getCode() == KeyCode.F2)
            {
                // set volume up
                volumeSlider.setValue(volumeSlider.getValue()+5);
            }
        else if(!timeSlider.disabledProperty().getValue())
            {
                if (event.getCode() == KeyCode.RIGHT)
                {
                    // Seek forward
                    timeSlider.setValue(timeSlider.getValue()+1);
                    player.setTime(timeSlider.getValue());
                    event.consume(); 
                }
                else if (event.getCode() == KeyCode.LEFT)
                {
                    // Seek back
                    timeSlider.setValue(timeSlider.getValue()-1);
                    player.setTime(timeSlider.getValue());
                    event.consume(); 
                }
            }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {
        player = new Player();
        
        initializeFileExplorer();

        initializePlaylist();
        
        initializeTimeSlider();
        
        initializeVolumeSlider();
        
        configureFocusSettings();
        
        initializeChart();
        
        // Initialize label and image binding to track metadata properties
        artistLabel.textProperty().bind(player.track.artist);
        albumLabel.textProperty().bind(player.track.album);
        titleLabel.textProperty().bind(player.track.title);
        durationLabel.textProperty().bind(player.track.durationString);
        currentTimeLabel.textProperty().bind(player.track.playbackTime);
        albumImage.imageProperty().bind(player.track.albumImage);
        
        setupFileDragDrop();
        
    }    

    private void initializeFileExplorer()
    {
        // Create FileExplorer object
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
    }
    
    private void initializePlaylist()
    {
        // Create Playlist object
        playlist = new Playlist();
        
        // Configure and populate playlist listview
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
    }
    
    private void initializeTimeSlider()
    {
        timeSlider.disableProperty().set(true);
        
        timeSlider.setOnMousePressed(event -> {
            player.mp.pause();
            player.setTime(timeSlider.getValue());
        });
        timeSlider.setOnMouseDragged(event -> {
            player.mp.pause();
            player.setTime(timeSlider.getValue());
        });
        timeSlider.setOnMouseReleased(event -> {
            player.mp.play();
        });
    }
    
    private void initializeVolumeSlider()
    {
        volumeSlider.setValue(100);
        
        volumeSlider.valueProperty().addListener(new InvalidationListener() {
            public void invalidated(Observable ov) {
                   if (player.mp != null)
                   {
                       player.mp.setVolume(volumeSlider.getValue() / 100.0);
                   }
            }
        });
    }
    
        private void initializeChart()
    {
        // setup the animation timeline and data for the chart
        chart.setAnimated(false);
        chart.setHorizontalGridLinesVisible(false);
        chart.setVerticalGridLinesVisible(false);
        chart.setLegendVisible(false);
        yAxis.setOpacity(0);
        
        Timeline animation = new Timeline();
        animation.getKeyFrames().add(new KeyFrame(Duration.millis(UPDATERATE), new    EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent actionEvent) {
              // Update graph data
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
    }
    
    private void configureFocusSettings()
    {
        // Give user a way to take focus off of lists
        anchorPane.setOnMouseClicked(event ->
        {
            anchorPane.requestFocus();
        });
        
        // Don't set focus with tab or arrow keys on any controls except lists or main pane
        volumeSlider.setFocusTraversable(false);
        timeSlider.setFocusTraversable(false);
        addButton.setFocusTraversable(false);
        deleteButton.setFocusTraversable(false);
        trackUpButton.setFocusTraversable(false);
        trackDownButton.setFocusTraversable(false);
        playButton.setFocusTraversable(false);
        skipButton.setFocusTraversable(false);
        prevButton.setFocusTraversable(false);
        repeatButton.setFocusTraversable(false);
        shuffleButton.setFocusTraversable(false);
        filePathList.setFocusTraversable(false);
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
        for (String track : playlist.getNames())
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
        albumImage.setOpacity(0); // hide so default metadata so it doesn't flash during update
        artistLabel.setOpacity(0);
        albumLabel.setOpacity(0);
        titleLabel.setOpacity(0);
        player.playNew(newFile);
        updatePlaylistCurTrackItem();
        player.mp.setVolume(volumeSlider.getValue() / 100.0);
        timeSlider.disableProperty().set(false);
        player.mp.setAudioSpectrumInterval((1.0/UPDATERATE));
        player.mp.setAudioSpectrumNumBands(NUMBARS);
        
        player.mp.currentTimeProperty().addListener(new InvalidationListener() 
        {
            public void invalidated(Observable ov) 
            {
                if (!timeSlider.isValueChanging())
                {
                    timeSlider.setValue(player.track.progress.doubleValue());
                }
                if (albumImage.opacityProperty().getValue() == 0)
                {
                    // When cur time starts changing, metadata should be loaded,
                    // so make it visible
                    albumImage.setOpacity(100);
                    artistLabel.setOpacity(100);
                    albumLabel.setOpacity(100);
                    titleLabel.setOpacity(100);
                }
            }
        });
        
        player.mp.setOnEndOfMedia(new Runnable() {
            public void run() 
            {
                playNextTrack();
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
                   addSelectedFilesToPlaylist();
                }
                event.setDropCompleted(success);

                event.consume();
             }
        });
    }
    
    private void addSelectedFilesToPlaylist()
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
            playlist.addTracks(newfiles);
            updatePlaylistListView();
        }
    }
    
    private void deleteSelectedFilesFromPlaylist()
    {
        // Get selected items and call playlist delete method, then update GUI
        ObservableList<Integer> playlistSelection = playList.getSelectionModel().getSelectedIndices();
        playlist.delTracks(playlistSelection);
        updatePlaylistListView();
        
    }
    
    private void playNextTrack()
    {
        // Pass next file from playlist to player
        File newTrack = playlist.getNext();
        
        if (newTrack != null)
        {
            playNewTrack(newTrack);
        }
        
    }
 
    private void playLastTrack()
    {
        // Pass last file from playlist history to player
        File newTrack = playlist.getLast();
        
        if (newTrack != null)
        {
            playNewTrack(newTrack);
        }
        
    }
    
    private void togglePlayPause()
    {
        if (player.mp != null)
        {
            player.playPause();
        }
    }
    


    
}
