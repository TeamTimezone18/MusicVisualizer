package musicvisualizer;

import java.io.File;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *  @author Tristan Hunter
 * 
 *  This class defines a local file explorer for MP3 and WAV files.
 * 
 *  Lists of child directories and files within
 *  a parent directory are stored as attributes 
 * 
 *  The attributes are observable so the controller
 *  can show them in the GUI
 * 
 */

public class FileExplorer
{    
    
    ObservableList<File> files;
    ObservableList<File> childDirs;
    File curDir;
    
    
    /**
     *  Initialize a new file explorer object of the 
     *  user's music folder or default path if not found
     * 
     *  ASSOCIATED REQUIREMENTS:
     *  3.0.0	Initialize to user’s music folder C:\Users\[User]\Music
     *  3.1.0	If not found, open folder containing the running app
     */
    public FileExplorer()
    {
        String username = System.getProperty("user.name");
        curDir = new File("C:\\Users\\" + username + "\\Music");
        
        files = FXCollections.observableArrayList();
        childDirs = FXCollections.observableArrayList();
        
        if (!curDir.exists())
        {
            curDir = new File(System.getProperty("user.dir"));
        }
        
        openDirectory(curDir);
        
    }
    
    
    /**
     *  Update to a specified directory
     * 
     *  ASSOCIATED REQUIREMENTS:
     *  1.1.0	List all accessible, unhidden child directories
     *  1.2.0	List accessible, unhidden audio files
     *  1.2.1	List only WAV and MP3 audio files
     *  1.4.0	User can double-click directories in the list to update the list with the contents
     * 
     *  @param newdir the File object of the directory to navigate to
     */
    public void openDirectory(File newdir)
    {
        curDir = newdir;
        files.clear();
        childDirs.clear();
        
        // Check if directory has contents -- only update childDirs and files if not empty
        if (curDir.listFiles() != null)
        {
            // Add all subdirectories to childDirs
            childDirs.addAll(curDir.listFiles(File::isDirectory));
            
            // Remove hidden directories from childDirs
            ObservableList<File> hiddenDirs = FXCollections.observableArrayList();
            for (File dir : childDirs)
            {
                if (dir.isHidden())
                {
                    hiddenDirs.add(dir);
                }
            }
            for (File dir : hiddenDirs)
            {
                childDirs.remove(dir);
            }
            
            // Update list of files from new directory
            files.addAll(curDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".wav")));
            files.addAll(curDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".mp3")));
        }
    }
    
    
    /**
     *  Go to parent directory n levels above curDir
     * 
     *  ASSOCIATED REQUIREMENTS:
     *  2.1.0	User can go up directories by clicking between “\” delimiters in the displayed path
     * 
     * @param n the number of parent directories to go up
     */
    public void upDirectory(int n)
    {
        for (int i=0; i<n; i++)
            {
                curDir = curDir.getParentFile();
            }
        
        openDirectory(curDir);
    }
    
     
    /**
     *  Getter for the current directory path
     * 
     *  ASSOCIATED REQUIREMENTS:
     *  2.0.0 	Display the full path of the parent directory of the contents list
     * 
     *  @return list of strings of directory names up to current directory
     */
    public ObservableList<String> getPathList()
    {
        ObservableList<String> dirNames = FXCollections.observableArrayList();
        File tempDir = curDir;
        
        while (tempDir != null) {
            dirNames.add(0, tempDir.getName() + " \\");
            tempDir = tempDir.getParentFile();
        }
        
        dirNames.remove(0);
        dirNames.add(0, "C:" + " \\");
        return dirNames;
    }
    
    
    /**
     *  Getter for current directory contents file names
     * 
     *  ASSOCIATED REQUIREMENTS:
     *  1.2.0	List accessible, unhidden audio files
     * 
     * @return list of all file names in current directory
     */
    public ObservableList<String> getAllFileNames()
    {
        ObservableList<String> fileNames = FXCollections.observableArrayList();
       
        for (File file : files) {
            if (file.isFile()) {
                fileNames.add(file.getName());
            }
        }
        
        return fileNames;
    }
    
    
    /**
     *  Getter for current directory's child directory names
     * 
     *  ASSOCIATED REQUIREMENTS:
     *  1.1.0	List all accessible, un-hidden child directories
     * 
     *  @return list of all child directory names in current directory
     */
    public ObservableList<String> getAllDirNames()
    {
        ObservableList<String> dirNames = FXCollections.observableArrayList();
       
        for (File dir : childDirs) 
        {
                dirNames.add(dir.getName());
        }
        
        return dirNames;
    }
    
    
    /**
     *  Getter for File objects in the current directory
     * 
     *  ASSOCIATED REQUIREMENTS:
     *  4.1.0	User can add the tracks selected in the file explorer
     * 
     * @param indices list of selected indices in file explorer ListView
     * @return list of Files at specified indices in ascending order
     */
    public ObservableList<File> getFilesAtIndices(ObservableList<Integer> indices)
    {
        ObservableList<File> filesAtIndices = FXCollections.observableArrayList();
       
        for (int index : indices) 
        {
                filesAtIndices.add((files.get(index)));
        }
        
        return filesAtIndices;
    }
    
    
}
