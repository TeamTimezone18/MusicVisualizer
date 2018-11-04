/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package musicvisualizer;

import java.io.File;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

/**
 * @author Benjamin Wasserman
 * @author Dan Sharp
 * @author Tristan Hunter
 */

public class Player {
    
    MediaPlayer mp;
    File track;
    boolean paused;
    
    public Player()
    {
        track = null;
        paused = true;
    }
    
    public void PlayNew(File newtrack)
    {
        track = newtrack;
        paused = false;
        
        String uriString = track.toURI().toString();
        mp = new MediaPlayer(new Media(uriString));
        mp.play();
    }
    
    public void PlayPause()
    {
        if (paused)
        {
            mp.play();
            paused = false;
        }
        else
        {
            mp.pause(); 
            paused = true;
        }
    }
    
    /* TODO
    // - get metadata functions
    // - visualization functions
    // - set playback time function
    
    */
    
}
