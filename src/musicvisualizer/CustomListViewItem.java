/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package musicvisualizer;

import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

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
        label.setGraphic(new ImageView(image));
    }

    public void setString(String string)
    {
        label.setText(string);
    }

    public void setBold()
    {
        label.setFont(Font.font("", FontWeight.BOLD, 12));
    }
    
    public void setNormal()
    {
        label.setFont(Font.font("", FontWeight.NORMAL, 12));
    }
    
    public Label getLabel()
    {
        return label;
    }
    
}
