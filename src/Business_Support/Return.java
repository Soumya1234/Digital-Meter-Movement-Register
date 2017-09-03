/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Business_Support;
import java.sql.*;
/**
 *
 * @author Station Manager
 */
public class Return {
    public static void performReturn(Connection con,String Met_No,String Met_Phase,int Ret_By)
    {
                 Vendor_Stock.returnFromVendor(con, Ret_By, Met_No);
                 Stock.addToStock(con, Met_No, Met_Phase);          
    }
    public static void updateReturnTable(Connection con,String Met_No,String Met_Phase,int Ret_By,String WO_No,Date WO_Date,Date Ret_date,String Purpose)
    {
        String SQL="INSERT INTO RETUR_N VALUES(?,?,?,?,?,?,?)";
        try{
            PreparedStatement PST=con.prepareStatement(SQL);
            PST.setString(1, Met_No);
            PST.setString(2, Met_Phase);
            PST.setInt(3, Ret_By);
            PST.setString(4,WO_No);
            PST.setDate(5, WO_Date );
            PST.setDate(6,Ret_date);
            PST.setString(7,Purpose);
            PST.execute();
        }
        catch(Exception e)
        {
             e.printStackTrace();
        }
    }
    public static boolean DirectReceiveFromStoreByOutSourced(Connection con,String Met)
    {
        String SQL="SELECT * FROM RECEIVE WHERE METER_NO=?";
        boolean result=false;
        ResultSet rst=null;
        try
        {
            PreparedStatement pst=con.prepareStatement(SQL);
            pst.setString(1,Met);
            rst=pst.executeQuery();
            if(rst.next())
            {
                
                if(Vendor_Stock.vendorType(con, rst.getInt("VENDOR_CODE")).equals("CONTRACTUAL"))  
                {
                    result=true;
                }
                else
                {
                    result=false;
                }
            }
        }
        catch(Exception e)
        {
            
        }
        return result;
    }
}
