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
public class Return_Report {
    public static String returnedBy(Connection con,String Met_No)
    {
        String returned_by=" ";
        ResultSet rst=null;
        String sql="SELECT RETUR_N.METER_NO,RETUR_N.RETURNED_BY,VENDOR.VENDOR_NAME FROM RETUR_N,VENDOR WHERE RETUR_N.METER_NO=? AND RETUR_N.RETURNED_BY=VENDOR.VENDOR_CODE";
        try
        {
        PreparedStatement pst=con.prepareStatement(sql);
        pst.setString(1, Met_No);
        rst= pst.executeQuery();
        if(rst.next())
        {
            returned_by=rst.getString("VENDOR_NAME");
        }
        else
        {
            returned_by=" ";
        }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return returned_by;
    }
}
