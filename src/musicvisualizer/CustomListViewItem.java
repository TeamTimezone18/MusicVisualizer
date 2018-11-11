/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package musicvisualizer;

import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 *
 * @author Personal
 */
public class CustomListViewItem 
{
    private final Label label = new Label();
    
    public void setLabelGlyph(String imgName)
    {
        
         Image image = new Image(getClass().getResourceAsStream(imgName));
        //FontAwesome fa = new FontAwesome();
        label.setGraphic(new ImageView(image));
    }

    public void setString(String string)
    {
        label.setText(string);
    }

    public Label getLabel()
    {
        return label;
    }
}
