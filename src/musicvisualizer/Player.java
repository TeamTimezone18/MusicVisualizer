package musicvisualizer;

import java.io.File;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import javafx.scene.media.AudioSpectrumListener;
import javafx.util.Duration;

/** 
 * @author Tristan Hunter
 * @author Dan Sharp
 * 
 *  The Player class defines the audio playback interface. 
 *  
 *  It is composed of a javafx.scene.media.MediaPlayer object 
 *  and a Track object (an inner class)
 * 
 *  The MediaPlayer is public so it can be manipulated by a controller
 * 
 *  Track attributes are updated asynchronously by 
 *  listening for value changes in the MediaPlayer
 * 
 */

public class Player 
{
    
    MediaPlayer mp;
    Track track;
    private AudioSpectrumListener audioSpectrumListener;
    
    
    /**
     *  Initialize an empty track and listener for spectrum data
     *  (Note that no MediaPlayer can be initialized until an audio file is chosen)
     */
    public Player()
    {
        track  = new Track();
        
        audioSpectrumListener = (double timestamp, double duration,
                                 float[] magnitudes, float[] phases) -> 
        {
            track.updateMagnitudes(magnitudes); 
        };
        
    }
    
    
    /**
     *  Start playback of a new audio file and update Track & listeners
     * 
     *  ASSOCIATED REQUIREMENTS:
     *  5.1.0	User can play an audio file from the playlist
     * 
     *  @param newTrack audio file to start playing
     */
    public void playNew(File newTrack)
    {
        // If there is a track playing, stop it before starting next
        if(mp != null)
        {
            mp.stop();
        }
        
        String uriString = newTrack.toURI().toString();
        mp = new MediaPlayer(new Media(uriString));
        mp.play();
        track.resetMetadata();
        track.updateDuration(mp.getMedia().getDuration());
        
        // Listen for metadata changes
        mp.getMedia().getMetadata().addListener(new MapChangeListener<String, Object>() 
    	{
            @Override
            public void onChanged(MapChangeListener.Change<? extends String, ? extends Object> ch) 
            {
              if (ch.wasAdded()) {
                track.updateMetadata(ch.getKey(), ch.getValueAdded());
              }
            }
          });
        
        // Listen for frequency data
        mp.setAudioSpectrumListener(audioSpectrumListener);
        mp.setAudioSpectrumThreshold(-100);
        
        
        // Listen for duration changes
        mp.totalDurationProperty().addListener(new InvalidationListener() 
        {
            @Override
            public void invalidated(Observable ov) {
                track.updateDuration(mp.getTotalDuration());
            }
        });
        
        // Listen for playtime changes
        mp.currentTimeProperty().addListener(new InvalidationListener() 
        {
            @Override
            public void invalidated(Observable ov) {
                track.updatePlaybackTime(mp.getCurrentTime());
            }
        });
    }
    
    
    /**
     * If a track is playing, pause playback. If a track is paused, start playback
     * 
     *  ASSOCIATED REQUIREMENTS:
     *  6.0.0	User can pause and unpause audio file playback
     */
    public void playPause()
    {
        if(track != null)
        {
            if (mp.getStatus() == Status.PAUSED)
            {
                mp.play();
            }
            else if (mp.getStatus() == Status.PLAYING)
            {
                mp.pause(); 
            }
        }
    }
    
    
    /**
     *  Seek the MediaPlayer object to a new time if it exists
     * 
     *  ASSOCIATED REQUIREMENTS:
     *  8.0.0	User can adjust current playback time
     * 
     *  @param currentTime percentage of duration (0-100) to seek MediaPlayer to
     */
    public void setTime(Number currentTime)
    {
        if (mp != null)
        {
            Duration trackTime = mp.getMedia().getDuration();
            Duration  newTime = trackTime.multiply((double) currentTime / 100);
            mp.seek(newTime);
            track.updatePlaybackTime(newTime);
        }
    }

    
    /**
     *  The Track class is nested in the Player class
     *  because it's lifecycle depends on the Player's lifecycle
     * 
     *  Track attributes are public and observable so the
     *  controller can listen to them and update the GUI
     */
    public class Track
    {
        public StringProperty artist;
        public StringProperty album;
        public StringProperty title;
        public StringProperty playbackTime;
        public StringProperty durationString;
        public DoubleProperty progress; // 0-100 -- current time percentage of duration
        public Duration duration;
        public ObservableList<Float> spectrumData = FXCollections.observableArrayList();
        int SPECTRUM_DATA_NUMBANDS = 128;  // size of spectrum data
        public ObjectProperty<Image> albumImage = new SimpleObjectProperty<>();;
        
        
        /**
         *  Constructor for a Track object
         *  (Initialize track data for new player - on program start only!)
         * 
         *  ASSOCIATED REQUIREMENTS:
         *  9.4.3   Duration is set to ‘00:00’ until a track is played
         *  9.5.4   Current playback time is set to ‘00:00’ until a track is played
         *  9.6.1   If no image metadata is available, load a default image
         *  9.7.0   Hide metadata display until a track is loaded
         *  10.2.3  Chart is empty when the app is started
         */
        public Track()
        {
            artist = new SimpleStringProperty();
            artist.set("");
            album = new SimpleStringProperty();
            album.set("");
            title = new SimpleStringProperty();
            title.set("");
            playbackTime = new SimpleStringProperty();
            playbackTime.set("0:00");
            progress = new SimpleDoubleProperty();
            progress.set(0.0);
            durationString = new SimpleStringProperty();
            durationString.set("0:00");
            duration = Duration.UNKNOWN;
            
            albumImage.set(new Image("/Resource/Vinyl.gif"));
            
            for(int i = 0; i<SPECTRUM_DATA_NUMBANDS; i++)
            {
                spectrumData.add(new Float(0));
            }
            
        }
        
        
        /**
         *  Function to normalize and set spectrum data values
         *  (this function is called asynchronously by the 
         *   MediaPlayer's audioSpectrumlistener)
         * 
         *  ASSOCIATED REQUIREMENTS:
         *  10.1.0	Audio data is represented as a bar chart of frequency magnitudes 
         *  10.2.0	While an audio file is playing, chart is updated in sync with playback
         *  10.2.2	Chart stops updating when playback is paused
         * 
         * @param magnitudes frequency magnitudes with each element corresponding to a frequency band
         */
        private void updateMagnitudes(float[] magnitudes)
        {
            int i = 0;
            for (float mag : magnitudes)
            {
                // Magnitudes are relative to an arbitrary dB-scale threshold,
                // so subtract the threshold to normalize (0.0 equals no sound)
                Float magnitude = new Float(mag-mp.getAudioSpectrumThreshold());
                spectrumData.set(i, magnitude);
                i++;
            }
            
        }
        
        
        /**
         *  Setter for current PlaybackTime
         *  (this function is called asynchronously by a listener
         *   on the MediaPlayer's currentTimeProperty)
         * 
         *  ASSOCIATED REQUIREMENTS:
         *  9.5.0   Current playback time is displayed
         * 
         * @param newTime time to set playback time to
         */
        private void updatePlaybackTime(Duration newTime) 
        {
            progress.set(100 * (newTime.toMillis()/duration.toMillis()));
            
            String newTimeString = formatTimeString(newTime);
            playbackTime.set(newTimeString);
        }
        
        
        /**
         *  Setter for duration of current track
         *  (this function called asynchronously by a listener
         *   on the MediaPlayer's totalDurationProperty)
         * 
         *  ASSOCIATED REQUIREMENTS:
         *  9.4.0    Duration is displayed
         * 
         * @param newDuration duration of new media
         */
        private void updateDuration(Duration newDuration) 
        {
            duration = newDuration;
            if (duration != null)
            {
                String newDurationString = formatTimeString(newDuration);
                durationString.set(newDurationString);
            }
        }
        
        
        /**
         *  Set track attributes to defaults
         * 
         *  Associated REQUIREMENTS:
         *  9.1.1   If no title metadata is available, “Unknown Title” should be displayed
         *  9.2.1   If no artist metadata is available, “Unknown Artist” should be displayed
         *  9.3.1   If no album metadata is available, “Unknown Album” should be displayed
         *  9.6.1   If no image metadata is available, load a default image
         */
        private void resetMetadata()
        {
            artist.set("Unknown Artist");
            album.set("Unknown Album");
            title.set("Unknown Title");
            albumImage.set(new Image("/Resource/Vinyl.gif"));
        }
        
        /**
         *  Setter for metadata attributes (album/artist/title/albumImage)
         *  (this function called asynchronously by a 
         *  listener on the MediaPlayer's getMetadata method)
         * 
         *  ASSOCIATED REQUIREMENTS:
         *  9.1.0   Track title is displayed
         *  9.2.0   Artist name is displayed
         *  9.3.0   Album name is displayed
         *  9.6.0   Album art image is displayed
         * 
         *  @param key   string indicating which metadata attribute to update
         *  @param value new metadata attribute value
         */
        private void updateMetadata(String key, Object value) 
        {
            if (key.equals("album")) 
            {
                album.set(value.toString());
            } 
            else if (key.equals("album artist")) 
            {
                artist.set(value.toString());
            } 
            if (key.equals("title"))  
            {
                title.set(value.toString());
            } 
            if (key.equals("image")) 
            {
                albumImage.set((Image)value);
            }
          }
        
        
        /**
         *  Build a time string as [mm:ss] from a Duration
         * 
         *  ASSOCIATED REQUIREMENTS:
         *  9.4.2   Duration is formatted [mm:ss]
         *  9.5.3   Current playback time is formatted [mm:ss]
         * 
         *  @param newTime  duration to be formatted
         * 
         *  @return time string in format [mm:ss] 
         */
        private String formatTimeString(Duration newTime)
        {
            Double mins = newTime.toMinutes();
            Double secs = mins%1.0;
            mins = mins - secs;
            secs = 60 * secs; //60 secs per min
            Integer minutes = mins.intValue();
            Integer seconds = secs.intValue();
            
            String secondsString;
            if (seconds < 10)
            {
                // Pad with a zero if only one digit
                secondsString = "0" + seconds.toString();
            }
            else
            {
                secondsString = seconds.toString();
            }
            String timeString = minutes + ":" + secondsString;
            return timeString;
        }
        
    }
    
}
