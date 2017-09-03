/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Business_Code;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.StackPane;
import javafx.event.ActionEvent;
import javax.swing.JOptionPane;
import Exceptions.*;
import Business_Support.ExcelHandling;
import Business_Support.Meter_Rep_Data_Entry;
import General_Support.Messages;
import Business_Support.Receive;
import Business_Support.Return;
import Business_Support.Return_Report;
import General_Support.Shared_Connection;
import User_Management.Active_User;
import Business_Support.Stock;
import Business_Support.TDates;
import Business_Support.Utilization;
import Business_Support.Vendor_Stock;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.time.ZoneId;
import static javafx.application.Platform.exit;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javax.swing.JTextField;

/**
 * FXML Controller class for the main window
 *
 * @author Station Manager
 */
public class DeviceManagerController implements Initializable {
    private Connection a=Shared_Connection.getSharedConnection();
    private int User_Permission_Level=Active_User.getPermissionLevel();
    @FXML
    private Label User_Name_Text;
    @FXML
    private TextField R_Starting_Meter_No;
    @FXML
    private TextField R_Last_Meter_No;
    @FXML
    private ComboBox R_Received_By;
    @FXML
    private ComboBox R_Meter_Phase;
    @FXML
    private DatePicker R_Receive_Date;
    @FXML
    private TextField R_Order_No;
    @FXML
    private DatePicker R_Order_Date;
    @FXML
    private TextField R_ERP_Reference;
    @FXML
    private Button R_Receive_Button;
    @FXML
    private Button R_Receive_Used_Meter_Button;
    @FXML
    private void handle_Receive_Button_Event(ActionEvent event)
    {
    	//THE TABLES EFFECTED ARE "RECEIVE","V-X","STOCK"//
        try
        {
          if(R_Starting_Meter_No.getText().length()==0)
          {
              throw new BlankFieldError("Enter Starting Meter Number");
          }
          if(R_Meter_Phase.getSelectionModel().getSelectedItem()==null)
          {
              throw new BlankFieldError("Please Select Meter Phase");
          }
          if(R_Received_By.getSelectionModel().getSelectedItem()==null)
          {
             throw new BlankFieldError("Please Select the Receiving Vendor");
          }
          if(R_Receive_Date.getValue()==null)
          {
             throw new BlankFieldError("Enter Receive Date");
          }
          if(R_ERP_Reference.getText().length()==0)
          {
              throw new BlankFieldError("Please Enter ERP Reference Number");
          }
          String s=R_Receive_Date.getValue().toString();
          System.out.println(s);
          Date R_WO_Date=null;
          if(R_Order_Date.getValue()==null)
          {
             R_WO_Date=null;
          }
          else
          {
             R_WO_Date = TDates.getSQLDate(R_Order_Date.getValue());
          } 
          String From=R_Starting_Meter_No.getText();
          String To=R_Last_Meter_No.getText();
          String Phase=R_Meter_Phase.getSelectionModel().getSelectedItem().toString();
          Date R_Date=TDates.getSQLDate(R_Receive_Date.getValue());
          String ERP=R_ERP_Reference.getText();
          String R_By_Name=(R_Received_By.getSelectionModel().getSelectedItem().toString());
          String WO_PO_No=R_Order_No.getText();
          int R_By_Code=Vendor_Stock.getVendorCode(a, R_By_Name);
          if(R_Last_Meter_No.getText().length()==0)   //ONLY A SINGLE METER IS BEING RECEIVED
          {
	            if (Stock.deviceExists(a, R_Starting_Meter_No.getText())) //IF THE SINGLE METER EXISTS IN STOCK
	            {                
	                R_Starting_Meter_No.setText("") ;
	                R_Last_Meter_No.setText("");
	                R_Received_By.setValue(null);
	                R_Order_No.setText("");
	                R_ERP_Reference.setText("");
	                throw new GeneralError("Meter already in local stock");
	            }
	            if(Vendor_Stock.deviceWithVendor(a, R_By_Code, From))
	            {
	            	throw new GeneralError("Meter already in Vendor's Stock");
	            }
	            else  //IF THE SINGLE METER IS NOT IN STOCK AND IS CLEAR TO BE RECEIVED
	            {             
	                if(Vendor_Stock.vendorType(a, R_By_Code).equals("DEPARTMENTAL")) // IF RECEIVER OF THE SINGLE METER IS A DEPARTMENTAL STAFF
	                {	                    
	                     try{   
	                         String sql="Insert into receive values(?,?,?,?,?,?,?)";
	                         PreparedStatement pst=a.prepareStatement(sql);
	                         pst.setString(1, From);
	                         pst.setString(2, Phase);
	                         pst.setDate(3, R_Date);
	                         pst.setInt(4, R_By_Code);
	                         pst.setString(5,ERP);
	                         pst.setString(6,WO_PO_No );
	                         pst.setDate(7,R_WO_Date);
	                         pst.execute();
	                         Stock.addToStock(a, From, Phase, R_By_Code);
	                         Messages.ShowInfoMessage("Stock Successfully Updated", "Success");
	                         R_Starting_Meter_No.setText("") ;
	                         R_Last_Meter_No.setText("");
	                         R_Received_By.setValue(null);
	                         R_Order_No.setText("");                          
	                        }
	                         catch(Exception e)
	                        {
	                            Messages.ShowErrorMessage(e.getMessage(), "Error");
	                        }
	                } 
	                else  //IF THE RECEIVER OF THE SINGLE METER IS CONTRACTUAL AGENCY
	                {   
	                	try
	                	{
	                		 String sql="Insert into receive values(?,?,?,?,?,?,?)";
	                         PreparedStatement pst=a.prepareStatement(sql);
	                         pst.setString(1, From);
	                         pst.setString(2, Phase);
	                         pst.setDate(3, R_Date);
	                         pst.setInt(4, R_By_Code);
	                         pst.setString(5,ERP);
	                         pst.setString(6,WO_PO_No );
	                         pst.setDate(7,R_WO_Date);
	                         pst.execute();
	                		 Vendor_Stock.issueToVendor(a, R_By_Code, From, Phase, R_Date,WO_PO_No, R_WO_Date, null);
		                     Messages.ShowInfoMessage("Vendor Stock Successfully Updated", "Success"); 
	                	}
	                	catch(Exception e)
	                	{
	                		Messages.ShowErrorMessage(e.getMessage(), "Error");
	                	}	                   
	                }  
	            } 
          }
          else // IF A RANGE OF METERS ARE BEING RECEIVED
          {
	           String From_Sub_Int_String=From.substring(3, From.length());
	           //System.out.println(From_Sub_Int_String);
	           int From_Int=Integer.parseInt(From_Sub_Int_String);
	           String From_Sub_Char_String=From.substring(0, 3);
	           //System.out.println(From_Sub_Char_String);
	           String To_Sub_Int_String=To.substring(3, To.length());
	           int To_Int=Integer.parseInt(To_Sub_Int_String);
	           String To_Sub_Char_String=To.substring(0,3);
	           int batch_amount=To_Int-From_Int;
	           if(batch_amount>9)
	           {
	        	   throw new GeneralError("Please Enter a range of Maximum 10 Meters");
	           }
	           //System.out.println(To_Int);
	           //System.out.println(From_Int);
	           //System.out.println(batch_amount);
	           if ((From_Sub_Char_String.equals(To_Sub_Char_String)==false) || (From.length()!=To.length())) // IF THERE IS A TYPO IN THE METER NO ENTRY FIELD 
	           {
	               //System.out.println(Starting_Meter_Sub);
	               //System.out.println(Last_Meter_Sub);
	               Messages.ShowErrorMessage("Device Series Mismatch", "Error");
	           }
	           else // IF THERE IS NO TYPO IN THE METER NO ENTRY FIELDS
	           {              
	               try{
	                   int flag=0;//FLAG TO CHECK IF ANY OF THE METERS IN THE RANGE ALREADY EXIST IN STOCK
	                   int d=1;
	                   int f=1;
	                   String m_no=From;
	                   System.out.println(m_no);
	                   //THE FOLLOWING SECTION CHECKS IF ANY OF THE METERS IN THE RANGE IS ANY STOCK// 
	                   while(d<=batch_amount+1)
	                       {     if(Stock.deviceExists(a, m_no)) //IF ANY OF THE METERS IN THE RANGE ALREADY EXIST IN STOCK
	                             {
	                                 Messages.ShowErrorMessage("Meters in the range already in Stock. Please Reenter Data", "Error");
	                                 flag=1;
	                                 break;
	                             }  
	                             String m_no_sub_int_String=m_no.substring(3, m_no.length());
	                             int x=(Integer.parseInt(m_no_sub_int_String)+1);
	                             System.out.println(x);
	                             System.out.println(From_Sub_Char_String);
	                             int zero_count=0;
	                             String zero_padding="";
	                             while(m_no_sub_int_String.charAt(zero_count)=='0')
	                             {
	                            	 zero_padding=zero_padding+'0';
	                            	 zero_count++;
	                             }	                       
	                             if(From_Sub_Int_String.length()==(zero_padding+x).length())
	                             {
	                                m_no=From_Sub_Char_String+zero_padding+x;
	                             }
	                             else
	                             {
	                            	m_no=From_Sub_Char_String+(zero_padding+x).substring(1);
	                             }
	                             System.out.println(m_no);
	                             d=d+1;
	                       } 
	                   m_no=From;
	                   if(Vendor_Stock.vendorType(a, R_By_Code).equals("DEPARTMENTAL")) // IF THE METERS ARE BEING RECEIVED BY A DEPARTMENTAL STAFF
	                   {
	                       if(flag==1) // IF ANY OF THE METERS IN THE RANGE ALREADY EXISTS IN THE STOCK
	                       {
	                           R_Starting_Meter_No.setText("") ;
	                           R_Last_Meter_No.setText("");
	                           R_Received_By.setValue(null);
	                           R_Order_No.setText("");
	                           R_ERP_Reference.setText("");                       
	                       }
	                       else //IF ALL THE METERS IN THE RANGE ARE CLEAR TO BE RECEIVED
	                       {
	                       while(f<=batch_amount+1)
	                       {     
	                             String sql="Insert into receive values(?,?,?,?,?,?,?)";
	                             PreparedStatement pst=a.prepareStatement(sql);
	                             pst.setString(1, m_no);
	                             pst.setString(2, Phase);
	                             pst.setDate(3, R_Date);
	                             pst.setInt(4, R_By_Code);
	                             pst.setString(5,ERP);
	                             pst.setString(6,WO_PO_No );
	                             pst.setDate(7,R_WO_Date);
	                             pst.execute();
	                             Stock.addToStock(a, m_no, Phase, R_By_Code);
	                             String m_no_sub_int_String=m_no.substring(3, m_no.length());
	                             int x=(Integer.parseInt(m_no_sub_int_String)+1);
	                             int zero_count=0;
	                             String zero_padding="";
	                             while(m_no_sub_int_String.charAt(zero_count)=='0')
	                             {
	                            	 zero_padding=zero_padding+'0';
	                            	 zero_count++;
	                             }
	                             System.out.println(zero_padding+x);
	                             if(From_Sub_Int_String.length()==(zero_padding+x).length())
	                             {
	                                m_no=From_Sub_Char_String+zero_padding+x;
	                             }
	                             else
	                             {
	                            	m_no=From_Sub_Char_String+(zero_padding+x).substring(1);
	                             }
	                             System.out.println(m_no);
	                             f=f+1;                                
	                        }
	                       Messages.ShowInfoMessage("Stock Successfully Updated","Success"); 
	                       R_Starting_Meter_No.setText("") ;
	                           R_Last_Meter_No.setText("");
	                           R_Received_By.setValue(null);
	                           R_Order_No.setText("");
	                           R_ERP_Reference.setText("");
	                   }    
	                   }
	                   else // IF THE METERS ARE BEING RECEIVED BY CONTRACTUAL AGENCY
	                   {
	                        while(f<=batch_amount+1)
	                       {                       
	                             String sql="Insert into receive values(?,?,?,?,?,?,?)";
	                             PreparedStatement pst=a.prepareStatement(sql);
	                             pst.setString(1, m_no);
	                             pst.setString(2, Phase);
	                             pst.setDate(3, R_Date);
	                             pst.setInt(4, R_By_Code);
	                             pst.setString(5,ERP);
	                             pst.setString(6,WO_PO_No );
	                             pst.setDate(7,R_WO_Date);
	                             pst.execute();
	                             Vendor_Stock.issueToVendor(a, R_By_Code,m_no, Phase,R_Date,WO_PO_No,R_WO_Date,null);
	                             String m_no_sub_int_String=m_no.substring(3, m_no.length());
	                             int x=(Integer.parseInt(m_no_sub_int_String)+1);
	                             int zero_count=0;
	                             String zero_padding="";
	                             while(m_no_sub_int_String.charAt(zero_count)=='0')
	                             {
	                            	 zero_padding=zero_padding+'0';
	                            	 zero_count++;
	                             }	                       
	                             if(From_Sub_Int_String.length()==(zero_padding+x).length())
	                             {
	                                m_no=From_Sub_Char_String+zero_padding+x;
	                             }
	                             else
	                             {
	                            	m_no=From_Sub_Char_String+(zero_padding+x).substring(1);
	                             }
	                             f=f+1; 
	                       }
	                       Messages.ShowInfoMessage("Vendor Stock Successfully Updated","Success"); 
	                       R_Starting_Meter_No.setText("") ;
	                           R_Last_Meter_No.setText("");
	                           R_Received_By.setValue(null);
	                           R_Order_No.setText("");
	                           R_ERP_Reference.setText("");
	                   }
	               }
	               catch(Exception e)
	               {
	            	   Alert a=new Alert(Alert.AlertType.ERROR);
	                   a.setTitle("Error");
	                   a.setContentText(e.toString());
	                   a.showAndWait();
	               }
	            }
	         }                                              
          }
          catch(Exception e)
          {
            Alert a=new Alert(Alert.AlertType.ERROR);
            a.setTitle("Error");
            a.setContentText(e.toString());
            a.showAndWait();
          }
    }
    @FXML 
    private void handle_R_Receive_Used_Meter_Button_Event(ActionEvent event)
    {
        try{
        Parent root=FXMLLoader.load(getClass().getResource("/UI/Receive Used Meter.fxml"));
        Scene Receive_Used_Meter_Scene=new Scene(root);
        Stage Receive_Used_Meter_Stage=new Stage();
        Receive_Used_Meter_Stage.setScene(Receive_Used_Meter_Scene);
        Receive_Used_Meter_Stage.setResizable(false);
        Receive_Used_Meter_Stage.setTitle("Receive Used Meter");
        Receive_Used_Meter_Stage.getIcons().add(new Image(getClass().getResourceAsStream("/UI/ABC.png")));
        Receive_Used_Meter_Stage.show();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    @FXML
    private TextField I_Starting_Meter_No;
    @FXML
    private TextField I_Last_Meter_No;
    @FXML
    private ComboBox I_Meter_Phase;
    @FXML
    private ComboBox I_Issued_To;
    @FXML
    private DatePicker I_Issue_Date;
    @FXML
    private TextField I_Order_No;
    @FXML
    private DatePicker I_Order_Date;
    @FXML
    private Button I_Issue_Button;
    @FXML
    private Button I_Issue_Used_Meter_Button;
    @FXML
    private ComboBox I_Purpose;
    @FXML
    private void handle_Issue_Button_Event(ActionEvent event)
    {   
        try
        {
            
            if(I_Starting_Meter_No.getText().length()==0)
            {
                throw new BlankFieldError("Please Enter the Starting Meter Number");
            }
            if(I_Meter_Phase.getSelectionModel().getSelectedItem()==null)
            {
                throw new BlankFieldError("Please select the Meter Phase");
            }
            if(I_Issued_To.getSelectionModel().getSelectedItem()==null)
            {
                throw new BlankFieldError("Please select the Vendor");
            } 
            if(I_Issue_Date.getValue()==null)
            {
                throw new BlankFieldError("Please Enter Issue Date");
            }
            if(I_Order_No.getText().length()!=0 && I_Order_Date.getValue()==null)
           {
                 throw new BlankFieldError("Please Enter WO/PO Date");
           }
           if(I_Purpose.getSelectionModel().getSelectedItem()==null)
           {
               throw new BlankFieldError("Please Select Purpose");
           }
           if(I_Order_No.getText().length()==0 && I_Order_Date.getValue()!=null)
           {
                 throw new BlankFieldError("Please Enter WO/PO Number");
           }
           if(TDates.compareDateOnly(TDates.getUTILDate(I_Issue_Date.getValue()), Receive.getReceiveDate(a,I_Starting_Meter_No.getText()))==2)
           {
                throw new DataEntryError("No Such Meter Was Received ");
           }
           if(TDates.compareDateOnly(TDates.getUTILDate(I_Issue_Date.getValue()), Receive.getReceiveDate(a,I_Starting_Meter_No.getText()))<0)
           {         
                throw new BlankFieldError("Meters were Received on "+Receive.getReceiveDate(a, I_Starting_Meter_No.getText()));
           }
           
            Date W_P_Date=null;
            
           if(I_Order_No.getText().length()==0 && I_Order_Date.getValue()==null)
           {
              W_P_Date=null;
           }
           else
           {
               W_P_Date=TDates.getSQLDate(I_Order_Date.getValue());
               if(I_Issue_Date.getValue().compareTo(I_Order_Date.getValue())<0)
               {
                   I_Issue_Date.getEditor().clear();
                   I_Issue_Date.setValue(null);
                   I_Order_Date.getEditor().clear();
                   I_Order_Date.setValue(null);
                   throw new DataEntryError("Issue Date before Work Order Date");
               }
           }
           
           String Starting_Meter=I_Starting_Meter_No.getText();
           String Last_Meter=I_Last_Meter_No.getText();
           String Phase=I_Meter_Phase.getSelectionModel().getSelectedItem().toString();
           Date I_Date=TDates.getSQLDate(I_Issue_Date.getValue());
           int Vendor=Vendor_Stock.getVendorCode(a, I_Issued_To.getSelectionModel().getSelectedItem().toString());
           String W_P_No=I_Order_No.getText();
           String Purpose=null;
           if(I_Purpose.getSelectionModel().getSelectedItem().toString().equals("NEW SERVICE CONNECTION"))
           {
               Purpose="NSC";
           }
           if(I_Purpose.getSelectionModel().getSelectedItem().toString().equals("DEFECTIVE METER REPLACEMENT"))
           {
               Purpose="DMR";
           }
           if(I_Purpose.getSelectionModel().getSelectedItem().toString().equals("ELECTROMAGNETIC METER REPLACEMENT"))
           {
               Purpose="EMR";
           }
           if(I_Purpose.getSelectionModel().getSelectedItem().toString().equals("OTHER"))
           {
               Purpose="OTHER";
           }
           if(I_Last_Meter_No.getText().length()==0)
                {
                    //System.out.println("Blank To");
                        try
                        {  
                             if(Stock.issueAllowed(a, Starting_Meter))
                             {
                                 String sql="Insert into issue values(?,?,?,?,?,?,?)";
                                 PreparedStatement pst=a.prepareStatement(sql);
                                 pst.setString(1, Starting_Meter);
                                 pst.setString(2, Phase);
                                 pst.setDate(3, I_Date);
                                 pst.setInt(4, Vendor);
                                 pst.setString(5,W_P_No);
                                 pst.setDate(6, W_P_Date);
                                 pst.setString(7,Purpose);
                                 pst.execute();            
                                 Stock.issueFromStock(a, Starting_Meter);
                                 Vendor_Stock.issueToVendor(a, Vendor, Starting_Meter,Phase, I_Date, W_P_No, W_P_Date, Purpose);                 
                                 Messages.ShowInfoMessage("Meter Successfully Issued", "Success");
                                 I_Starting_Meter_No.setText("");
                                 I_Last_Meter_No.setText("");
                             }                                          
                             else
                             {
                                 throw new DataEntryError("No Such Meter In Stock");
                             }
                        }
                        catch(DataEntryError e)
                        {
                            Messages.ShowErrorMessage(e.toString(), "Error");
                        }
                        catch(SQLException e)
                        {
                            Messages.ShowErrorMessage(e.toString(), "Error");
                        }
                        catch(Exception e)
                        {
                            Messages.ShowErrorMessage(e.toString(), "Error");
                        }
                }
           else
           {
                   String Starting_Meter_Sub_Int_String=Starting_Meter.substring(3, Starting_Meter.length());
                   int Starting_Int=Integer.parseInt(Starting_Meter_Sub_Int_String);
                   String Starting_Meter_Sub_Char_String=Starting_Meter.substring(0, 3);
                   String Last_Meter_Sub_Int_String=Last_Meter.substring(3, Last_Meter.length());
                   int Last_Int=Integer.parseInt(Last_Meter_Sub_Int_String);
                   String Last_Meter_Sub_Char_String=Last_Meter.substring(0,3);
                   int batch_amount=Last_Int-Starting_Int;
                   if(batch_amount>9)
                   {
                	   throw new GeneralError("Please Issue a maximum of 10 Meters at a time");
                   }
                   if (!Starting_Meter_Sub_Char_String.equals(Last_Meter_Sub_Char_String) || I_Starting_Meter_No.getText().length()!=I_Last_Meter_No.getText().length())
                   {
                       throw new DataEntryError("Device Series Mismatch");
                   }
                   try
                   {
                       int d=1;
                       int x_flag=0;
                       String m_no=Starting_Meter;
                           while(d<=batch_amount+1)
                           {

                                 if(Stock.issueAllowed(a, m_no))
                               {
                                     x_flag=1;
                                     String sql="Insert into issue values(?,?,?,?,?,?,?)";
                                     PreparedStatement pst=a.prepareStatement(sql);
                                     pst.setString(1, m_no);
                                     pst.setString(2, Phase);
                                     pst.setDate(3, I_Date);
                                     pst.setInt(4, Vendor);
                                     pst.setString(5,W_P_No);
                                     pst.setDate(6, W_P_Date);
                                     pst.setString(7,Purpose);
                                     pst.execute();
                                     Stock.issueFromStock(a, m_no);
                                     Vendor_Stock.issueToVendor(a, Vendor, m_no,Phase, I_Date, W_P_No, W_P_Date, Purpose);
                               }
                               else
                               {                                          
                                     I_Starting_Meter_No.setText("");
                                     I_Last_Meter_No.setText("");
                                     throw new DataEntryError(m_no+" is not in Stock");
                               }
                                 String m_no_sub_int_String=m_no.substring(3, m_no.length());
	                             int x=(Integer.parseInt(m_no_sub_int_String)+1);
                                 int zero_count=0;
	                             String zero_padding="";
	                             while(m_no_sub_int_String.charAt(zero_count)=='0')
	                             {
	                            	 zero_padding=zero_padding+'0';
	                            	 zero_count++;
	                             }	                       
	                             if(Starting_Meter_Sub_Int_String.length()==(zero_padding+x).length())
	                             {
	                                m_no=Starting_Meter_Sub_Char_String+zero_padding+x;
	                             }
	                             else
	                             {
	                            	m_no=Starting_Meter_Sub_Char_String+(zero_padding+x).substring(1);
	                             }
                                 d=d+1;              

                           }
                           if(x_flag==1)
                           {
                              Messages.ShowInfoMessage("Stock Successfully Updated","Success");
                              I_Starting_Meter_No.setText("");
                              I_Last_Meter_No.setText("");
                           }
                           else
                           {
                               throw new DataEntryError("Please Check Your Data");
                           }
                    }
                    catch(Exception e)
                    {                  
                        Messages.ShowErrorMessage(e.toString(), "Error");
                    }
           }
        }
        catch(Exception e)
        {
            Messages.ShowErrorMessage(e.toString(), "Error");
        }
    }
    @FXML
    private void handle_Issue_Used_Meter_Button_Event(ActionEvent event)
    {
        try
        {
            if(I_Starting_Meter_No.getText().length()==0)
            {
                throw new BlankFieldError("Please Enter the Starting Meter Number");
            }
            if(I_Meter_Phase.getSelectionModel().getSelectedItem()==null)
            {
                throw new BlankFieldError("Please select the Meter Phase");
            }
            if(I_Issued_To.getSelectionModel().getSelectedItem()==null)
            {
                throw new BlankFieldError("Please select the Vendor");
            } 
            if(I_Issue_Date.getValue()==null)
            {
                throw new BlankFieldError("Please Enter Issue Date");
            }
            if(I_Order_No.getText().length()!=0 && I_Order_Date.getValue()==null)
           {
                 throw new BlankFieldError("Please Enter WO/PO Date");
           }
           if(I_Purpose.getSelectionModel().getSelectedItem()==null)
           {
               throw new BlankFieldError("Please Select Purpose");
           }
           if(I_Order_No.getText().length()==0 && I_Order_Date.getValue()!=null)
           {
                 throw new BlankFieldError("Please Enter WO/PO Number");
           }
           if(TDates.compareDateOnly(TDates.getUTILDate(I_Issue_Date.getValue()), Receive.getReceive_Utilized_Date(a,I_Starting_Meter_No.getText()))==2)
           {
                throw new DataEntryError("No Such Meter Was Received ");
           }
           if(TDates.compareDateOnly(TDates.getUTILDate(I_Issue_Date.getValue()), Receive.getReceive_Utilized_Date(a,I_Starting_Meter_No.getText()))<0)
           {         
                throw new BlankFieldError("Meters were Received on "+Receive.getReceive_Utilized_Date(a, I_Starting_Meter_No.getText()));
           }
           
            Date W_P_Date=null;
            
           if(I_Order_No.getText().length()==0 && I_Order_Date.getValue()==null)
           {
              W_P_Date=null;
           }
           else
           {
               W_P_Date=TDates.getSQLDate(I_Order_Date.getValue());
               if(I_Issue_Date.getValue().compareTo(I_Order_Date.getValue())<0)
               {
                   I_Issue_Date.getEditor().clear();
                   I_Issue_Date.setValue(null);
                   I_Order_Date.getEditor().clear();
                   I_Order_Date.setValue(null);
                   throw new DataEntryError("Issue Date before Work Order Date");
               }
           }
           
           String Starting_Meter=I_Starting_Meter_No.getText();
           String Last_Meter=I_Last_Meter_No.getText();
           String Phase=I_Meter_Phase.getSelectionModel().getSelectedItem().toString();
           Date I_Date=TDates.getSQLDate(I_Issue_Date.getValue());
           int Vendor=Vendor_Stock.getVendorCode(a, I_Issued_To.getSelectionModel().getSelectedItem().toString());
           String W_P_No=I_Order_No.getText();
           String Purpose=null;
           if(I_Purpose.getSelectionModel().getSelectedItem().toString().equals("NEW SERVICE CONNECTION"))
           {
               Purpose="NSC";
           }
           if(I_Purpose.getSelectionModel().getSelectedItem().toString().equals("DEFECTIVE METER REPLACEMENT"))
           {
               Purpose="DMR";
           }
           if(I_Purpose.getSelectionModel().getSelectedItem().toString().equals("ELECTROMAGNETIC METER REPLACEMENT"))
           {
               Purpose="EMR";
           }
           if(I_Purpose.getSelectionModel().getSelectedItem().toString().equals("OTHER"))
           {
               Purpose="OTHER";
           }
           if(I_Last_Meter_No.getText().length()==0)
                {
                    //System.out.println("Blank To");
                        try
                        {  
                             if(Stock.issueAllowed(a, Starting_Meter))
                             {
                                 String sql="Insert into issue values(?,?,?,?,?,?,?)";
                                 PreparedStatement pst=a.prepareStatement(sql);
                                 pst.setString(1, Starting_Meter);
                                 pst.setString(2, Phase);
                                 pst.setDate(3, I_Date);
                                 pst.setInt(4, Vendor);
                                 pst.setString(5,W_P_No);
                                 pst.setDate(6, W_P_Date);
                                 pst.setString(7,Purpose);
                                 pst.execute();            
                                 Stock.issueFromStock(a, Starting_Meter);
                                 Vendor_Stock.issueToVendor(a, Vendor, Starting_Meter,Phase, I_Date, W_P_No, W_P_Date, Purpose);                 
                                 Messages.ShowInfoMessage("Meter Successfully Issued", "Success");
                                 I_Starting_Meter_No.setText("");
                                 I_Last_Meter_No.setText("");
                             }                                          
                             else
                             {
                                 throw new DataEntryError("No Such Meter In Stock");
                             }
                        }
                        catch(DataEntryError e)
                        {
                            Messages.ShowErrorMessage(e.toString(), "Error");
                        }
                        catch(SQLException e)
                        {
                            Messages.ShowErrorMessage(e.toString(), "Error");
                        }
                        catch(Exception e)
                        {
                            Messages.ShowErrorMessage(e.toString(), "Error");
                        }
                }
           else
           {
                   String Starting_Meter_Sub=Starting_Meter.substring(1, Starting_Meter.length());
                   int Starting_Int=Integer.parseInt(Starting_Meter_Sub);
                   Starting_Meter_Sub=Starting_Meter.substring(0, 1);
                   String Last_Meter_Sub=Last_Meter.substring(1, Last_Meter.length());
                   int Last_Int=Integer.parseInt(Last_Meter_Sub);
                   Last_Meter_Sub=Last_Meter.substring(0,1);
                   int batch_amount=Last_Int-Starting_Int;
                   if (!Starting_Meter_Sub.equals(Last_Meter_Sub) || I_Starting_Meter_No.getText().length()!=I_Last_Meter_No.getText().length())
                   {
                       throw new DataEntryError("Device Series Mismatch");
                   }
                   try
                   {
                       int d=1;
                       int x_flag=0;
                       String m_no=Starting_Meter;
                           while(d<=batch_amount+1)
                           {

                                 if(Stock.issueAllowed(a, m_no))
                               {
                                     x_flag=1;
                                     String sql="Insert into issue values(?,?,?,?,?,?,?)";
                                     PreparedStatement pst=a.prepareStatement(sql);
                                     pst.setString(1, m_no);
                                     pst.setString(2, Phase);
                                     pst.setDate(3, I_Date);
                                     pst.setInt(4, Vendor);
                                     pst.setString(5,W_P_No);
                                     pst.setDate(6, W_P_Date);
                                     pst.setString(7,Purpose);
                                     pst.execute();
                                     Stock.issueFromStock(a, m_no);
                                     Vendor_Stock.issueToVendor(a, Vendor, m_no,Phase, I_Date, W_P_No, W_P_Date, Purpose);
                               }
                               else
                               {                                          
                                     I_Starting_Meter_No.setText("");
                                     I_Last_Meter_No.setText("");
                                     throw new DataEntryError(m_no+" is not in Stock");
                               }
                                 int x=(Integer.parseInt(m_no.substring(1, m_no.length()))+1);
                                 m_no=Starting_Meter_Sub+x;
                                 d=d+1;              

                           }
                           if(x_flag==1)
                           {
                              Messages.ShowInfoMessage("Stock Successfully Updated","Success");
                              I_Starting_Meter_No.setText("");
                              I_Last_Meter_No.setText("");
                           }
                           else
                           {
                               throw new DataEntryError("Please Check Your Data");
                           }
                    }
                    catch(Exception e)
                    {                  
                        Messages.ShowErrorMessage(e.toString(), "Error");
                    }
           }
        }
        catch(Exception e)
        {
            Messages.ShowErrorMessage(e.toString(), "Error");
        }
    }
    @FXML
    private TextField U_Meter_No;
    @FXML
    private ComboBox U_Meter_Phase;
    @FXML
    private TextField U_Consumer_Reference_No;
    @FXML
    private ComboBox U_Utilized_By;
    @FXML
    private DatePicker U_Utilization_Date;
    @FXML
    private ComboBox U_Purpose;
    @FXML
    private Button Utilize_Button;    
    @FXML
    private void handle_Utilize_Button_Event(ActionEvent event)
    {
        try
        {
            int flag=0;
            Date U_Date=null;
            if(U_Meter_No.getText().length()==0)
            {
                throw new BlankFieldError("Enter Meter No");
            } 
            if(U_Meter_Phase.getSelectionModel().getSelectedItem()==null)
            {
                throw new BlankFieldError("Select Meter Phase");
            }
            if(U_Consumer_Reference_No.getText().length()==0)
            {
                throw new BlankFieldError("Enter Con Ref No");
            }
            if(U_Utilized_By.getSelectionModel().getSelectedItem()==null)
            {
                throw new BlankFieldError("Select Vendor");
            }
            if(U_Utilization_Date.getValue()==null)
            {
                throw new BlankFieldError("Enter Utilization Date");
            }                              
            if(U_Purpose.getSelectionModel().getSelectedItem()==null)
            {
                throw new BlankFieldError("Please Select Purpose of Utilization");
            }
            String Met_No=U_Meter_No.getText();
            String Con_No=U_Consumer_Reference_No.getText();
            if(Return.DirectReceiveFromStoreByOutSourced(a, Met_No)==true)
            {
                if(Receive.getReceiveDate(a, Met_No)==null)
                {
                    throw new DataEntryError("No Such Meter was Received By the Contractor from Store");
                }

                if(TDates.getUTILDate(U_Utilization_Date.getValue()).compareTo(Receive.getReceiveDate(a, Met_No))<0)
                {
                    throw new DataEntryError("Utilization Date before Date of Direct Receive from Store");
                }               
                U_Date=TDates.getSQLDate(U_Utilization_Date.getValue());
                int Vendor_Code=Vendor_Stock.getVendorCode(a, U_Utilized_By.getSelectionModel().getSelectedItem().toString());
                String Purpose="";
                String M_Phase=U_Meter_Phase.getSelectionModel().getSelectedItem().toString();
                if(!Vendor_Stock.deviceWithVendor(a, Vendor_Code, Met_No))
                {
                    throw new DataEntryError("No Such Meter in the Custody of this Vendor");
                }
                if(U_Purpose.getSelectionModel().getSelectedItem().toString().equals("NEW SERVICE CONNECTION"))
                {
                    Purpose="NSC";
                }
                if(U_Purpose.getSelectionModel().getSelectedItem().toString().equals("DEFECTIVE METER REPLACEMENT"))
                {
                    Purpose="DMR";
                    U_Meter_No.setText(" ");
                    U_Consumer_Reference_No.setText(" ");
                    U_Utilized_By.setValue(null);
                    U_Utilization_Date.getEditor().clear();
                    U_Utilization_Date.setValue(null);
                    throw new Meter_Rep_Data_Entry(Met_No,Con_No,U_Date,Purpose,M_Phase,Vendor_Code);
                }
                if(U_Purpose.getSelectionModel().getSelectedItem().toString().equals("ELECTROMAGNETIC METER REPLACEMENT"))
                {
                    Purpose="CMR";
                    U_Meter_No.setText(" ");
                    U_Consumer_Reference_No.setText(" ");
                    U_Utilized_By.setValue(null);
                    U_Utilization_Date.getEditor().clear();
                    U_Utilization_Date.setValue(null);
                    throw new Meter_Rep_Data_Entry(Met_No,Con_No,U_Date,Purpose,M_Phase,Vendor_Code);
                }
                if(U_Purpose.getSelectionModel().getSelectedItem().toString().equals("OTHER"))
                {
                    Purpose="OTHER";
                    U_Meter_No.setText(" ");
                    U_Consumer_Reference_No.setText(" ");
                    U_Utilized_By.setValue(null);
                    U_Utilization_Date.getEditor().clear();
                    U_Utilization_Date.setValue(null);
                    throw new Meter_Rep_Data_Entry(Met_No,Con_No,U_Date,Purpose,M_Phase,Vendor_Code);
                }
                if(Utilization.utilizeDevice(a, Met_No, M_Phase, Con_No, Vendor_Code, U_Date, Purpose))
                {
                   Messages.ShowInfoMessage("Device Utilized Successfully", "Success");
                    U_Meter_No.setText("");
                    U_Consumer_Reference_No.setText("");
                    U_Utilized_By.setValue(null);
                    U_Utilization_Date.setValue(null);
                }
                else
                {
                    throw new DataEntryError("Please Check Your Data");
                }
            }
            else
            {
                 if(TDates.getIssueDate(a, Met_No)==null)
                 {
                    throw new DataEntryError("No Such Meter Was Issued");
                 }
                 if(TDates.getUTILDate(U_Utilization_Date.getValue()).compareTo(TDates.getIssueDate(a, Met_No))<0)
                 {
                    throw new DataEntryError("Utilization Date before Date of Issue");
                 }
                 
                U_Date=TDates.getSQLDate(U_Utilization_Date.getValue());
                int Vendor_Code=Vendor_Stock.getVendorCode(a, U_Utilized_By.getSelectionModel().getSelectedItem().toString());
                if(Vendor_Stock.deviceWithVendor(a,Vendor_Code,Met_No)==false)
                {
                	throw new DataEntryError("Device not in Vendor Stock");
                }
                String M_Phase=U_Meter_Phase.getSelectionModel().getSelectedItem().toString();
                String Purpose=" ";
                if(U_Purpose.getSelectionModel().getSelectedItem().toString().equals("NEW SERVICE CONNECTION"))
                {
                    Purpose="NSC";
                }
                if(U_Purpose.getSelectionModel().getSelectedItem().toString().equals("DEFECTIVE METER REPLACEMENT"))
                {
                    Purpose="DMR";
                    U_Meter_No.setText(" ");
                    U_Consumer_Reference_No.setText(" ");
                    U_Utilized_By.setValue(null);
                    U_Utilization_Date.getEditor().clear();
                    U_Utilization_Date.setValue(null);
                    throw new Meter_Rep_Data_Entry(Met_No,Con_No,U_Date,Purpose,M_Phase,Vendor_Code);
                }
                if(U_Purpose.getSelectionModel().getSelectedItem().toString().equals("ELECTROMAGNETIC METER REPLACEMENT"))
                {
                    Purpose="CMR";
                    U_Meter_No.setText(" ");
                    U_Consumer_Reference_No.setText(" ");
                    U_Utilized_By.setValue(null);
                    U_Utilization_Date.getEditor().clear();
                    U_Utilization_Date.setValue(null);
                    throw new Meter_Rep_Data_Entry(Met_No,Con_No,U_Date,Purpose,M_Phase,Vendor_Code);
                }
                if(U_Purpose.getSelectionModel().getSelectedItem().toString().equals("OTHER"))
                {
                    Purpose="OTHER";
                    U_Meter_No.setText(" ");
                    U_Consumer_Reference_No.setText(" ");
                    U_Utilized_By.setValue(null);
                    U_Utilization_Date.getEditor().clear();
                    U_Utilization_Date.setValue(null);
                    throw new Meter_Rep_Data_Entry(Met_No,Con_No,U_Date,Purpose,M_Phase,Vendor_Code);
                }
                if(!Vendor_Stock.deviceWithVendor(a, Vendor_Code, Met_No))
                {
                    throw new DataEntryError("No Such Meter in the Custody of this Vendor");
                }
                Utilization.utilizeDevice(a, Met_No, M_Phase, Con_No, Vendor_Code, U_Date, Purpose);                
                Messages.ShowInfoMessage( "Device Utilized Successfully", "Success");
                U_Meter_No.setText("");
                U_Consumer_Reference_No.setText("");
                U_Utilized_By.setValue(null);
                U_Utilization_Date.getEditor().clear();
                U_Utilization_Date.setValue(null);
            }               
        }       
        catch(Meter_Rep_Data_Entry M)
        {
            Meter_Rep_Data_Entry_Form F=new Meter_Rep_Data_Entry_Form(a,M.Con_Ref,M.U_Date,M.Met_No,M.Vendor_Code,M.Purpose,M.Phase);
            F.setVisible(true);
        }
        catch(BlankFieldError b)
        {
            Messages.ShowErrorMessage(b.toString(), "Error");
        }
        catch(Exception e)
        {
            Messages.ShowErrorMessage(e.toString(), "Error");
        }
    }
    @FXML
    private TextField Ret_Meter_No;
    @FXML
    private ComboBox Ret_Meter_Phase;
    @FXML
    private ComboBox Ret_Returned_By;
    @FXML
    private TextField Ret_Order_No;
    @FXML
    private DatePicker Ret_Order_Date;
    @FXML
    private DatePicker Ret_Return_Date;
    @FXML
    private TextField Ret_Reason;
    @FXML
    private Button Return_Button;
    @FXML
    private void handle_Return_Button_Event(ActionEvent event)
    {
        try
        {
            if(Ret_Meter_No.getText().length()==0)
            {
                throw new BlankFieldError("Please Enter the Meter No.");
            }
            if(Ret_Meter_Phase.getSelectionModel().getSelectedItem()==null)
            {
                throw new BlankFieldError("Please Select Meter Phase");
            }
            if(Ret_Returned_By.getSelectionModel().getSelectedItem()==null)
            {
                throw new BlankFieldError("Please Select the Vendor");
            }
            if(Ret_Order_No.getText().length()==0 && Ret_Order_Date.getValue()!=null)
            {
                throw new BlankFieldError("Please Enter Both Work Order No and Work Order Date");
            }
            if(Ret_Order_No.getText().length()!=0 && Ret_Order_Date.getValue()==null)
            {
                throw new BlankFieldError("Please Enter Both Work Order No and Work Order Date");
            }
            if(Ret_Reason.getText().length()<3)
            {
                throw new DataEntryError("String Length Less than 3.Please Enter the Correct Reason for Return");
            }
            String Ret_Met_No=Ret_Meter_No.getText();
            String Ret_Met_Phase=Ret_Meter_Phase.getSelectionModel().getSelectedItem().toString();
            String Ret_WO_No=Ret_Order_No.getText();
            Date Ret_WO_date=null;
            int Ret_By=Vendor_Stock.getVendorCode(a,Ret_Returned_By.getSelectionModel().getSelectedItem().toString());
            if(Ret_Order_Date.getValue()==null)
            {
                Ret_WO_date=null;
            }
            else
            {
                Ret_WO_date=TDates.getSQLDate(Ret_Order_Date.getValue());
            }
            Date Ret_Date=null;
            if(!Vendor_Stock.deviceWithVendor(a, Ret_By, Ret_Met_No))
            {
                throw new DataEntryError("No Such Meter in the Custody of this Vendor");
            }
            if(Return.DirectReceiveFromStoreByOutSourced(a, Ret_Met_No))
            {           
                throw new DataEntryError("Meter Directly Received from Store by Contractual Vendor Can not be returned using this software. They can only be utilized");
            }
            if((TDates.getUTILDate(Ret_Return_Date.getValue())).compareTo(TDates.getIssueDate(a, Ret_Met_No))<0)
            {
                throw new DataEntryError("Return Date Before Issue Date");
            }
            Ret_Date=TDates.getSQLDate(Ret_Return_Date.getValue());
            String Reason=Ret_Reason.getText();
            Return.performReturn(a, Ret_Met_No, Ret_Met_Phase, Ret_By);
            Return.updateReturnTable(a, Ret_Met_No, Ret_Met_Phase, Ret_By, Ret_WO_No, Ret_WO_date, Ret_Date, Reason);
            Messages.ShowInfoMessage("Meter Successfully Returned to Local Stock","Success");
            Ret_Meter_No.setText("");
            Ret_Order_No.setText("");
            Ret_Order_Date.setValue(null);
            Ret_Order_Date.getEditor().setText("");
            Ret_Return_Date.setValue(null);
            Ret_Return_Date.getEditor().setText("");            
            Ret_Returned_By.setValue(null);
            Ret_Reason.setText("");                   
        }
        catch(BlankFieldError e)
        {
            Messages.ShowErrorMessage(e.toString(), "Error");
        }
        catch(DataEntryError e)
        {
            Messages.ShowErrorMessage(e.toString(), "Error");
        }
        catch(Exception e)
        {
            Messages.ShowErrorMessage(e.toString(), "Error");
        }
    }
    @FXML
    private RadioButton Report_Daily_Report_Issue_RadioButton;
    @FXML
    private RadioButton Report_Daily_Report_Receive_RadioButton;
    @FXML
    private DatePicker Report_Daily_Report_Date;
    @FXML
    private Button Report_Daily_Report_OK_Button;
    @FXML
    private void handle_Daily_Report_OK_Button_Event(ActionEvent event)
    {
        try
        {
            ResultSet Rst;
            if(Report_Daily_Report_Date.getValue()==null)
            {
                throw new BlankFieldError("Please Enter Report Date");
            }
            if(!Report_Daily_Report_Issue_RadioButton.isSelected() && !Report_Daily_Report_Receive_RadioButton.isSelected())
            {
                throw new BlankFieldError("Please Select Report Type");
            }
            if(Report_Daily_Report_Issue_RadioButton.isSelected())
            {
                System.out.println(Report_Daily_Report_Date.getValue());
                Date R_Date=null;
                R_Date=TDates.getSQLDate(Report_Daily_Report_Date.getValue());
                String R_Date_String=TDates.dateToString(R_Date);
                //String Current_Date=TDates.getCurrentDate();
                ExcelHandling e=new ExcelHandling("Issue"+R_Date_String+".xls","Sheet1");
                String Met_No;
                String Met_Phase;
                String Issue_Date;
                String Vendor_Name;
                String WO_No;
                String WO_Date;
                String Purpose;
                String SQL="SELECT ISSUE.*,VENDOR.VENDOR_NAME FROM ISSUE,VENDOR WHERE ISSUE_DATE=? AND ISSUE.VENDOR_CODE=VENDOR.VENDOR_CODE";
                int i=1;
                e.writeCell(0, 0, "SERIAL NO");
                e.writeCell(0, 1, "METER NO");
                e.writeCell(0, 2, "METER_PHASE");
                e.writeCell(0, 3, "ISSUE DATE(yyyy-mm-dd)");
                e.writeCell(0, 4, "ISSUED TO");
                e.writeCell(0, 5, "WO_No");
                e.writeCell(0, 6, "WO_Date");
                e.writeCell(0, 7,"PURPOSE");
                PreparedStatement pst=a.prepareStatement(SQL);
                pst.setDate(1, R_Date);
                Rst=pst.executeQuery();
                while(Rst.next())
                {
                    Met_No=Rst.getString("METER_NO");
                    Met_Phase=Rst.getString("METER_PHASE");
                    Issue_Date=TDates.dateToString(Rst.getDate("ISSUE_DATE"));
                    Vendor_Name=Rst.getString("VENDOR_NAME");
                    WO_No=Rst.getString("WO_NO");
                    if(Rst.getDate("WO_DATE")==null)
                    {
                      WO_Date="";
                    }
                    else
                    {
                    WO_Date=TDates.dateToString(Rst.getDate("WO_DATE"));
                    }
                    Purpose=Rst.getString("PURPOSE");
                    e.writeCell(i, 0, i);
                    e.writeCell(i, 1, Met_No);
                    e.writeCell(i, 2, Met_Phase);  
                    e.writeCell(i, 3, Issue_Date);
                    e.writeCell(i, 4, Vendor_Name);
                    e.writeCell(i, 5, WO_No);
                    e.writeCell(i, 6, WO_Date);
                    e.writeCell(i, 7, Purpose);
                    i=i+1;
                }
                e.generate();
                Messages.ShowInfoMessage("Issue Report Generated Successfully in the Desktop/Reports folder", "Success");
            }
            if(Report_Daily_Report_Receive_RadioButton.isSelected())
            {
                System.out.println("Receive Report Selected");
                Date R_Date=null;
                R_Date=TDates.getSQLDate(Report_Daily_Report_Date.getValue());
                String R_Date_String=TDates.dateToString(R_Date);
                ExcelHandling e=new ExcelHandling("Receive"+R_Date_String+".xls","Sheet1");
                String Met_No;
                String Met_Phase;
                String Vendor_Name;
                String ERP_Ref_No;
                String Receive_Date;
                String SQL="SELECT RECEIVE.*,VENDOR.VENDOR_NAME FROM RECEIVE,VENDOR WHERE RECEIVE_DATE=? AND RECEIVE.VENDOR_CODE=VENDOR.VENDOR_CODE AND VENDOR.VENDOR_TYPE='DEPARTMENTAL'";
                int i=1;
                e.writeCell(0, 0, "SERIAL NO");
                e.writeCell(0, 1, "METER NO");
                e.writeCell(0, 2, "METER_PHASE");
                e.writeCell(0, 3, "RECEIVE DATE(yyyy-mm-dd)");
                e.writeCell(0, 4, "RECEIVED BY");
                e.writeCell(0, 5, "ERP REF NO");
                PreparedStatement pst=a.prepareStatement(SQL);
                pst.setDate(1, R_Date);
                Rst=pst.executeQuery();
                while(Rst.next())
                {
                   Met_No=Rst.getString("METER_NO");
                   Met_Phase=Rst.getString("METER_PHASE");
                   Receive_Date=TDates.dateToString(Rst.getDate("RECEIVE_DATE"));
                   Vendor_Name=Rst.getString("VENDOR_NAME");
                   ERP_Ref_No=Rst.getString("ERP_REF_NO");
                   e.writeCell(i, 0, i);
                   e.writeCell(i, 1, Met_No);
                   e.writeCell(i, 2, Met_Phase); 
                   e.writeCell(i, 3, Receive_Date);
                   e.writeCell(i, 4, Vendor_Name);
                   e.writeCell(i, 5, ERP_Ref_No);
                   i=i+1;
                }
                e.generate();
                Messages.ShowInfoMessage("Receive Report Generated Successfully in the Desktop/Reports folder", "Success");
                Report_Daily_Report_Date.setValue(null);
                Report_Daily_Report_Date.getEditor().setText("");
            }
        }
        catch(Exception e)
        {
            Messages.ShowInfoMessage(e.toString(), "Error");
        }
    }
    @FXML
    private ComboBox Report_Stock_Report_Type;
    @FXML
    private ComboBox Report_Stock_Report_Vendor;
    @FXML
    private Button Report_Stock_Report_OK_Button;
    @FXML
    private void handle_Stock_Report_OK_Button_Event(ActionEvent event)
    {
        try
        {
            if(Report_Stock_Report_Type.getSelectionModel().getSelectedItem()==null)
            {
                throw new BlankFieldError("Please Select Report Type");
            }
            if(Report_Stock_Report_Vendor.isDisabled()==false && Report_Stock_Report_Vendor.getSelectionModel().getSelectedItem()==null)
            {
                throw new BlankFieldError("Please Select Vendor");
            }
            if(Report_Stock_Report_Type.getSelectionModel().getSelectedItem().toString().equals("Local Stock"))
            {
                String R_Date_String=TDates.getCurrentDate();
                ExcelHandling e=new ExcelHandling("Stock_As_On_"+R_Date_String+".xls","Sheet1");
                String Met_No;
                String Met_Phase;
                String Received_By;
                String Receive_Date;
                String sql="SELECT distinct STOCK.METER_NO,STOCK.METER_PHASE,STOCK.RECEIVED_BY,VENDOR.VENDOR_NAME,RECEIVE.RECEIVE_DATE FROM STOCK,VENDOR,RECEIVE WHERE STOCK.RECEIVED_BY=VENDOR.VENDOR_CODE AND STOCK.METER_NO=RECEIVE.METER_NO union SELECT STOCK.*,null col1,null col2 FROM STOCK where stock.RECEIVED_BY is null and stock.METER_NO is not null union SELECT STOCK.*,VENDOR.VENDOR_NAME,RECEIVE_UTILIZED.receive_date FROM STOCK,VENDOR,receive_utilized where STOCK.meter_no=RECEIVE_UTILIZED.METER_NO and STOCK.RECEIVED_BY=VENDOR.VENDOR_CODE";                
                Statement S=a.createStatement();
                ResultSet rst=S.executeQuery(sql);
                int i=1;
                e.writeCell(0, 0, "SERIAL NO");
                e.writeCell(0, 1, "METER NO");
                e.writeCell(0, 2, "METER_PHASE");
                e.writeCell(0, 3, "RECEIVED BY");
                e.writeCell(0, 4, "RECEIVE DATE(yyyy-mm-dd)");
                while(rst.next())
                {
                   Met_No=rst.getString("METER_NO");
                   Met_Phase=rst.getString("METER_PHASE");                     
                   if(rst.getString("VENDOR_NAME")==null)
                   {
                       Received_By="RETURNED BY "+Return_Report.returnedBy(a, Met_No);
                       Receive_Date=" ";
                   }
                   else
                   {
                       Received_By=rst.getString("VENDOR_NAME");  
                       Receive_Date=TDates.dateToString(rst.getDate("RECEIVE_DATE"));
                   }
                   e.writeCell(i, 0, i);
                   e.writeCell(i, 1, Met_No);
                   e.writeCell(i, 2, Met_Phase); 
                   e.writeCell(i, 3, Received_By);
                   e.writeCell(i, 4, Receive_Date);
                   i=i+1;
                }
                e.generate();
                Messages.ShowInfoMessage("Local Stock Report Generated Successfully in Desktop/Reports", "Success");
            }
            if(Report_Stock_Report_Type.getSelectionModel().getSelectedItem().toString().equals("Vendor Stock"))
            {
                String R_Date_String=TDates.getCurrentDate();
                ExcelHandling e=new ExcelHandling("Stock_of_"+Report_Stock_Report_Vendor.getSelectionModel().getSelectedItem().toString()+"_As_On_"+R_Date_String+".xls","Sheet1");
                String Met_No;
                String Met_Phase;
                String Issue_Date;
                String WO_No;
                String WO_Date;
                String Purpose;
                String sql="SELECT * FROM V"+Integer.toString(Vendor_Stock.getVendorCode(a, Report_Stock_Report_Vendor.getSelectionModel().getSelectedItem().toString()));
                Statement S=a.createStatement();
                ResultSet rst=S.executeQuery(sql);
                int i=1;
                e.writeCell(0, 0, "SERIAL NO");
                e.writeCell(0, 1, "METER NO");
                e.writeCell(0, 2, "METER_PHASE");
                e.writeCell(0, 3, "ISSUE DATE(yyyy-mm-dd)");
                e.writeCell(0, 4, "WO_NO");
                e.writeCell(0, 5, "WO DATE(yyyy-mm-dd)");
                e.writeCell(0, 6, "PURPOSE");
                while(rst.next())
                {
                    Met_No=rst.getString("METER_NO");
                    Met_Phase=rst.getString("METER_PHASE");
                    Issue_Date=TDates.dateToString(rst.getDate("ISSUE_DATE"));
                    WO_No=rst.getString("WO_NO");
                    if(rst.getDate("WO_DATE")==null)
                        {
                          WO_Date="";
                        }
                    else
                        {
                        WO_Date=TDates.dateToString(rst.getDate("WO_DATE"));
                        }
                    Purpose=rst.getString("PURPOSE");
                   e.writeCell(i, 0, i);
                   e.writeCell(i, 1, Met_No);
                   e.writeCell(i, 2, Met_Phase); 
                   e.writeCell(i, 3, Issue_Date);
                   e.writeCell(i, 4, WO_No);
                   e.writeCell(i, 5, WO_Date);
                   e.writeCell(i, 6, Purpose);
                   i=i+1;
                }
                e.generate();
                Messages.ShowInfoMessage("Vendor Stock Report Generated Successfully in Desktop/Reports", "Success");
            }
            
        }
        catch(Exception e)
        {
            Messages.ShowErrorMessage(e.toString(), "Error");
        }
        
    }
    @FXML
    private RadioButton Reports_Other_Reports_NSC;
    @FXML
    private RadioButton Reports_Other_Reports_DMR;
    @FXML
    private RadioButton Reports_Other_Reports_EMR;
    @FXML
    private RadioButton Reports_Other_Reports_SAP_Updation;    
    @FXML
    private RadioButton Reports_Other_Reports_Issue_and_Receive;    
    @FXML
    private DatePicker Reports_Other_Reports_Date_From;
    @FXML
    private DatePicker Reports_Other_Reports_Date_To;
    @FXML
    private Button Reports_Other_Reports_OK_Button;
    @FXML
    private void handle_Other_Reports_OK_Button_Event(ActionEvent event)
    {
        try
        {
            if(!Reports_Other_Reports_NSC.isSelected() && !Reports_Other_Reports_DMR.isSelected() && !Reports_Other_Reports_EMR.isSelected() && !Reports_Other_Reports_SAP_Updation.isSelected() && !Reports_Other_Reports_Issue_and_Receive.isSelected())
            {
                 throw new BlankFieldError("Please Select a Report Type");
            }
            if(Reports_Other_Reports_Date_From.getValue()==null || Reports_Other_Reports_Date_To.getValue()==null)
            {
                 throw new BlankFieldError("Please Fill Up the necessary Date Fields");
            }
            String From_Date_String;
            String To_Date_String;
            Date From_Date=TDates.getSQLDate(Reports_Other_Reports_Date_From.getValue());
            From_Date_String=TDates.dateToString(From_Date);
            Date To_Date=TDates.getSQLDate(Reports_Other_Reports_Date_To.getValue());
            To_Date_String=TDates.dateToString(To_Date);
            ResultSet rst=null;
            String sql="";
            String sql2="";
            ExcelHandling e=null;
            ExcelHandling e2=null;
            ExcelHandling e3=null;
            int flag=0;
            if(Reports_Other_Reports_DMR.isSelected())
            {
                sql="SELECT UTILIZATION.*,VENDOR.VENDOR_NAME FROM UTILIZATION,VENDOR WHERE UTIL_DATE>=? AND UTIL_DATE<=? AND UTILIZATION.PURPOSE='DMR' AND UTILIZATION.VENDOR_CODE=VENDOR.VENDOR_CODE";
                e=new ExcelHandling("DEFECTIVE METER REPLACEMENT FROM "+From_Date_String+" TO "+To_Date_String+".xls","Sheet1");
            }
            if(Reports_Other_Reports_EMR.isSelected())
            {
                sql="SELECT UTILIZATION.*,VENDOR.VENDOR_NAME FROM UTILIZATION,VENDOR WHERE UTIL_DATE>=? AND UTIL_DATE<=? AND UTILIZATION.PURPOSE='CMR' AND UTILIZATION.VENDOR_CODE=VENDOR.VENDOR_CODE";
                e=new ExcelHandling("ELECTROMAGNETIC METER REPLACEMENT FROM "+From_Date_String+" TO "+To_Date_String+".xls","Sheet1");
            }
            if(Reports_Other_Reports_NSC.isSelected())
            {
                sql="SELECT UTILIZATION.*,VENDOR.VENDOR_NAME FROM UTILIZATION,VENDOR WHERE UTIL_DATE>=? AND UTIL_DATE<=? AND UTILIZATION.PURPOSE='NSC' AND UTILIZATION.VENDOR_CODE=VENDOR.VENDOR_CODE";
                e=new ExcelHandling("METER UTILIZED AGAINST NSC FROM "+From_Date_String+" TO "+To_Date_String+".xls","Sheet1");
            }
            if(Reports_Other_Reports_SAP_Updation.isSelected())
            {
                sql="SELECT METER_REP_DATA.*,VENDOR.VENDOR_NAME FROM METER_REP_DATA,VENDOR WHERE REP_DATE>=? AND REP_DATE<=? AND METER_REP_DATA.VENDOR_CODE=VENDOR.VENDOR_CODE";
                e2=new ExcelHandling("METER REPLACEMENT DATA FROM "+From_Date_String+" TO "+To_Date_String+".xls","Sheet1");
                flag=1;
            }
            if(Reports_Other_Reports_Issue_and_Receive.isSelected()==true)
            {
                sql="SELECT ISSUE.*,VENDOR.VENDOR_NAME FROM ISSUE,VENDOR WHERE ISSUE_DATE>=? AND ISSUE_DATE<=? AND ISSUE.VENDOR_CODE=VENDOR.VENDOR_CODE";
                sql2="SELECT RECEIVE.*,VENDOR.VENDOR_NAME FROM RECEIVE,VENDOR WHERE RECEIVE_DATE>=? AND RECEIVE_DATE<=? AND RECEIVE.VENDOR_CODE=VENDOR.VENDOR_CODE AND VENDOR.VENDOR_TYPE='DEPARTMENTAL'";
                e2=new ExcelHandling("METER ISSUE DATA FROM "+From_Date_String+" TO "+To_Date_String+".xls","Sheet1");               
                e3=new ExcelHandling("METER RECEIVE DATA FROM "+From_Date_String+" TO "+To_Date_String+".xls","Sheet1");
                flag=2;
            }
            if(flag==0)
            {                    
                PreparedStatement pst=a.prepareStatement(sql);
                pst.setDate(1, From_Date);
                pst.setDate(2, To_Date);
                rst=pst.executeQuery();
                e.writeCell(0, 0, "SERIAL NO");
                e.writeCell(0, 1, "METER NO");
                e.writeCell(0, 2, "METER_PHASE");
                e.writeCell(0, 3, "CON ID/INSTALLATION NO");
                e.writeCell(0, 4, "UTILIZATION DATE(yyyy-mm-dd)");
                e.writeCell(0, 5, "PURPOSE");
                e.writeCell(0, 6, "UTILIZED BY");
                int i=1;
                while(rst.next())
                        {
                             String Met_No=rst.getString("METER_NO");
                            String Met_Phase=rst.getString("METER_PHASE");
                            String Vendor_Name=rst.getString("VENDOR_NAME");
                            String Util_Date=TDates.dateToString(rst.getDate("UTIL_DATE"));
                            String Con_ID=rst.getString("CON_ID");
                            String Purpose=rst.getString("PURPOSE");

                            e.writeCell(i, 0, i);
                            e.writeCell(i, 1, Met_No);
                            e.writeCell(i, 2, Met_Phase); 
                            e.writeCell(i, 3, Con_ID);
                            e.writeCell(i, 4, Util_Date);
                            e.writeCell(i, 5, Purpose);
                            e.writeCell(i, 6, Vendor_Name);
                            i=i+1;
                        }
                e.generate();
                Messages.ShowInfoMessage("Utilization Report Generated Successfully in Desktop/Reports", "Success");
                Reports_Other_Reports_Date_From.setValue(null);
                Reports_Other_Reports_Date_To.setValue(null);
                Reports_Other_Reports_Date_From.getEditor().setText("");
                Reports_Other_Reports_Date_From.getEditor().setText("");
            }
            else if(flag==1)
            {
                PreparedStatement pst=a.prepareStatement(sql);
                pst.setDate(1, From_Date);
                pst.setDate(2, To_Date);
                rst=pst.executeQuery();
                e2.writeCell(0,0,"SERIAL NO");
                e2.writeCell(0, 1, "CON ID/INSTALLATION");
                e2.writeCell(0,2,"DEVICE CATEGORY");
                e2.writeCell(0,3,"NEW METER NO");
                e2.writeCell(0,4,"OLD METER NO");
                e2.writeCell(0,5,"REPLACEMENT DATE");
                e2.writeCell(0,6,"REASON FOR REMOVAL");
                e2.writeCell(0,7,"SEAL1");
                e2.writeCell(0,8,"SEAL2");
                e2.writeCell(0,9,"SEAL3");
                e2.writeCell(0,10, "MR NOTE");
                e2.writeCell(0, 11, "FINAL READING");
                e2.writeCell(0, 12, "NEW METER INITIAL READING");
                e2.writeCell(0, 13, "PHASE");
                e2.writeCell(0, 14, "CON NAME");
                int i=1;
                while(rst.next())
                        {   String Con_ID=rst.getString("CON_REF");
                            String Con_Name=rst.getString("CON_NAME");
                            String Old_Meter_No=rst.getString("OLD_MET_NO");
                            String Final_Reading=rst.getString("FINAL_READING");
                            String Rep_Date=TDates.dateToString(rst.getDate("REP_DATE"));
                            String Phase=rst.getString("OLD_MET_PHASE");
                            String New_Meter_No=rst.getString("NEW_MET_NO");
                            String Initial_reading=rst.getString("NEW_MET_INIT_READING");
                            String Rep_Purpose=rst.getString("REMOVE_REASON");
                            String Dev_Cat=rst.getString("DEV_CAT");
                            String MR_Note=rst.getString("MR_NOTE");
                            String Seal1=rst.getString("seal1");   
                            String Seal2=rst.getString("seal2");
                            String Seal3=rst.getString("seal3");
                            e2.writeCell(i, 0, i);
                            e2.writeCell(i, 1, Con_ID);
                            e2.writeCell(i, 2, Dev_Cat); 
                            e2.writeCell(i, 3, New_Meter_No);
                            e2.writeCell(i, 4, Old_Meter_No);
                            e2.writeCell(i, 5, Rep_Date);
                            e2.writeCell(i, 6, Rep_Purpose);
                            e2.writeCell(i,7,Seal1);
                            e2.writeCell(i, 8, Seal2);
                            e2.writeCell(i, 9, Seal3);
                            e2.writeCell(i, 10,MR_Note);
                            e2.writeCell(i, 11,Final_Reading);
                            e2.writeCell(i, 12,Initial_reading);
                            e2.writeCell(i, 13,Phase);
                            e2.writeCell(i, 14,Con_Name);
                            i=i+1;
                        }
                e2.generate();
                Messages.ShowInfoMessage("Report for SAP Generated Successfully in Desktop/Reports", "Success");
                Reports_Other_Reports_Date_From.setValue(null);
                Reports_Other_Reports_Date_To.setValue(null);
                Reports_Other_Reports_Date_From.getEditor().setText("");
                Reports_Other_Reports_Date_From.getEditor().setText("");
            }
            else
            {
                String WO_Date="";
                PreparedStatement pst1=a.prepareStatement(sql);
                PreparedStatement pst2=a.prepareStatement(sql2);
                pst1.setDate(1, From_Date);
                pst1.setDate(2, To_Date);
                pst2.setDate(1, From_Date);
                pst2.setDate(2, To_Date);
                e2.writeCell(0, 0, "SERIAL NO");
                e2.writeCell(0, 1, "METER NO");
                e2.writeCell(0, 2, "METER_PHASE");
                e2.writeCell(0, 3, "ISSUE DATE(yyyy-mm-dd)");
                e2.writeCell(0, 4, "ISSUED TO");
                e2.writeCell(0, 5, "WO_No");
                e2.writeCell(0, 6, "WO_Date");
                e2.writeCell(0, 7,"PURPOSE");
                ResultSet Rst=pst1.executeQuery();
                
                int i=1;
                while(Rst.next())
                {
                    String Met_No=Rst.getString("METER_NO");
                    String Met_Phase=Rst.getString("METER_PHASE");
                    String Issue_Date=TDates.dateToString(Rst.getDate("ISSUE_DATE"));
                    String Vendor_Name=Rst.getString("VENDOR_NAME");
                    String WO_No=Rst.getString("WO_NO");
                    
                    if(Rst.getDate("WO_DATE")==null)
                    {
                      WO_Date="";
                    }
                    else
                    {
                    WO_Date=TDates.dateToString(Rst.getDate("WO_DATE"));
                    }
                    String Purpose=Rst.getString("PURPOSE");
                    e2.writeCell(i, 0, i);
                    e2.writeCell(i, 1, Met_No);
                    e2.writeCell(i, 2, Met_Phase);  
                    e2.writeCell(i, 3, Issue_Date);
                    e2.writeCell(i, 4, Vendor_Name);
                    e2.writeCell(i, 5, WO_No);
                    e2.writeCell(i, 6, WO_Date);
                    e2.writeCell(i, 7, Purpose);
                    i=i+1;
                }
                e2.generate();
                
                e3.writeCell(0, 0, "SERIAL NO");
                e3.writeCell(0, 1, "METER NO");
                e3.writeCell(0, 2, "METER_PHASE");
                e3.writeCell(0, 3, "RECEIVE DATE(yyyy-mm-dd)");
                e3.writeCell(0, 4, "RECEIVED BY");
                e3.writeCell(0, 5, "ERP REF NO");
                ResultSet Rst2=pst2.executeQuery();
                i=1;
                while(Rst2.next())
                {
                   String Met_No=Rst2.getString("METER_NO");
                   String Met_Phase=Rst2.getString("METER_PHASE");
                   String Receive_Date=TDates.dateToString(Rst2.getDate("RECEIVE_DATE"));
                   String Vendor_Name=Rst2.getString("VENDOR_NAME");
                   String ERP_Ref_No=Rst2.getString("ERP_REF_NO");
                   e3.writeCell(i, 0, i);
                   e3.writeCell(i, 1, Met_No);
                   e3.writeCell(i, 2, Met_Phase); 
                   e3.writeCell(i, 3, Receive_Date);
                   e3.writeCell(i, 4, Vendor_Name);
                   e3.writeCell(i, 5, ERP_Ref_No);
                   i=i+1;
                }
                e3.generate();
                Messages.ShowInfoMessage("Report Generated Successfully in Desktop/Reports folder", "Sucess");
                Reports_Other_Reports_Date_From.setValue(null);
                Reports_Other_Reports_Date_To.setValue(null);
                Reports_Other_Reports_Date_From.getEditor().setText("");
                Reports_Other_Reports_Date_From.getEditor().setText("");
            }           
        }
        catch(Exception e)
        {
            Messages.ShowInfoMessage(e.toString(), "Error");
        }
    }
    @FXML
    private MenuItem New_Vendor_Menu;
    @FXML
    private MenuItem Close_Menu;
    @FXML
    private MenuItem About_Menu;
    
    @FXML
    private void handle_New_Vendor_Menu_Item_Event(ActionEvent event) throws IOException 
    {
    	try
        {
	    	if(User_Permission_Level==1)
	    	{
		    	Parent root=FXMLLoader.load(getClass().getResource("/UI/Create New Vendor.fxml"));
		        Scene New_Vendor_Scene=new Scene(root);
		        Stage New_Vendor_Stage=new Stage();
		        New_Vendor_Stage.setScene(New_Vendor_Scene);
		        New_Vendor_Stage.setTitle("Create New Vendor");
		        New_Vendor_Stage.getIcons().add(new Image(getClass().getResourceAsStream("/UI/ABC.png")));
		        New_Vendor_Stage.setResizable(false);
		        New_Vendor_Stage.show(); 
	    	}
	    	else
	    	{
	    		throw new AuthenticationError("You are not authorized to create new vendor");
	    	}
        }
	    catch(Exception e)
	    {
	    	Messages.ShowErrorMessage(e.toString(), "Error");
	    }
    }
    @FXML
    private void handle_Close_Menu_Item_Event(ActionEvent event)
    {
        exit();
    }
    @FXML
    private void handle_About_Menu_Item_Event(ActionEvent event) throws IOException
    {
        Parent root=FXMLLoader.load(getClass().getResource("/UI/About.fxml"));
        Scene About_Scene=new Scene(root);
        Stage About_Stage=new Stage();
        About_Stage.setScene(About_Scene);
        About_Stage.setTitle("About");
        About_Stage.getIcons().add(new Image(getClass().getResourceAsStream("/UI/ABC.png")));
        About_Stage.setResizable(false);
        About_Stage.show();
    }
    @FXML
    private MenuItem Create_User_Menu;    
    @FXML
    private void handle_Create_User_MenuItem_Event(ActionEvent event)
    {
    	try
    	{
	    	if(User_Permission_Level==1)
	    	{
	    		Parent root=FXMLLoader.load(getClass().getResource("/UI/Create New User.fxml"));
	    		Scene scene=new Scene(root);
            	Stage stage=new Stage();
            	stage.setScene(scene);
            	stage.setTitle("Create New User");
            	stage.getIcons().add(new Image(getClass().getResourceAsStream("/UI/ABC.png")));
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
    @FXML
    private MenuItem Change_Account_Password_Menu;
    @FXML
    void handle_Change_Account_Password_Menu_Event(ActionEvent event) throws IOException
    {
    	Parent root=FXMLLoader.load(getClass().getResource("/UI/Edit Password.fxml"));
		Scene scene=new Scene(root);
    	Stage stage=new Stage();
    	stage.setScene(scene);
    	stage.setTitle("Change Password");
    	stage.getIcons().add(new Image(getClass().getResourceAsStream("/UI/ABC.png")));
    	stage.setResizable(false);
    	stage.show();
    }
    @Override  
    public void initialize(URL url, ResourceBundle rb) 
    {
    User_Name_Text.setText(Active_User.getUserName());;
    ToggleGroup group=new ToggleGroup();
    ToggleGroup group2=new ToggleGroup();
    R_Meter_Phase.getItems().addAll("1P","3P");
    I_Meter_Phase.getItems().addAll("1P","3P");
    I_Purpose.getItems().addAll("NEW SERVICE CONNECTION","DEFECTIVE METER REPLACEMENT","ELECTROMAGNETIC METER REPLACEMENT","OTHER");
    U_Meter_Phase.getItems().addAll("1P","3P");
    U_Purpose.getItems().addAll("NEW SERVICE CONNECTION","DEFECTIVE METER REPLACEMENT","ELECTROMAGNETIC METER REPLACEMENT","OTHER");
    Ret_Meter_Phase.getItems().addAll("1P","3P");
    Report_Stock_Report_Type.getItems().addAll("Local Stock","Vendor Stock");
    Report_Stock_Report_Vendor.setDisable(true);  
    Report_Stock_Report_Type.valueProperty().addListener(new ChangeListener<String>(){    
        @Override 
        public void changed(ObservableValue ov, String t, String t1) 
        {
            if(t1.equals("Vendor Stock"))
            {
                Report_Stock_Report_Vendor.setDisable(false);
            }
            if(t1.equals("Local Stock"))
            {
                Report_Stock_Report_Vendor.setDisable(true);
            }
        }
    });
    
        ResultSet rst=null;
        Statement stmt;
        String sql="SELECT * FROM VENDOR ORDER BY VENDOR_CODE ASC";
        try
        {
            stmt=a.createStatement();
            rst=stmt.executeQuery(sql);
            while(rst.next())
            {
                R_Received_By.getItems().add(rst.getString("VENDOR_NAME"));
                I_Issued_To.getItems().add(rst.getString("VENDOR_NAME"));
                U_Utilized_By.getItems().add(rst.getString("VENDOR_NAME"));
                Ret_Returned_By.getItems().add(rst.getString("VENDOR_NAME"));
                Report_Stock_Report_Vendor.getItems().add(rst.getString("VENDOR_NAME"));
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
}
}

