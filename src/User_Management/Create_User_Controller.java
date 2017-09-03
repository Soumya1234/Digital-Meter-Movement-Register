package User_Management;

import java.net.URL;
import java.util.ResourceBundle;

import Exceptions.AuthenticationError;
import Exceptions.BlankFieldError;
import Exceptions.UserCreationError;
import General_Support.Messages;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;



public class Create_User_Controller implements Initializable{
	@FXML
    private PasswordField Password_Field1;

    @FXML
    private PasswordField ConfirmPassword_TextField;

    @FXML
    private TextField Username_TextField;
    
    @FXML
    private Button OK_Button;

    @FXML
    private ComboBox User_Role;
    
    @FXML
    private void handle_Create_User_OK_Button_Event(ActionEvent event)
    {
    	String Username=Username_TextField.getText();
    	
    	try
    	{   
	
    		if(Username.length()==0 || Password_Field1.getText().length()==0 || ConfirmPassword_TextField.getText().length()==0)
    		{
    		    throw new BlankFieldError("All fields are mandatory");
    		}
    		
    		if(User_Role.getSelectionModel().getSelectedItem()==null)
    		{
    		throw new BlankFieldError("Please Select User Role");
    		}
    		
    		if(Password_Field1.getText().equals(ConfirmPassword_TextField.getText()))
    		{
    			if(User.usernameExists(Username_TextField.getText()))
    			{
    				throw new UserCreationError("Username already exists. Please use another Username");
    			}
    			int Permission_Level= (User_Role.getSelectionModel().getSelectedItem().toString().equals("ADMINISTRATOR"))? 1:2;
    			String PasswordHashCode=Cryptography.getPasswordHash(Password_Field1.getText());
    			User.createUser(Username, PasswordHashCode, Permission_Level);
    			Username_TextField.setText("");
    			Password_Field1.setText("");
    			ConfirmPassword_TextField.setText("");
    			Messages.ShowInfoMessage("User Created Succesfully","Success");
    		}
    		else
    		{
    			throw new UserCreationError("Password not confirmed");
    		}
    	}
    	catch (Exception e)
    	{
    		Messages.ShowErrorMessage(e.toString(), "Error");
    	}
    }
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		User_Role.getItems().addAll("ADMINISTRATOR","USER");
		
	}

}
