package General_Support;
import java.sql.*;
public class Shared_Connection {
   private static String Server_IP;
   private static String Server_Port;
   private static Connection SCN;
   public static Connection getSharedConnection()
   {
	   return SCN;
   }
   public static void setSharedConnection(Connection a)
   {
	   SCN=a;
   }
   public static void setServerConfig(String IP,String Port)
   {
	   Server_IP=IP;
	   Server_Port=Port;
   }
   public static String getIP()
   {
	   return Server_IP;
   }
   
   public static String getPort()
   {
	   return Server_Port;
   }
   
   public static void closeSharedConnection() throws SQLException
   {
	   SCN.close();
   }
}
