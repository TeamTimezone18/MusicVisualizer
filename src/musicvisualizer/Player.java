/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package musicvisualizer;

import java.io.File;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;

/**
 * @author Benjamin Wasserman
 * @author Dan Sharp
 * @author Tristan Hunter
 */

public class Player {
    
    MediaPlayer mp;
    File track;
    
    public Player()
    {
        track = null;
    }
    
    public void PlayNew(File newtrack)
    {
        track = newtrack;
        
        //TODO: Stop and release old player before making a new one
        
        String uriString = track.toURI().toString();
        mp = new MediaPlayer(new Media(uriString));
        mp.play();
    }
    
    public void PlayPause()
    {
        //TODO: make sure player exists before trying to play/pause
        
        if (mp.getStatus() == Status.PAUSED)
        {
            mp.play();
        }
        else if (mp.getStatus() == Status.PLAYING)
        {
            mp.pause(); 
        }
    }
    
    /* TODO
    // - get metadata functions
    // - visualization data generator functions
    // - Get current playback time function
    // - Set current playback time function using mp.seek()?
    // 
    
    */
    
}
