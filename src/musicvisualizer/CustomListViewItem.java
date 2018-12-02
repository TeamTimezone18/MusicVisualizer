package musicvisualizer;

import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * @author Tristan Hunter
 * 
 *  This class defines the custom objects to be used 
 *  in the file explorer ListView and playlist ListView
 * 
 */

public class CustomListViewItem 
{
    private final Label label = new Label();
    
    
    /**
     *  Sets the icon of the ListView cell to a specified image
     * 
     *  ASSOCIATED REQUIREMENTS:
     *  1.1.1	Each directory in the list should have a folder glyph
     *  1.2.2    Each file in the list should have a file glyph
     * 
     *  @param imgName  the path to the image file (with extension)
     */
    public void setLabelGlyph(String imgName)
    {
        Image image = new Image(getClass().getResourceAsStream(imgName));
        label.setGraphic(new ImageView(image));
    }

    
    /**
     *  Sets the text in a ListView cell
     * 
     *  ASSOCIATED REQUIREMENTS:
     *  1.0.0	Display the contents of a directory in an interactive list
     *  4.0.0	Display a user-built list of WAV and MP3 audio files
     * 
     *  @param string the text that the ListView cell will display
     */
    public void setString(String string)
    {
        label.setText(string);
    }

    
    /**
     *  Sets the font in the ListView cell to bold
     * 
     *  ASSOCIATED REQUIREMENTS:
     *  5.4.0	Current track is indicated in playlist with bold font
     */
    public void setBold()
    {
        label.setFont(Font.font("", FontWeight.BOLD, 12));
    }
    
    
    /**
     *  Un-bold a ListView cell
     *  (for updating the playlist current track)
     * 
     *  ASSOCIATED REQUIREMENTS:
     *  5.4.0	Current track is indicated in playlist with bold font
     */
    public void setNormal()
    {
        label.setFont(Font.font("", FontWeight.NORMAL, 12));
    }
    
    /**
     *  Getter for the cell data 
     *  (for updating the ListViews)
     * 
     *  @return the current label in the ListView cell
     */
    public Label getLabel()
    {
        return label;
    }
    
}
