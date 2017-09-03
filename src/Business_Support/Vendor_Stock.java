/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Business_Support;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Soumyadeep
 */
public class Vendor_Stock {
   // static Connection V_Con;
    
    public static void issueToVendor(Connection Con,int a,String Met,String M_P,Date I_Date,String WO,Date W_date,String P)
    {
        String sql="INSERT INTO "+"v"+Integer.toString(a)+" values(?,?,?,?,?,?)";
        try{
           PreparedStatement pst=Con.prepareStatement(sql);
        pst.setString(1, Met);
        pst.setString(2, M_P);
        pst.setDate(3, I_Date);
        pst.setString(4, WO);
        pst.setDate(5, W_date);
        pst.setString(6, P);
        pst.execute();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
    }
    
    public static boolean vendorExists(Connection Con,int a)
    {
        boolean result=false;
        ResultSet r=null;
        String sql="SELECT * FROM VENDOR WHERE VENDOR_CODE="+Integer.toString(a);
        try{
           Statement pst=Con.createStatement();
           r=pst.executeQuery(sql);
           if(r.next())
           {
               result=true;
           }
           else
           {
               result=false;
           }
         }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return result;
    }
    
    public static  String vendorType(Connection a,int b)
    {
        String Type=null;
        ResultSet r=null;
        String sql="SELECT * FROM VENDOR WHERE VENDOR_CODE="+Integer.toString(b);
        try{
           Statement pst=a.createStatement();
           r=pst.executeQuery(sql);
           while(r.next())
           {
               Type=r.getString("VENDOR_TYPE");
           }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return Type;
    }
    
    public static  void returnFromVendor(Connection Con,int a,String Met)
    {
        String sql="DELETE FROM "+"v"+Integer.toString(a)+" WHERE METER_NO=?";
        try{
           PreparedStatement pst=Con.prepareStatement(sql);
           pst.setString(1, Met);
           pst.execute();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public static  boolean deviceWithVendor(Connection con,int a,String Met)
    {
        boolean result=false;
        ResultSet r=null;
        String sql="SELECT * FROM "+"v"+Integer.toString(a)+" WHERE METER_NO=?";
        try
        {
            PreparedStatement PST=con.prepareStatement(sql);
            PST.setString(1,Met);
            r=PST.executeQuery();
            if(r.next())
            {
                result=true;
            }
            else
            {
                result=false;
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return result;
    }
    public static int getVendorCode(Connection a,String VendorName)
    {   
        int Code=0;
        ResultSet r=null;
        String sql="SELECT * FROM VENDOR WHERE VENDOR_NAME='"+VendorName+"'";
        try{
           Statement pst=a.createStatement();
           r=pst.executeQuery(sql);
           while(r.next())
           {
               Code=r.getInt("VENDOR_CODE");
           }
        } catch (SQLException ex) {
            Logger.getLogger(Vendor_Stock.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Code;
    }

    
    
}
