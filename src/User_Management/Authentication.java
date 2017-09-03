//This class manages the Authentication of the User 

package User_Management;
import java.security.NoSuchAlgorithmException;
import java.sql.*;

import General_Support.*;

 
public class Authentication {
	
	
/**
 * Establishes Connection to the Database
 * Authenticates the user.Returns TRUE on authentication.
 * BROAD LOGIC: connects to the database with hard coded root password and then 
   checks for the User Credentials in the "User_Data" Table of the Database.  
 * Sets the Connection Details to the "Shared_Connection Class after successful Log In
 * Sets the User Details to the "Active_User" class after successful Log In         
 * @param User_Name
 * @param Password
 * @return Boolean 
 * @throws NoSuchAlgorithmException 
 */
   public static boolean isauthenticated(String User_Name,String Password) throws NoSuchAlgorithmException 
   {
	   try
	   {
		   String Server_IP=Shared_Connection.getIP();
		   String Server_Port=Shared_Connection.getPort();
		   Connection con=DriverManager.getConnection("jdbc:mysql://"+Server_IP+":"+Server_Port+"/meter_movement", "appuser", "apppassword");
		   String query_authenticate="SELECT * FROM USER_DATA WHERE USERNAME=?";
		   PreparedStatement pst=con.prepareStatement(query_authenticate);
		   pst.setString(1, User_Name);
		   ResultSet rst=pst.executeQuery();
		   if(rst.next())
		   {
			   if((Cryptography.getPasswordHash(Password)).equals(rst.getString("Login_Password")))
			   {
				   Shared_Connection.setSharedConnection(con);
				   Active_User.setUserID(rst.getInt("User_ID")); //Sets the User_ID against given Username
				   Active_User.setUserName(User_Name);
				   return true;
			   }
		   }	                    
	   }
	   catch(SQLException e)
	   {
		   e.printStackTrace();
	   }
	   return false;
   }
}
