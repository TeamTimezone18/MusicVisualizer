/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package musicvisualizer;

import java.io.File;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * @author Tristan Hunter
 */
public class FileExplorer
{    
    ObservableList<File> files;
    ObservableList<File> childDirs;
    File curDir;
    
    // Initialize to user's music folder 
    public FileExplorer()
    {
        String username = System.getProperty("user.name");
        curDir = new File("C:\\Users\\" + username + "\\Music");
        
        files = FXCollections.observableArrayList();
        childDirs = FXCollections.observableArrayList();
        openDirectory(curDir);
         
    }
    
    // Go to a specified directory
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
    
    // Go to parent directory n levels above curDir
    public void upDirectory(int n)
    {
        for (int i=0; i<n; i++)
            {
                curDir = curDir.getParentFile();
            }
        
        openDirectory(curDir);
    }
    
    
    // Return list of strings of directoy names up to current directory 
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
    
    // Return name strings of all files in current directory 
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
    
    // Return name strings of all folders in current directory 
    public ObservableList<String> getAllDirNames()
    {
        ObservableList<String> dirNames = FXCollections.observableArrayList();
       
        for (File dir : childDirs) 
        {
                dirNames.add(dir.getName());
        }
        
        return dirNames;
    }
    
    // Return list of Files objects at specific indices
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
