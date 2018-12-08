package musicvisualizer;

import java.io.*; 
import java.util.Random; 
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * @author Tristan Hunter
 * 
 *  This class defines a dynamic ordered list of audio files 
 *  and the associated methods to be used for automated playback
 * 
 *  Files can be added, deleted, and reordered
 * 
 *  A current track and a history of previously current tracks are 
 *  stored as attributes during Playlist usage 
 * 
 */

public class Playlist 
{
    
    ObservableList<File> tracks;
    ObservableList<File> history;
    File curTrack;
    int curTrackIndex;
    MODE mode;

    enum MODE {NORMAL,SHUFFLE,REPEAT}
    
    
    /**
     *  Initialize a new playlist with no tracks, history, or current track
     * 
     *  ASSOCIATED REQUIREMENTS:
     *  4.4.1	Playlist should be in sequential mode when the app is opened
     *  4.5.0	Playlist is empty on program start
     */
    public Playlist()
    {
        tracks = FXCollections.observableArrayList();
        history = FXCollections.observableArrayList();
        curTrack = null;
        curTrackIndex = -1;
        mode = MODE.NORMAL;
    }
    
    
    /**
     *  Change the operational mode of the Playlist
     * 
     *  ASSOCIATED REQUIREMENTS:
     *  4.4.0	User can configure a playlist mode (sequential/shuffle/repeat)
     * 
     *  @param modeName string of an element of the enum MODE (i.e. "NORMAL"/"SHUFFLE"/"REPEAT")
     */
    public void setMode(String modeName)
    {
        mode = MODE.valueOf(modeName);
    }
    
    /**
     *  Set a new current track at a specified index
     * 
     *  ASSOCIATED REQUIREMENTS:
     *  5.4.0	Current track is indicated in playlist with bold font
     * 
     * @param index the index of the file in the playlist to set as current track
     */
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
            // ... otherwise, set to defaults
            curTrackIndex = -1;
            curTrack = null;
        }
    }
    
    /**
     *  Add valid tracks to end of playlist, but don't add duplicates
     * 
     *  ASSOCIATED REQUIREMENTS:
     *  4.1.0	User can add the tracks selected in the file explorer
     *  4.1.4	Attempts to add duplicate tracks are ignored
     * 
     *  @param newtracks list of new files to add
     */
    public void addTracks(ObservableList<File> newtracks)
    {
        for (File file : tracks)
        {
            if (newtracks.contains(file))
            {
                newtracks.remove(file);
            }
        }   
        tracks.addAll(newtracks);
    }
    
    
    /**
     *  Remove tracks from playlist (and history if necessary)
     * 
     *  ASSOCIATED REQUIREMENTS:
     *  4.2.0	User can delete tracks selected in the playlist
     *  5.3.4	If a file is deleted from playlist, it should be removed from the history too
     * 
     * @param indices list of indices of files to be removed - must be in ascending order!
     */
    public void delTracks(ObservableList<Integer> indices)
    {
        // Delete tracks last to first, one at a time
        for (int i = indices.size()-1; i >= 0; i--)
        {
            File del = tracks.remove(indices.get(i).intValue());
            
            // Remove all instances of deleted track in history too
            while (history.contains(del))
            {
                history.remove(del);
            }
            
            // If deleting the current track, set curTrack to default null
            if (indices.get(i).intValue() == curTrackIndex)
            {
                setCurTrack(-1);
            }
            
        }
        
        // Update index of curTrack
        curTrackIndex = tracks.indexOf(curTrack);
        
    }
    
    
    /**
     *  Swap index of a selected track with the next lower index
     * 
     *  ASSOCIATED REQUIREMENTS:
     *  4.3.0	User can reorder tracks
     *  4.3.1	User can move a selected track up the list using a button
     * 
     *  @param index index of file in the playlist to be moved up
     */
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
    
    /**
     *  Swap index of a selected track with the next higher index
     * 
     *  ASSOCIATED REQUIREMENTS:
     *  4.3.0	User can reorder tracks
     *  4.3.2	User can move a selected track down the list using a button
     * 
     *  @param index index of file in the playlist to be moved down
     */
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
    
    
    /**
     *  Get a new track from the playlist according to the mode and update history
     * 
     *  ASSOCIATED REQUIREMENTS:
     *  5.2.0   User can skip to the next file in the playlist
     *  5.2.3	If mode is shuffle, a random file from the playlist is started
     *  5.2.4	If mode is repeat, the current file is restarted if it is in the playlist still
     *  5.2.5	If mode is sequential, the next sequential file in the playlist is started
     * 
     *  @return an audio file in the playlist
     */
    public File getNext()
    {
        File nextTrack = null;
        int nextIndex = -1;
        
        if (tracks.size() > 0)
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
                if (tracks.size() == 1)
                {
                    nextIndex = 0;
                }
                else
                {
                    while (nextIndex == curTrackIndex)
                    {
                        nextIndex = rand.nextInt(tracks.size());
                    }
                }
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
    
    /**
     *  Get the most recent track from the history list
     * 
     *  ASSOCIATED REQUIREMENTS:
     *  5.3.0	User can skip through a history of previously played tracks
     *  5.3.3	If there is no history, trying to go back does nothing
     * 
     *  @return previous audio file
     */
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
    
    
    /**
     *  Get names of all audio files in the playlist
     * 
     *  ASSOCIATED REQUIREMENTS:
     *  4.0.0	Display a user-built list of WAV and MP3 audio files
     * 
     * @return list of audio file names in playlist
     */
    public ObservableList<String> getNames()
    {
        ObservableList<String> names = FXCollections.observableArrayList();
        
        for (File track : tracks)
        {
            names.add(track.getName());
        }
        
        return names;
    }
    
}
