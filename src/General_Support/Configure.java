/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package General_Support;
import java.io.*;
/**
 *
 * @author Soumyadeep
 */
public class Configure {
    public static void saveConfiguration(String a,String b) 
    {
        BufferedWriter br=null;
        String Current_Dir=System.getProperty("user.dir");
        try
        {
        File Config=new File(Current_Dir+"\\Config.txt");
        br=new BufferedWriter(new FileWriter(Config));
        br.write(a);
        br.write(" ");
        br.write(b);
        //System.out.println(a);
        //System.out.println(b);
        }
        catch(IOException w)
                {
                   w.printStackTrace();
                }       
        finally
        {
            try
            {
            br.close();
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }
    }
    
    public static String readIP() throws FileNotFoundException,IOException
    {
        BufferedReader br=null;
        String IP=" ";
        String Current_Dir=System.getProperty("user.dir");
            File config=new File(Current_Dir+"\\Config.txt");
            br=new BufferedReader(new FileReader(config));
            String str;
            while((str=br.readLine())!=null)
            {
               String[] tmp=str.split(" ");
               IP=tmp[0];
            }
            return IP;
    }    
    
    public static String readPort() throws FileNotFoundException,IOException
    {
        BufferedReader br=null;
        String Port=" ";
        String Current_Dir=System.getProperty("user.dir");
            File config=new File(Current_Dir+"\\Config.txt");
            br=new BufferedReader(new FileReader(config));
            String str;
            while((str=br.readLine())!=null)
            {
               String[] tmp=str.split(" ");
               Port=tmp[1];
            }
            return Port;
    }
    
}