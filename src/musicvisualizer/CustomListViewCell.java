/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package musicvisualizer;

import javafx.scene.control.Label;
import javafx.scene.control.ListCell;

/**
 *
 * @author Personal
 */
public class CustomListViewCell extends ListCell<CustomListViewItem>{
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
