/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Business_Code;

import General_Support.Messages;
import General_Support.Shared_Connection;
import Exceptions.BlankFieldError;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import java.sql.*;
import javax.swing.JOptionPane;

/**
 * FXML Controller class
 *
 * @author Station Manager
 */
public class CreateNewVendorController implements Initializable {
    private Connection b=Shared_Connection.getSharedConnection();
    @FXML
    private TextField New_Vendor_Name;
    @FXML
    private ComboBox New_Vendor_Category;
    @FXML
    private TextField New_Vendor_Address;
    @FXML
    private TextField New_Vendor_Pin;
    @FXML
    private TextField New_Vendor_Mobile;
    @FXML
    private TextField New_Vendor_Email;
    @FXML
    private Button New_Vendor_OK_Button;
    @FXML
    private void handle_New_Vendor_OK_Button_Event(ActionEvent event)
    {
         try
         {
             if(New_Vendor_Name.getText().length()==0)
             {
                 throw new BlankFieldError("Please Enter Vendor Name");
             }
             if(New_Vendor_Category.getValue()==null)
             {
                 throw new BlankFieldError("Please Select Vendor Category");
             }
             if(New_Vendor_Address.getText().length()==0)
             {
                 throw new BlankFieldError("Please Enter Vendor Address");
             }
             if(New_Vendor_Pin.getText().length()==0)
             {
                 throw new BlankFieldError("Please Enter Vendor Pin Code");
             }
             String Name=New_Vendor_Name.getText();
             String Type=New_Vendor_Category.getSelectionModel().getSelectedItem().toString();
             String Address=New_Vendor_Address.getText(); 
             String pin=New_Vendor_Pin.getText();
             String mob=New_Vendor_Mobile.getText();             
             String Email=New_Vendor_Email.getText();
             int Last_Code=0;
             Statement myStat=null;
             myStat=b.createStatement();
             ResultSet myRest=myStat.executeQuery("SELECT * FROM VENDOR ORDER BY VENDOR_CODE DESC LIMIT 1");
             if(myRest.next())
             {
                Last_Code=myRest.getInt("VENDOR_CODE");
             }
             int New_Code=Last_Code+1;
             String sql="INSERT INTO VENDOR values(?,?,?,?,?,?,?)";
             PreparedStatement pst=b.prepareStatement(sql);
             pst.setInt(1,New_Code);
             pst.setString(2, Name);
             pst.setString(3, Type);
             pst.setString(4, Address);
             pst.setString(5,pin);
             pst.setString(6,mob);
             pst.setString(7, Email);
             pst.execute();
             String Create_Vendor_Table="CREATE TABLE "+"v"+Integer.toString(New_Code)+" (METER_NO varchar(255),METER_PHASE varchar(12),ISSUE_DATE datetime,WO_NO varchar(255),WO_DATE datetime,PURPOSE varchar(255))";
             PreparedStatement pst2=b.prepareStatement(Create_Vendor_Table);
             pst2.execute();
             New_Vendor_Address.setText("");
             New_Vendor_Name.setText("");
             New_Vendor_Pin.setText("");
             New_Vendor_Mobile.setText("");
             New_Vendor_Email.setText("");
             New_Vendor_Category.setValue(null);
             Messages.ShowInfoMessage("Vendor Successfully Created with code "+Integer.toString(New_Code)+".Please Restart the Application for the Changes to take Effect","Success");
           }
         
         catch(Exception e)
         {
             Messages.ShowErrorMessage(e.toString(), "Error");
         }
    }
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        New_Vendor_Category.getItems().addAll("DEPARTMENTAL","CONTRACTUAL");
    }    
    
}
