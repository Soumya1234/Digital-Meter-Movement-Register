/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Business_Support;
import java.sql.*;
import java.util.Date;
import java.util.Calendar;
import java.text.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
/**
 *
 * @author Soumyadeep
 */
public class TDates {
  public static Date getIssueDate(Connection a,String Met)   
  {   Date i_Date=null;
      ResultSet rst=null;
      String sql="SELECT * FROM ISSUE WHERE METER_NO=?";
      try
      {
          PreparedStatement pst=a.prepareStatement(sql);
          pst.setString(1,Met);
          rst=pst.executeQuery();
          while(rst.next())
          {
              i_Date=rst.getDate("ISSUE_DATE");
          }
      }
      catch(Exception e)
      {
          e.printStackTrace();;
      }
      return i_Date;
  }
  
  public static Date getWODate(Connection con,String Met)
  {
      Date WO_Date=null;
      ResultSet rst=null;
      String sql="SELECT * FROM ISSUE WHERE METER_NO=?";
      try
      {
          PreparedStatement pst=con.prepareStatement(sql);
          pst.setString(1,Met);
          rst=pst.executeQuery();
          while(rst.next())
          {
              WO_Date=rst.getDate("WO_DATE");
              
          }
          
      }
      catch(Exception e)
      {
          e.printStackTrace();
          
      }
      return WO_Date;
  }
  
  public static java.util.Date removeTime(java.util.Date a)
  {   Date x=null;
      DateFormat c=new SimpleDateFormat("yyyy-MM-dd");
      String d=c.format(a);
      try{
       x=c.parse(d);
      }
      catch(Exception e)
      {
          e.printStackTrace();
      }
      return x;
      //Calendar c= Calendar.getInstance();
      //c.set(Calendar.HOUR_OF_DAY, 0);
      //c.set(Calendar.MINUTE, 0);
      //c.set(Calendar.SECOND,0);
      //return c.getTime();/
  }


 public static int compareDateOnly(java.util.Date a,java.util.Date b)
{
    int x=2;
    if(b!=null)
    {
    System.out.println(removeTime(a));
    System.out.println(removeTime(b));
    x=removeTime(a).compareTo(removeTime(b));
    }
    return x;
}
 
 public static java.sql.Date getSQLDate(LocalDate d)
 {
     Instant instant=Instant.from(d.atStartOfDay(ZoneId.systemDefault()));
     java.sql.Date date=new java.sql.Date(Date.from(instant).getTime());
     return date;
 }
 public static java.util.Date getUTILDate(LocalDate d)
 {
     Instant instant=Instant.from(d.atStartOfDay(ZoneId.systemDefault()));
     java.util.Date date=new java.util.Date(Date.from(instant).getTime());
     return date;
 }
 public static String getCurrentDate()
    { 
        SimpleDateFormat d= new SimpleDateFormat("yyyy-MM-dd");
        Calendar c=Calendar.getInstance();
        String a=d.format(c.getTime());
        return a;
    }
    
    public static String dateToString(java.sql.Date a)
    {
        SimpleDateFormat d= new SimpleDateFormat("yyyy-MM-dd");
        return(d.format(a.getTime()));
    }
}