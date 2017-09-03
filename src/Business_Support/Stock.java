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
public class Stock {
   //static private Connection StockCon;
   static  private ResultSet StockRest;
   static  private PreparedStatement StockStmt;
   
   
   static public boolean issueAllowed(Connection con,String a)
   {
       return (deviceExists(con,a));
   }
   
   public static void issueFromStock(Connection con,String a)
   {
       try{      
       String sql="DELETE from stock where METER_NO=?";
       StockStmt=con.prepareStatement(sql);
       StockStmt.setString(1, a);
       StockStmt.execute();
       }
       catch(Exception e)
       {
           e.printStackTrace();
       }        
   }
   
   public static void addToStock(Connection Con,String a,String b,int c)
   {
        try{      
       String sql="INSERT INTO stock values(?,?,?)";
       StockStmt=Con.prepareStatement(sql);
       StockStmt.setString(1, a);
       StockStmt.setString(2, b);
       StockStmt.setInt(3, c);
       StockStmt.execute();
       }
       catch(Exception e)
       {
           e.printStackTrace();
       }        
       
   }
   
   public static void addToStock(Connection Con,String a,String b)
   {
        try{      
       String sql="INSERT INTO stock (METER_NO,METER_PHASE) values(?,?)";
       StockStmt=Con.prepareStatement(sql);
       StockStmt.setString(1, a);
       StockStmt.setString(2, b);
       StockStmt.execute();
       }
       catch(Exception e)
       {
           e.printStackTrace();
       }        
       
   }
   
   public static boolean deviceExists(Connection con,String a)
   {
        boolean result=false;
       try{      
       String sql="SELECT * from stock where METER_NO=?";
       StockStmt=con.prepareStatement(sql);
       StockStmt.setString(1, a);
       StockRest=StockStmt.executeQuery();
       if(StockRest.next())
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
           
    
    
}
