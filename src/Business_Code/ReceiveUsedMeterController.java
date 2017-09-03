/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Business_Code;

import General_Support.Messages;
import General_Support.Shared_Connection;
import Business_Support.TDates;
import Exceptions.BlankFieldError;
import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 *
 * @author Station Manager
 */
public class ReceiveUsedMeterController implements Initializable {
    private Connection c=Shared_Connection.getSharedConnection();
    @FXML
    private TextField Receive_Used_Meter_Number;
    @FXML
    private ComboBox Receive_Used_Meter_Phase;
    @FXML
    private DatePicker Receive_Used_Meter_Date;
    @FXML
    private ComboBox Receive_Used_Meter_By;
    @FXML
    private Button Receive_Used_Meter_OK_Button;
    @FXML
    private void handle_Receive_Used_Meter_OK_Button_Event(ActionEvent event)
    {
        try{
                if(Receive_Used_Meter_Number.getText().length()==0)
                {
                    throw new BlankFieldError("Please Enter the Used Meter Number");
                }
                if(Receive_Used_Meter_Phase.getValue()==null)
                {
                    throw new BlankFieldError("Please Select the Used Meter Phase");
                }
                if(Receive_Used_Meter_Date.getValue()==null)
                {
                    throw new BlankFieldError("Please Enter the Receive Date");
                }
                if(Receive_Used_Meter_By.getValue()==null)
                {
                    throw new BlankFieldError("Please Select Receiving Vendor");
                }
                String Meter_No=Receive_Used_Meter_Number.getText();
                Date Receive_Date=TDates.getSQLDate(Receive_Used_Meter_Date.getValue());
                String Received_By=Receive_Used_Meter_By.getSelectionModel().getSelectedItem().toString();
                String Meter_Phase=Receive_Used_Meter_Phase.getSelectionModel().getSelectedItem().toString();
                String sql="INSERT INTO STOCK VALUES(?,?,?)";
                String sql2="SELECT VENDOR_CODE FROM VENDOR WHERE VENDOR_NAME=?";
                PreparedStatement pst2=c.prepareCall(sql2);
                pst2.setString(1, Received_By);
                ResultSet rst=pst2.executeQuery();
                while(rst.next())
                {
                    PreparedStatement pst=c.prepareCall(sql);
                    pst.setString(1,Meter_No);
                    pst.setString(2,Meter_Phase);
                    pst.setInt(3,rst.getInt("VENDOR_CODE"));
                    pst.execute();

                    String sql3="INSERT INTO RECEIVE_UTILIZED VALUES(?,?,?,?)";
                    PreparedStatement pst3=c.prepareCall(sql3);
                    pst3.setString(1, Meter_No);
                    pst3.setString(2,Meter_Phase);
                    pst3.setInt(3,rst.getInt("VENDOR_CODE"));
                    pst3.setDate(4, Receive_Date);
                    pst3.execute();
                }
               Messages.ShowInfoMessage("Stock Successfully Updated", "Success");
               Receive_Used_Meter_Number.setText("");
               Receive_Used_Meter_Phase.setValue(null);
               Receive_Used_Meter_Date.setValue(null);
               Receive_Used_Meter_Date.getEditor().setText("");
               Receive_Used_Meter_By.setValue(null);              
        }
        catch(Exception e)
        {
            Messages.ShowInfoMessage(e.toString(), "Error");
        }
    }
    @Override
    public void initialize(URL url, ResourceBundle rb) {
       Receive_Used_Meter_Phase.getItems().addAll("1P","3P");
       ResultSet rst=null;
        Statement stmt;
        String sql="SELECT * FROM VENDOR ORDER BY VENDOR_CODE ASC";
        try
        {
            stmt=c.createStatement();
            rst=stmt.executeQuery(sql);
            while(rst.next())
            {
                Receive_Used_Meter_By.getItems().add(rst.getString("VENDOR_NAME"));                
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }    
    
}
