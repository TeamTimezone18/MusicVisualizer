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
*  @author Benjamin Wasserman
 * @author Dan Sharp
 * @author Tristan Hunter
 */
public class FileExplorer
{    
    ObservableList<File> dirContents;
    File curDir;
    
    // Initialize to user's music folder 
    public FileExplorer()
    {
        String username = System.getProperty("user.name");
        System.out.println(username);
        
        curDir = new File("C:\\Users\\" + username + "\\Music");
        
        dirContents = FXCollections.observableArrayList();
        dirContents.addAll(curDir.listFiles());
         
    }
    
    public void openDirectory(File newdir)
    {
        curDir = newdir;
        dirContents.clear();
        dirContents.addAll(curDir.listFiles());
    }
    
    
    //UTILITY FUNCTIONS FOR GUI CONTROLLER
    
    // Return name strings of all items in current directory 
    public ObservableList<String> GetAllNames()
    {
        ObservableList<String> files = FXCollections.observableArrayList();
       
        for (File file : dirContents) {
            if (file.isFile()) {
                files.add(file.getName());
            }
        }
        
        return files;
    }
    
    
    
    // Return names strings of items at specific indices
    public ObservableList<String> GetNamesAtIndices(ObservableList<Integer> indices)
    {
        ObservableList<String> files = FXCollections.observableArrayList();
       
        for (int index : indices) 
        {
                files.add((dirContents.get(index)).getName());
        }
        
        return files;
    }
    
    
    // Return list of Files objects at specific indices
    public ObservableList<File> GetFilesAtIndices(ObservableList<Integer> indices)
    {
        ObservableList<File> files = FXCollections.observableArrayList();
       
        for (int index : indices) 
        {
                files.add((dirContents.get(index)));
        }
        
        return files;
    }
    
    
}
