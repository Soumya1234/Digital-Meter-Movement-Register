/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Business_Support;
import java.sql.*;
/**
 *
 * @author Soumyadeep
 */
public class Receive {
    public static Date getReceiveDate(Connection a,String Met)   
  {   Date r_Date=null;
      ResultSet rst=null;
      String sql="SELECT * FROM RECEIVE WHERE METER_NO=?";
      try
      {
          PreparedStatement pst=a.prepareStatement(sql);
          pst.setString(1,Met);
          rst=pst.executeQuery();
          while(rst.next())
          {
              r_Date=rst.getDate("RECEIVE_DATE");
          }
      }
      catch(Exception e)
      {
          e.printStackTrace();;
      }
      return r_Date;
  }
    
    public static boolean UtilizedMeterReceived(Connection a,String Met)
    {
        String sql="SELECT * FROM RECEIVE_UTILZED WHERE METER_NO=?";
        ResultSet rst=null;
        boolean Flag=false;
        try
        {
            PreparedStatement pst=a.prepareStatement(sql);
            pst.setString(1,Met);
            rst=pst.executeQuery();
            while(rst.next())
            {
                Flag=true;
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return Flag;
                
    }
    
    public static Date getReceive_Utilized_Date(Connection a,String Met)   
  {   Date r_Date=null;
      ResultSet rst=null;
      String sql="SELECT * FROM RECEIVE_UTILIZED WHERE METER_NO=?";
      try
      {
          PreparedStatement pst=a.prepareStatement(sql);
          pst.setString(1,Met);
          rst=pst.executeQuery();
          while(rst.next())
          {
              r_Date=rst.getDate("RECEIVE_DATE");
          }
      }
      catch(Exception e)
      {
          e.printStackTrace();;
      }
      return r_Date;
  }
    
    
}
