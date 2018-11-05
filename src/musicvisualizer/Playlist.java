/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package musicvisualizer;

import java.io.*; 
import java.util.*; 
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * @author Benjamin Wasserman
 * @author Dan Sharp
 * @author Tristan Hunter
 */

public class Playlist {
    
    ObservableList<File> tracks;
    ObservableList<File> history;
    MODE mode;

    enum MODE {NORMAL,SHUFFLE,REPEAT}
    
    public Playlist()
    {
        tracks = FXCollections.observableArrayList();
        history = FXCollections.observableArrayList();
        mode = MODE.NORMAL;
    }
    
    public void AddTracks(ObservableList<File> newtracks)
    {
        //TODO: skip adding track that are already in the playlist
        tracks.addAll(newtracks);
    }
    
    /* TODO
    public void delTracks(ObservableList<Integer> indices)
    {
        
    }
    
    public void moveTrackUp(int index)
    {
        
    }
    
    public void moveTrackDown(int index)
    {
        
    }
    
    public File getNext()
    {
        
    }
    
    public File getLast()
    {
        
    }

    */
    
    
    // UTILITY FUNCTIONS FOR GUI CONTROLLER
    
    // Return name strings of tracks in playlist
    public ObservableList<String> GetNames()
    {
        ObservableList<String> names = FXCollections.observableArrayList();
        
        for (File track : tracks)
        {
            names.add(track.getName());
        }
        
        return names;
    }
    
}
