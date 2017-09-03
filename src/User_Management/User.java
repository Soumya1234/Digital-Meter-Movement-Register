package User_Management;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import General_Support.Shared_Connection;

public class User {
   
	public static boolean usernameExists(String Usrname)
	{
		try
		{
			Connection con=Shared_Connection.getSharedConnection();
			String query_authenticate="SELECT * FROM USER_DATA WHERE USERNAME=?";
			PreparedStatement pst=con.prepareStatement(query_authenticate);
			pst.setString(1, Usrname);
			ResultSet rst=pst.executeQuery();
			if(rst.next())
			{
                return true;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}
	
	public static void createUser(String Username, String PasswordHash,int PermissionLevel)
	{
		try
		{
			Connection con=Shared_Connection.getSharedConnection();
			int Last_User_ID=0;
            Statement myStat=null;
            myStat=con.createStatement();
            ResultSet myRest=myStat.executeQuery("SELECT * FROM USER_DATA ORDER BY USER_ID DESC LIMIT 1");
            if(myRest.next())
            {
               Last_User_ID=myRest.getInt("USER_ID");
            }
            int New_User_ID=Last_User_ID+1;
			String create_user_query="INSERT INTO USER_DATA VALUES(?,?,?,?)";
			PreparedStatement pst=con.prepareStatement(create_user_query);
			pst.setInt(1,New_User_ID);
			pst.setString(2, Username);
			pst.setString(3, PasswordHash);
			pst.setInt(4, PermissionLevel);
			pst.execute();
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
