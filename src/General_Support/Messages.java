/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package General_Support;

import javafx.scene.control.Alert;

/**
 *
 * @author Station Manager
 */
public class Messages {
    public static void ShowInfoMessage(String Message,String AlertTitle)
    {
        Alert a=new Alert(Alert.AlertType.INFORMATION);
            a.setTitle(AlertTitle);
            a.setContentText(Message);
            a.showAndWait();
    }
    
  
    
    public static void ShowErrorMessage(String Message,String AlertTitle)
    {
        Alert a=new Alert(Alert.AlertType.ERROR);
            a.setTitle(AlertTitle);
            a.setContentText(Message);
            a.showAndWait();
    }
}
