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
public class Utilization {
    
    
    public static boolean utilizeDevice(Connection a,String Met_No,String Met_Ph,String Con_No,int Vendor,Date Util_Date,String Purpose)
    {
        int flag=0;
        String SQL1="INSERT INTO UTILIZATION VALUES(?,?,?,?,?,?)";
        String SQL2="DELETE FROM V"+Integer.toString(Vendor)+" WHERE METER_NO=?";
        try{
        PreparedStatement PST1=a.prepareStatement(SQL1);
        PST1.setString(1,Met_No);
        //System.out.println(Met_No);
        PST1.setString(2, Met_Ph);
        //System.out.println(Met_Ph);
        PST1.setString(3,Con_No);
        //System.out.println(Con_No);
        PST1.setInt(4,Vendor);
        //System.out.println(Vendor);
        PST1.setDate(5,Util_Date);
        //System.out.println(Util_Date);
        PST1.setString(6,Purpose);
        //System.out.println(Purpose);
        PST1.execute();
        PreparedStatement PST2=a.prepareStatement(SQL2);
        PST2.setString(1,Met_No);
        PST2.execute();
        flag=1;
        }
        catch(Exception e)
        {
           flag=0;
            e.printStackTrace();
        }
        if(flag==1)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
}
