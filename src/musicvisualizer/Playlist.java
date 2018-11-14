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
    
    public void setCurTrack(int index)
    {
        // if there are tracks in the list..
        if (tracks.size() >=0)
        {
            //.. and if the index is valid
            if (index >= 0 && index <= (tracks.size()-1))
            {
                // add track to history and set new
                if (curTrack != null)
                        {
                            history.add(curTrack);
                        }
                curTrackIndex = index;
                curTrack = tracks.get(index);
            }
        }
       System.out.println("CURRENT TRACK SET: " + curTrack);
       System.out.println("PLAYLIST HISTORY: " + history);
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
    
    /*
    public File getNext()
    {
        File nextTrack = null;
        
        if (tracks.size() > 0 && curTrack != null)
        {
            if (mode == MODE.NORMAL)
            {
                history.add(curTrack);
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
                history.add(curTrack);
                // Get random number that hasn't been played
                Random rand = new Random();
                int nextIndex;
                while (nextTrack == null)
                {
                    nextIndex = rand.nextInt(tracks.size() - 1);
                    if (history.contains(tracks.get(nextIndex)))
                    {
                        nextTrack = null;
                    }
                    else
                    {
                        nextTrack = tracks.get(nextIndex);
                    }
                }
            }
            else if (mode == MODE.REPEAT)
            {
                nextTrack = curTrack;
            }
        }
        System.out.println("PLAY NEXT :" + nextTrack);
        System.out.println("HISTORY: " + history);
        return nextTrack;
    }
    
    
    public File getLast()
    {
        File lastTrack = history.get(history.size()-1);
        history.remove(history.size()-1);
        return lastTrack;
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
