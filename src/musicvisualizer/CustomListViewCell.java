package musicvisualizer;

import javafx.scene.control.Label;
import javafx.scene.control.ListCell;

/**
 * @author Tristan Hunter
 * 
 *  This class defines the list cell of a custom list item object to be
 *  used in the file explorer ListView and playlist ListView
 * 
 *  CustomListViewCell inherits from javafx.scene.control.ListCell
 *  but overrides the updateItem() method to use a CustomListViewItem
 * 
 *  ASSOCIATED REQUIREMENTS:
 *  1.0.0	Display the contents of a directory in an interactive list
 *  4.0.0	Display a user-built list of WAV and MP3 audio files
 * 
 */

public class CustomListViewCell extends ListCell<CustomListViewItem>
{
    @Override
    public void updateItem(CustomListViewItem item, boolean empty)
    {         
       super.updateItem(item, empty);

        if (empty || item == null) 
        {
            setGraphic(null);
            setText(null);
        } 
        else 
        {
            Label label = item.getLabel();
            setGraphic(label);
        }       
    }
}
