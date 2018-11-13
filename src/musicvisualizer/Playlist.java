/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package musicvisualizer;

import java.io.*; 
import java.util.Random; 
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
    File curTrack;
    int curTrackIndex;
    MODE mode;

    enum MODE {NORMAL,SHUFFLE,REPEAT}
    
    public Playlist()
    {
        tracks = FXCollections.observableArrayList();
        history = FXCollections.observableArrayList();
        curTrack = null;
        curTrackIndex = 0;
        mode = MODE.NORMAL;
    }
    
    public void AddTracks(ObservableList<File> newtracks)
    {
        // Remove duplicates and add valid tracks to end
        for (File file : tracks)
        {
            if (newtracks.contains(file))
            {
                newtracks.remove(file);
            }
        }   
        tracks.addAll(newtracks);
    }
    
    
    public void delTracks(ObservableList<Integer> indices)
    {
        // Indices should be in increasing order
        // Delete tracks last to first, one at a time
        for (int i = indices.size()-1; i >= 0; i--)
        {
            File del = tracks.remove(indices.get(i).intValue());
            System.out.println("DELETING FROM PLAYLIST: " + del);
        }
    }
    
    
    public void moveTrackUp(int index)
    {
        File toMove = tracks.get(index);
        tracks.set(index, tracks.get(index-1));
        tracks.set(index-1, toMove);
        System.out.println(GetNames());
    }
    

    public void moveTrackDown(int index)
    {
        File toMove = tracks.get(index);
        tracks.set(index, tracks.get(index+1));
        tracks.set(index+1, toMove);
        System.out.println(GetNames());
    }
    
    /* Partially finished -- need to code shuffle (which needs history list)
    public File getNext()
    {
        File nextTrack = null;
        if (tracks.size() > 0)
        {
            if (mode == MODE.NORMAL)
            {
                // Get next sequential track
                curTrackIndex = curTrackIndex + 1;
                if (curTrackIndex < tracks.size())
                {
                    System.out.println(curTrackIndex);
                    nextTrack = tracks.get(curTrackIndex);
                }
                // or wrap to start at the end
                else
                {
                    curTrackIndex = 0;
                    curTrack = tracks.get(0);
                    nextTrack = tracks.get(curTrackIndex);
                }
            }
            else if (mode == MODE.SHUFFLE)
            {
                //TODO
                Random rand = new Random();
                int nextIndex;
                while (nextTrack == null)
                {
                    nextIndex = rand.nextInt(tracks.size() - 1);
                    //
                }
            }
            else if (mode == MODE.REPEAT)
            {
                nextTrack = curTrack;
            }
        }
        return nextTrack;
    }
    
        /* TODO
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
