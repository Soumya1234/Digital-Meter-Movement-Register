//This class contains the data about the user logged into the Application
package User_Management;
import java.sql.*;

import General_Support.Shared_Connection;

public class Active_User {
	
  private static int Active_User_ID;
  private static String Active_User_Name;
  private static int Active_User_Permission_Level=3;
  
  /**
   * Sets the User_ID of the Active User Logged in to the Application 
   * @param ID
   */
  public static void setUserID(int ID)
  {
	  Active_User_ID=ID;
  }
  
  public static void setUserName(String UserName)
  {
	  Active_User_Name=UserName;
  }
  
  public static String getUserName()
  {
	  return Active_User_Name;
  }
  /**
   * Will be used for determining Permission Level for each Method in Business Logic
   * @return Permission Level as integer
   */
  public static int getPermissionLevel()
  {
	  try{
		  Connection con=Shared_Connection.getSharedConnection();
		  String query_permission_level="SELECT PERMISSION_LEVEL FROM USER_DATA WHERE USER_ID=?";
		  PreparedStatement pst=con.prepareStatement(query_permission_level);
		  pst.setInt(1, Active_User_ID);
		  ResultSet rst= pst.executeQuery();
		  rst.next();
		  Active_User_Permission_Level=rst.getInt("PERMISSION_LEVEL");
	  }
	  catch (SQLException e)
	  {
		  e.printStackTrace();
	  }
	  return Active_User_Permission_Level;
  }
  
 
}
