package musicvisualizer;

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
 * 
 * This class manipulates the FXML GUI, FileExplorer, Player, and
 * Playlist objects according to user input from the GUI
 * 
 */
public class FXMLDocumentController implements Initializable 
{    
    FileExplorer fileExplorer;
    Player player;
    Playlist playlist;
    
    ObservableList<CustomListViewItem> playListData = FXCollections.observableArrayList();
    ObservableList<CustomListViewItem> fileListViewData = FXCollections.observableArrayList();
    Integer numDir;
    
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
    private ImageView volumeIcon;
    @FXML
    private AnchorPane volSliderPane;

    
    /**
     *  Add files selected in file explorer to playlist
     * 
     *  ASSOCIATED REQUIREMENTS:
     *  4.1.1	User can press a button to add selected tracks
     */
    @FXML
    private void handleAddButtonAction(ActionEvent event) 
    {    
        addSelectedFilesToPlaylist();
    }
    
    
    /**
     *  Delete files selected in playlist
     * 
     *  ASSOCIATED REQUIREMENTS:
     *  4.2.1	User can press a button to delete selected tracks
     */
    @FXML
    private void handleDeleteButtonAction(ActionEvent event) 
    {
        deleteSelectedFilesFromPlaylist();
    }
    
    /**
     *  Get new track from the playlist and start playing it
     * 
     *  ASSOCIATED REQUIREMENTS:
     *  5.2.1	User can click a button to start playing the next file from playlist
     */
    @FXML
    private void handleSkipButtonAction(ActionEvent event) 
    {
        playNextTrack();
    }

    
    /**
     *  Get last file from playlist and start playing it
     * 
     *  ASSOCIATED REQUIREMENTS:
     *  5.3.1	User can click a button to start playing the last file played
     */
    @FXML
    private void handlePrevButtonAction(ActionEvent event) 
    {
        playLastTrack();
    }
    
    
    /**
     *  Move one selected track in the playlist up one index
     * 
     *  ASSOCIATED REQUIREMENTS:
     *  4.3.1	User can move a selected track up the list using a button
     */
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
    
    
    /**
     *  Move one selected track in the playlist down one index
     * 
     *  ASSOCIATED REQUIREMENTS:
     *  4.3.2	User can move a selected track down the list using a button
     */
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
    
    
    /**
     *  Set playlist mode to shuffle and update buttons
     * 
     *  ASSOCIATED REQUIREMENTS:
     *  4.4.0	User can configure a playlist mode (sequential/shuffle/repeat)
     *  4.4.2	User can click a button to switch to shuffle mode
     *  4.4.4	Shuffle and repeat buttons indicate current mode
     *  4.4.5	Turning off shuffle or repeat mode returns the playlist to sequential mode
     */
    @FXML
    private void handleShuffleButtonAction(ActionEvent event) 
    {
        if (playlist.mode.toString().equals("SHUFFLE"))
        {
            playlist.setMode("NORMAL");
            shuffleButton.setStyle("-fx-border-color: #000000");
        }
        else
        {
            playlist.setMode("SHUFFLE");
            shuffleButton.setStyle("-fx-border-color: #ED5402");
            repeatButton.setStyle("-fx-border-color: #000000");  
        }
    }
    
    
    /**
     *  Set playlist mode to repeat and update buttons
     * 
     *  ASSOCIATED REQUIREMENTS:
     *  4.4.0	User can configure a playlist mode (sequential/shuffle/repeat)
     *  4.4.3	User can click a button to switch to repeat mode
     *  4.4.4	Shuffle and repeat buttons indicate current mode
     *  4.4.5	Turning off shuffle or repeat mode returns the playlist to sequential mode
     */
    @FXML
    private void handleRepeatButtonAction(ActionEvent event) 
    {
        
        if (playlist.mode.toString().equals("REPEAT"))
        {
            playlist.setMode("NORMAL");
            repeatButton.setStyle("-fx-border-color: #000000");
        }
        else
        {
            playlist.setMode("REPEAT");
            repeatButton.setStyle("-fx-border-color: #ED5402");
            shuffleButton.setStyle("-fx-border-color: #000000");            
        }
    }

    
    /**
     *  Toggle playback paused/playing
     * 
     *  ASSOCIATED REQUIREMENTS:
     *  6.1.0	User can click a button to toggle between paused and playing
     */
    @FXML
    private void handlePlayButtonAction(ActionEvent event) 
    {
        togglePlayPause();
    }
    
    
    /**
     *  Get the selected song from the playlist and start playback
     * 
     *  ASSOCIATED REQUIREMENTS:
     *  5.1.1	User can double click a file in the playlist to start playback
     */
    @FXML
    private void handlePlaylistDblClickAction(MouseEvent event) 
    {
        if(event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) 
        {
            int selIndex = playList.getSelectionModel().getSelectedIndex();
            if (selIndex >= 0)
            {
                File newTrack = playlist.tracks.get(selIndex);
                if (newTrack.exists())
                {
                    playlist.setCurTrack(selIndex);
                    playNewTrack(newTrack);
                }
                else
                {   // Remove track that is missing from it's original location
                    playlist.delTracks(playList.getSelectionModel().getSelectedIndices());
                    updatePlaylistListView();
                }
            }
        }    
    }
    
    
    /**
     *  Update the file explorer ListViews with the contents of the selected parent directory
     * 
     *  ASSOCIATED REQUIREMENTS:
     *  2.1.0	User can go up directories by double-clicking between “\” delimiters in the displayed path
     */
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
    
    
    /**
     *  Update the file explorer ListViews with the contents of the selected child directory
     * 
     *  ASSOCIATED REQUIREMENTS:
     *   1.4.0	User can double-click a child directory in the list to update the list with it’s contents
     */
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
                File selectedDir = fileExplorer.childDirs.get(fileList.getSelectionModel().getSelectedIndex());
                if (selectedDir.exists())
                {
                    fileExplorer.openDirectory(selectedDir);
                }
                else
                {
                    fileExplorer.openDirectory(fileExplorer.curDir);
                }
                fileListViewData.clear();
                updateFileExplorerListViews();
            }
        }    
    }
    
    
    /**
     *  Key press listener for all GUI components
     */
    @FXML
    public void keyListener(KeyEvent event)
    {
        if(event.getCode() == KeyCode.ENTER) 
        {
            if (playList.isFocused())
            {
                /**
                 * ASSOCIATED REQUIREMENT:
                 * 5.1.2   User can press ENTER when the playlist has focus to play the chosen file
                 */
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
                /**
                 * ASSOCIATED REQUIREMENT:
                 * 4.1.2    User can press ENTER while the file explorer is focused to add tracks
                 */
                addSelectedFilesToPlaylist();
            }
         }
        else if (event.getCode() == KeyCode.END)
        {
            /**
             * ASSOCIATED REQUIREMENT:
             * 5.2.2    User can press END to start playing the next file from playlist
             */
            playNextTrack();
            event.consume();
        } 
        else if (event.getCode() == KeyCode.BACK_SPACE)
        {
            /**
             * ASSOCIATED REQUIREMENT:
             * 5.3.2    User can press BACKSPACE to start playing the last file played
             */
            playLastTrack();
            event.consume();
        }     
        else if (event.getCode() == KeyCode.SPACE)
        {
            /**
             * ASSOCIATED REQUIREMENT:
             * 6.2.0    User can press SPACE to toggle between paused and playing
             */
            togglePlayPause();
        }        
        else if (event.getCode() == KeyCode.DELETE && playList.isFocused())
        {
            /**
             * ASSOCIATED REQUIREMENT:
             * 4.2.2    User can press DELETE while the playlist is focused to delete tracks
             */
            deleteSelectedFilesFromPlaylist();
        }
        else if (event.getCode() == KeyCode.F1)
        {
            /**
             * ASSOCIATED REQUIREMENT:
             * 7.2.0    User can press F1 to decrease volume and update slider
             */
            volumeSlider.setValue(volumeSlider.getValue()-5);
        }
        else if (event.getCode() == KeyCode.F2)
        {
            /**
             * ASSOCIATED REQUIREMENT:
             * 7.3.0    User can press F2 to increase volume and update slider
             */
            volumeSlider.setValue(volumeSlider.getValue()+5);
        }
        else if(!timeSlider.disabledProperty().getValue())
        {
            if (event.getCode() == KeyCode.RIGHT)
            {
                /**
                * ASSOCIATED REQUIREMENT:
                * 8.2.0	User can press RIGHT to increase playback time and update slider
                * 8.5.0	Hotkeys do nothing until a file is played
                */
                timeSlider.setValue(timeSlider.getValue()+1);
                player.setTime(timeSlider.getValue());
                event.consume(); 
            }
            else if (event.getCode() == KeyCode.LEFT)
            {
                /**
                * ASSOCIATED REQUIREMENT:
                * 8.3.0	User can press LEFT to decrease playback time and update slider
                * 8.5.0	Hotkeys do nothing until a file is played
                */
                timeSlider.setValue(timeSlider.getValue()-1);
                player.setTime(timeSlider.getValue());
                event.consume(); 
            }
        }
    }

    
    /**
     * Instantiate supporting objects and initialize GUI
     */
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
        
        /**
         * Setup label and image binding to track metadata properties
         * 
         * ASSOCIATED REQUIREMENTS:
         * 9.1.0    Track title is displayed
         * 9.2.0    Artist name is displayed
         * 9.3.0    Album name is displayed
         * 9.4.1    Duration is shown at the end of playback time slider
         * 9.5.1    Current playback time is shown by slider position
         */
        artistLabel.textProperty().bind(player.track.artist);
        albumLabel.textProperty().bind(player.track.album);
        titleLabel.textProperty().bind(player.track.title);
        durationLabel.textProperty().bind(player.track.durationString);
        currentTimeLabel.textProperty().bind(player.track.playbackTime);
        albumImage.imageProperty().bind(player.track.albumImage);
        
        setupFileDragDrop();
        
    }    

    
    /**
     * Instantiate a FileExplorer with a corresponding ListView
     * 
     * ASSOCIATED REQUIREMENTS:
     * 1.0.0	Display the contents of a directory in an interactive list
     * 1.3.0	User can select multiple file in the list
     * 2.0.0 	Display the full path of the parent directory of the contents list
     */
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
    
    
    /**
     * Instantiate a Playlist with a corresponding ListView
     * 
     * ASSOCIATED REQUIREMENTS:
     * 4.0.0	Display a user-built list of WAV and MP3 audio files
     * 4.2.3	User can select [and delete] multiple tracks at once (note selection model setting)
     */
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
    
    
    /**
     * Setup mouse press functions and disable slider until a track is loaded
     * 
     * ASSOCIATED REQUIREMENTS:
     * 8.1.0	User can drag a slider to adjust playback time
     * 8.4.0	Playback time slider is disabled at minimum until a file is played
     */
    private void initializeTimeSlider()
    {
        timeSlider.disableProperty().set(true);
        
        // Setup response to dragging
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
        
        // Setup binding for progress fill
        timeSlider.styleProperty().bind(Bindings.createStringBinding(() -> 
        {
            Double percentage = (timeSlider.getValue() - timeSlider.getMin()) / (timeSlider.getMax() - timeSlider.getMin()) * 100.0 ;
            
            if (percentage.isNaN())
            {
                percentage = 0.0;
            }
            
            return String.format("-slider-track-color: linear-gradient(to right, -slider-filled-track-color 0%%, "
                                + "-slider-filled-track-color %f%%, -fx-base %f%%, -fx-base 100%%);", 
                                percentage, percentage);
        },
        timeSlider.valueProperty(), timeSlider.minProperty(), timeSlider.maxProperty()));
        
    }
    
    
    /**
     * Set volume to max and setup listener
     * 
     * ASSOCIATED REQUIREMENTS:
     * 7.1.0	User can drag a slider to adjust playback volume
     * 7.4.0	Volume is set to max on program start
     */
    private void initializeVolumeSlider()
    {
        volumeSlider.setValue(100);
        
        // Setup transparent slider pane, unless mouse over
        volSliderPane.setOpacity(0);
        volumeIcon.setOnMouseEntered(event -> {
            volSliderPane.setOpacity(1);
        });
        volumeIcon.setOnMouseExited(event -> {
            volSliderPane.setOpacity(0);
        });
        volSliderPane.setOnMouseEntered(event -> {
            volSliderPane.setOpacity(1);
        });
        volSliderPane.setOnMouseExited(event -> {
            volSliderPane.setOpacity(0);
        });
        
        // Setup listener to adjust player volume
        volumeSlider.valueProperty().addListener(new InvalidationListener() {
            public void invalidated(Observable ov) {
                   if (player.mp != null)
                   {
                       player.mp.setVolume(volumeSlider.getValue() / 100.0);
                   }
            }
        });
        
        // Setup binding for partial fill
        volumeSlider.styleProperty().bind(Bindings.createStringBinding(() -> 
        {
            double percentage = (volumeSlider.getValue() - volumeSlider.getMin()) / (volumeSlider.getMax() - volumeSlider.getMin()) * 100.0 ;
            return String.format("-slider-track-color: linear-gradient(to top, -slider-filled-track-color 0%%, " 
                                 + "-slider-filled-track-color %f%%, -fx-base %f%%, -fx-base 100%%);", 
                                 percentage, percentage);
        },
        volumeSlider.valueProperty(), volumeSlider.minProperty(), volumeSlider.maxProperty()));
    }
    
    
     /**
     * Setup spectrum data graph and spectrum data listener
     * 
     * ASSOCIATED REQUIREMENTS:
     * 10.1.0	Audio data is represented as a bar chart of frequency magnitudes 
     * 10.1.1	Horizontal axis is discrete ‘bands’ of frequency ranges
     * 10.1.2	Vertical axis is magnitude
     * 10.1.3	Axes and legend of the chart should be hidden
     * 10.2.0	While an audio file is playing, chart is updated in sync with playback
     * 10.2.1 	Chart updates 10 times per second without flickering
     * 10.2.2	Chart stops updating when playback is paused
     * 10.2.3	Chart is empty when the app is started
     */
    private void initializeChart()
    {
        // Setup the animation timeline and data for the chart
        chart.setAnimated(false);
        chart.getYAxis().setOpacity(0);
        chart.getXAxis().setOpacity(0);
        chart.setVerticalGridLinesVisible(false);
        chart.setHorizontalGridLinesVisible(false);
        chart.setLegendVisible(false);
        
        Timeline animation = new Timeline();
        animation.getKeyFrames().add(new KeyFrame(Duration.millis(UPDATERATE), new EventHandler<ActionEvent>() 
        {
            @Override public void handle(ActionEvent actionEvent) 
            {
              // Update graph data on every key frame
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
    
    
    /**
     * Disable focus traversal so the LEFT/RIGHT keys only control playback time
     * 
     * ASSOCIATED REQUIREMENTS (indirect):
     * 8.2.0	User can press RIGHT to increase playback time and update slider
     * 8.3.0	User can press LEFT to decrease playback time and update slider
     */
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
    
    
    /**
     * Refresh file explorer contents and path ListViews with current data
     * 
     * ASSOCIATED REQUIREMENTS:
     * 1.1.0	List all accessible, unhidden child directories
     * 1.1.1	Each directory in the list should have a folder glyph
     * 1.2.1	List only WAV and MP3 audio files
     * 1.2.2	Each file in the list should have a file glyph
     * 2.0.0 	Display the full path of the parent directory of the contents list
     */
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
    
    
    /**
     * Refresh playlist ListView with current playlist data
     * 
     * ASSOCIATED REQUIREMENTS:
     * 4.0.0	Display a user-built list of WAV and MP3 audio files
     */
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
    
    
    /**
     * Find the currently playing track in the playlist and make bold
     * 
     * ASSOCIATED REQUIREMENTS:
     * 5.4.0	Current track is indicated in playlist with bold font
     */
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
    
    
    /**
     * Start playback from a new file and setup listeners to player
     * 
     * ASSOCIATED REQUIREMENTS:
     * 5.1.0	User can play an audio file from the playlist
     * 5.5.0	Next track is started when the end of a track is reached
     * 9.5.1	Current playback time is shown by slider position
     * 10.2.0	While an audio file is playing, chart is updated in sync with playback (note setAudioSpectrumInterval())
     * 
     * @param newFile audio file to start playing
     */
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
    
    /**
     * Configure drag and drop functions from file explorer to playlist
     * 
     * ASSOCIATED REQUIREMENTS:
     * 4.1.3	User can drag selected tracks from file explorer and drop in playlist
     */
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
                // If there is a file selection string data on dragboard, add selected files
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
    
    
    /**
     *  Add files selected in file explorer to playlist and update playlist ListView
     * 
     *  ASSOCIATED REQUIREMENTS:
     *  4.1.0	User can add the tracks selected in the file explorer
     *  4.1.5	Attempts to add directories selected in the file explorer are ignored
     */
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
    
    
    /**
     * Delete selected files from playlist and update playlist ListView
     * 
     * ASSOCIATED REQUIREMENTS:
     * 4.2.0	User can delete tracks selected in the playlist
     */
    private void deleteSelectedFilesFromPlaylist()
    {
        // Get selected items and call playlist delete method, then update GUI
        ObservableList<Integer> playlistSelection = playList.getSelectionModel().getSelectedIndices();
        playlist.delTracks(playlistSelection);
        updatePlaylistListView();
        
    }
    
    
    /**
     * Play the next track from the playlist (if there is one)
     * 
     * ASSOCIATED REQUIREMENTS:
     * 5.2.0	User can skip to the next file in the playlist
     * 5.5.0	Next track is started when the end of a track is reached
     * 5.2.6	If there are no files in the playlist, skipping to the next track does nothing
     */
    private void playNextTrack()
    {
        // Pass next file from playlist to player
        File newTrack = playlist.getNext();
        
        if (newTrack != null)
        {
            playNewTrack(newTrack);
        }
        
    }
 
    
     /**
     * Play the last track from the playlist (if there is one)
     * 
     * ASSOCIATED REQUIREMENTS:
     * 5.3.0	User can skip through a history of previously played tracks
     * 5.3.3	If there is no history, trying to go back does nothing
     */
    private void playLastTrack()
    {
        // Pass last file from playlist history to player
        File newTrack = playlist.getLast();
        
        if (newTrack != null)
        {
            playNewTrack(newTrack);
        }
        
    }
    
    
    /** 
     * Toggle playback pause on/off
     * 
     * ASSOCIATED REQUIREMENTS:
     * 6.0.0	User can pause and unpause audio file playback
     * 6.3.0 	The button and hotkey do nothing if no file is playing
     */
    private void togglePlayPause()
    {
        if (player.mp != null)
        {
            player.playPause();
        }
    }
    


    
}
