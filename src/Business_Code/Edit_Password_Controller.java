package Business_Code;

import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import Exceptions.AuthenticationError;
import Exceptions.GeneralError;
import General_Support.Messages;
import General_Support.Shared_Connection;
import User_Management.Active_User;
import User_Management.Cryptography;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;

public class Edit_Password_Controller implements Initializable {
	 private void editPassword(String newPassword) throws SQLException, NoSuchAlgorithmException
	 {
		 Connection con=Shared_Connection.getSharedConnection();
		 String password_query="UPDATE METER_MOVEMENT.USER_DATA SET LOGIN_PASSWORD=? WHERE USERNAME=?";
		 PreparedStatement pst=con.prepareStatement(password_query);
		 pst.setString(1,Cryptography.getPasswordHash(newPassword));
		 pst.setString(2,Active_User.getUserName());
		 pst.executeUpdate();
	 }
	 private boolean currentPasswordVerified(String CurrentPass) throws SQLException, NoSuchAlgorithmException
	 {
		 Connection con=Shared_Connection.getSharedConnection();
		 String password_query="SELECT LOGIN_PASSWORD FROM METER_MOVEMENT.USER_DATA WHERE USERNAME=?";
		 PreparedStatement pst=con.prepareStatement(password_query);
		 pst.setString(1, Active_User.getUserName());
		 ResultSet rst=pst.executeQuery();
		 rst.next();
	     if(Cryptography.getPasswordHash(CurrentPass).equals(rst.getString("LOGIN_PASSWORD")))
		 {
			return true;
		 }
		 return false;
	 }
     @FXML
     private PasswordField Current_Password_Text;
     @FXML
     private PasswordField New_Password_Text;
     @FXML
     private PasswordField New_Pass_Confirm_Text;
     @FXML
     private Button New_Password_Save_Btn;
     @FXML
     void handle_New_Password_Save_Btn_Event(ActionEvent event) 
     {
    	 try
    	 {
	    	 if(Current_Password_Text.getText().length()==0 || New_Password_Text.getText().length()==0 || New_Pass_Confirm_Text.getText().length()==0)
	    	 {
	    		 throw new GeneralError("All fields are mandatory");
	    	 }
	    	 if(!New_Password_Text.getText().equals(New_Pass_Confirm_Text.getText()))
	    	 {
	    		 throw new GeneralError("New Password not Confirmed");
	    	 }
	    	 if(currentPasswordVerified(Current_Password_Text.getText())) 
	    	 {
	    		 System.out.println("OK");
	    		 editPassword(New_Password_Text.getText());
	    		 Messages.ShowInfoMessage("Password Changed Successfully", "Success");
	    		 Node source = (Node) event.getSource();
	    		 Stage stage = (Stage) source.getScene().getWindow();
	    		 stage.close();
	    	 }
	    	 else
	    	 {
	    		 System.out.println("not OK");
	    		 throw new AuthenticationError("Authentication Error:Current Password entered is wrong.");
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
		
	}

}
