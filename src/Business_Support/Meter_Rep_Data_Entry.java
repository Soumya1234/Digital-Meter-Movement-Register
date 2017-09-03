/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Business_Support;
import java.sql.*;
/**
 *
 * @author Station Manager
 */
public class Meter_Rep_Data_Entry extends Exception{
    public Date U_Date;
    public String Met_No;
    public String Con_Ref;
    public String Purpose;
    public String Phase;
    public int Vendor_Code;
    
    public Meter_Rep_Data_Entry(String M_No,String Con_ID,Date Util_Date,String _Purpose,String _Phase,int VC)
    {
        Met_No=M_No;
        Con_Ref=Con_ID;
        U_Date=Util_Date;
        Purpose=_Purpose;
        Phase=_Phase;
        Vendor_Code=VC;
    }
}
