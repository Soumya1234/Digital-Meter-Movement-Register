/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Business_Support;
import java.io.File;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import java.io.FileOutputStream;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;
/**
 *
 * @author Soumyadeep
 */
public class ExcelHandling {
    private String fileName;
    private Workbook WB;
    private Sheet sheet1;
    Row r;
    public ExcelHandling()
    {
        fileName="NewFile.xls";
        WB=new HSSFWorkbook();
        sheet1= WB.createSheet();
         r=sheet1.createRow(0);
    }
    public ExcelHandling(String a, String b)
    {
        fileName=a;
        WB=new HSSFWorkbook();
        sheet1= WB.createSheet(b);
        r=sheet1.createRow(0);
    }
    public void writeCell(int a,int b,String c)
    {
        if(a==sheet1.getLastRowNum())
        {
        Cell cell=r.createCell(b);
        cell.setCellValue(c);}
        else
        {
            r=sheet1.createRow(a);
            Cell cell=r.createCell(b);
            cell.setCellValue(c);
        }
    }
    public void writeCell(int a,int b,int c)
    {
        if(a==sheet1.getLastRowNum())
        {
        Cell cell=r.createCell(b);
        cell.setCellValue(c);}
        else
        {
            r=sheet1.createRow(a);
            Cell cell=r.createCell(b);
            cell.setCellValue(c);
        }
    }
    
    public void generate()
    {
        FileOutputStream Output=null;
    try{
        String Path=System.getProperty("user.home")+"\\Desktop\\Reports";
        if(new File(Path).exists())
        {
            Output=new FileOutputStream(Path+"\\"+fileName);
        }
        else
        {
            new File(Path).mkdir();
            Output =new FileOutputStream(Path+"\\"+fileName);
        }
        WB.write(Output);
        Output.close();}
    catch(Exception e)
    {
        e.printStackTrace();
    }
    }
}
