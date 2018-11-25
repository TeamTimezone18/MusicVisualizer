/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package musicvisualizer;

import java.io.File;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.media.AudioSpectrumListener;
import javafx.util.Duration;

/**
 * @author Benjamin Wasserman
 * @author Dan Sharp
 * @author Tristan Hunter
 */

public class Player {
    
    MediaPlayer mp;
    Track track;
    private AudioSpectrumListener audioSpectrumListener;
    
    public Player()
    {
        track  = new Track();
        
        audioSpectrumListener = (double timestamp, double duration,
                                 float[] magnitudes, float[] phases) -> 
        {
            track.updateMagnitudes(magnitudes); 
        };
        
    }
    
    public void PlayNew(File newTrack)
    {
        // If there is a track playing, stop it before starting next
        if(mp != null)
        {
            mp.stop();
        }
        
        track.setTrack(newTrack);
        String uriString = track.trackFile.toURI().toString();
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
        
        // listen for frequency data
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
    
    public void PlayPause()
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

    
    public class Track
    {
        File trackFile;
        public StringProperty artist;
        public StringProperty album;
        public StringProperty title;
        public StringProperty playbackTime;
        public StringProperty durationString;
        public DoubleProperty progress; // 0-100 -- current time percentage of duration
        Duration duration;
        public ObservableList<Float> spectrumData = FXCollections.observableArrayList();
        int NUMBARS = 128;  // size of spectrum data
        
        public Track()
        {
            trackFile = null;
            artist = new SimpleStringProperty();
            artist.set("artist");
            album = new SimpleStringProperty();
            album.set("album");
            title = new SimpleStringProperty();
            title.set("title");
            playbackTime = new SimpleStringProperty();
            playbackTime.set("0:00");
            progress = new SimpleDoubleProperty();
            progress.set(0.0);
            durationString = new SimpleStringProperty();
            durationString.set("0:00");
            duration = Duration.UNKNOWN;
            
            for(int i = 0; i<NUMBARS; i++)
            {
                spectrumData.add(new Float(0));
            }
            
        }
        
        private void updateMagnitudes(float[] magnitudes)
        {
            int i = 0;
            for (float mag : magnitudes)
            {
                Float magnitude = new Float(mag-mp.getAudioSpectrumThreshold());
                spectrumData.set(i, magnitude);
                i++;
            }
            
        }
        
        public void setTrack(File file)
        {
            trackFile = file;
        }
        
        private void updatePlaybackTime(Duration newTime) 
        {
            progress.set(100 * (newTime.toMillis()/duration.toMillis()));
            
            String newTimeString = formatTimeString(newTime);
            playbackTime.set(newTimeString);
        }
        
        private void updateDuration(Duration newDuration) 
        {
            duration = newDuration;
            
            String newDurationString = formatTimeString(newDuration);
            durationString.set(newDurationString);
        }
        
        private void resetMetadata()
        {
            artist.set("-");
            album.set("-");
            title.set("-");
        }
        
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
                //
            }
          }
        
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
