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
        curTrackIndex = -1;
        mode = MODE.NORMAL;
    }
    
    public void setMode(String modeName)
    {
        mode = MODE.valueOf(modeName);
    }
    
    
    public void setCurTrack(int index)
    {
        if (tracks.size() < 0)
        {
            // no tracks to set as current
            return;
        }
        
        // if the index is valid...
        if (index >= 0 && index <= (tracks.size()-1))
        {
            // ... then update history and set new current track
            
            if (curTrack != null)
            {
                history.add(curTrack);
            }
            curTrackIndex = index;
            curTrack = tracks.get(index);
        }
        else
        {
            // set to defaults
            curTrackIndex = -1;
            curTrack = null;
        }
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
        // Indices arg should be in increasing order
        // Delete tracks last to first, one at a time
        for (int i = indices.size()-1; i >= 0; i--)
        {
            File del = tracks.remove(indices.get(i).intValue());
            
            // remove all instances of deleted track in history too
            while (history.contains(del))
            {
                history.remove(del);
            }
            
            // if deleting the current track, set curTrack to default null
            if (indices.get(i).intValue() == curTrackIndex)
            {
                setCurTrack(-1);
            }
            
        }
        
        //update index of curTrack
        curTrackIndex = tracks.indexOf(curTrack);
        
    }
    
    public void moveTrackUp(int index)
    {
        File toMove = tracks.get(index);
        tracks.set(index, tracks.get(index-1));
        tracks.set(index-1, toMove);
        
        // if we're moving the current track, update it's index
        if (curTrack != null)
        {
            if (index == curTrackIndex || index == curTrackIndex+1) 
            {
                curTrackIndex = tracks.indexOf(curTrack);
            }
        }
    }
    

    public void moveTrackDown(int index)
    {
        File toMove = tracks.get(index);
        tracks.set(index, tracks.get(index+1));
        tracks.set(index+1, toMove);
        
        // if we're moving the current track, update it's index
        if (curTrack != null)
        {
            if (index == curTrackIndex || index == curTrackIndex-1) 
            {
                curTrackIndex = tracks.indexOf(curTrack);
            }
        }
    }
    
    
    public File getNext()
    {
        File nextTrack = null;
        int nextIndex = -1;
        
        if (tracks.size() > 0 && curTrack != null)
        {
            if (mode == MODE.NORMAL)
            {
                // Get next sequential track or wrap to start
                nextIndex = curTrackIndex + 1;
                if (nextIndex >= tracks.size())
                {
                    nextIndex = 0;
                }
            }
            else if (mode == MODE.SHUFFLE)
            {
                // Get random number that isn't the current track index
                Random rand = new Random();
                nextIndex = rand.nextInt(tracks.size());
                while (nextIndex == curTrackIndex)
                {
                    nextIndex = rand.nextInt(tracks.size());
                }
                
                /*  logic to prevent repeating tracks if new tracks exist
                    breaks after entire playlist is played once
                while (nextIndex == -1)
                {
                    nextIndex = rand.nextInt(tracks.size());
                    if (nextIndex == curTrackIndex || history.contains(tracks.get(nextIndex)))
                    {
                        nextIndex = -1;
                    }
                }*/
                
            }
            else if (mode == MODE.REPEAT)
            {
                nextIndex = curTrackIndex;
            }
        }
        
        if (nextIndex >= 0)
        {
            setCurTrack(nextIndex);
            nextTrack = tracks.get(curTrackIndex);
        }
        
        return nextTrack;
    }
    
    
    public File getLast()
    {
        File lastTrack = null;
        int lastIndex = -1;
        
        if(!history.isEmpty())
        {
            lastTrack = history.get(history.size()-1);
            lastIndex = tracks.indexOf(lastTrack);
            history.remove(history.size()-1);
            setCurTrack(lastIndex);
            history.remove(history.size()-1); //delete prior track from history
        }
        
        return lastTrack;
    }
    
    
    
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
