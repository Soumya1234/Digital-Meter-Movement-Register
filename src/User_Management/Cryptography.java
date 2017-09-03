package User_Management;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Cryptography 
{
   private static String generateHash(String StringtoEncrypt) throws NoSuchAlgorithmException
   {
	  
	   MessageDigest msgdigest=MessageDigest.getInstance("SHA-256");
	   msgdigest.update(StringtoEncrypt.getBytes());
	   String encryptedString = new String(msgdigest.digest());
	   return encryptedString;
	   
   }
   
   public static String getPasswordHash(String Password) throws NoSuchAlgorithmException
   {
	   return generateHash(Password);
   }
}
