package Business_Code;

import java.net.URL;
import java.util.ResourceBundle;

import Exceptions.AuthenticationError;
import General_Support.Messages;
import User_Management.Active_User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.*;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

public class Main_Window_Controller implements Initializable {
	
	private int Main_Window_User_Permission;
	private String Main_Window_User_Name;

    @FXML
    private Label Label_Logged_In_As;

    @FXML
    private Label Label_Permission_Level;
    
    @FXML
    private MenuItem Create_User_MenuItem;
    
    @FXML
    private void handle_Create_User_MenuItem_Event(ActionEvent event)
    {
    	try
    	{
	    	if(Main_Window_User_Permission==1)
	    	{
	    		Parent root=FXMLLoader.load(getClass().getResource("/UI/Create New User.fxml"));
	    		Scene scene=new Scene(root);
            	Stage stage=new Stage();
            	stage.setScene(scene);
            	stage.setTitle("Create New User");
            	stage.setResizable(false);
            	stage.show();
	    	}
	    	else
	    	{
	    		throw new AuthenticationError("You are not authorized to create new user");
	    	}
    	}
    	catch(Exception e)
    	{
    		Messages.ShowErrorMessage(e.toString(), "Error");
    	}
    }

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		Main_Window_User_Permission=Active_User.getPermissionLevel();
		Main_Window_User_Name=Active_User.getUserName();
		Label_Logged_In_As.setText(Main_Window_User_Name);
		if(Main_Window_User_Permission==1)
		{
			Label_Permission_Level.setText("Administrator");
		}
		else
		{
			Label_Permission_Level.setText("User");
		}
	}

}

