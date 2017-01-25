package components;

import  java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import  org.apache.poi.hssf.usermodel.HSSFSheet;
import  org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import  org.apache.poi.hssf.usermodel.HSSFRow;
import  org.apache.poi.hssf.usermodel.HSSFCell;

public class CreateExlFile{
    public static void main(String[]args) {
        try {
        	HSSFWorkbook workbook = null;
            String filename_exist = "C:/Pierwszy.xls";
            String filename_new = "C:/Drugi.xls";
            File file = new File(filename_exist);
            
            if(file.exists()) {
            	System.out.println("tu jestem");
            	try {
                    workbook = (HSSFWorkbook)WorkbookFactory.create(file);
                    HSSFSheet sheet = workbook.createSheet("FourthSheet");  

                    HSSFRow rowhead = sheet.createRow((short)0);
                    rowhead.createCell(0).setCellValue("No.");
                    rowhead.createCell(1).setCellValue("Name");
                    rowhead.createCell(2).setCellValue("Address");
                    rowhead.createCell(3).setCellValue("Email");

                    HSSFRow row = sheet.createRow((short)1);
                    row.createCell(0).setCellValue("1");
                    row.createCell(1).setCellValue("Sankumarsingh");
                    row.createCell(2).setCellValue("India");
                    row.createCell(3).setCellValue("sankumarsingh@gmail.com");
                    
                    FileOutputStream fileOut = new FileOutputStream(filename_new);
                    workbook.write(fileOut);
                    workbook.close();
                    fileOut.close();
                    
                } catch (InvalidFormatException e) {
                    e.printStackTrace();
                }
            } else { 
            	workbook = new HSSFWorkbook();
            	HSSFSheet sheet = workbook.createSheet("FirstSheet");  

                HSSFRow rowhead = sheet.createRow((short)0);
                rowhead.createCell(0).setCellValue("No.");
                rowhead.createCell(1).setCellValue("Name");
                rowhead.createCell(2).setCellValue("Address");
                rowhead.createCell(3).setCellValue("Email");

                HSSFRow row = sheet.createRow((short)1);
                row.createCell(0).setCellValue("1");
                row.createCell(1).setCellValue("Sankumarsingh");
                row.createCell(2).setCellValue("India");
                row.createCell(3).setCellValue("sankumarsingh@gmail.com");
                
                FileOutputStream fileOut = new FileOutputStream(filename_exist);
                workbook.write(fileOut);
                fileOut.close();
                
            }
        
            //file = new File(filename_new);
            //Path source = Paths.get("C:/Pierwszy.xls");
            //Path newdir = Paths.get("C:/Drugi.xls");
            File file_new = new File(filename_new);
           
            try {
            	//Files.move(source, newdir.resolve(source.getFileName()), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            	Files.copy(file_new.toPath(), file.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                file_new.delete();
            } catch(IOException e) {
            	e.printStackTrace();
            }
            
    
            //file.delete();
            System.out.println("Your excel file has been generated!");

        } catch ( Exception ex ) {
            System.out.println(ex);
        }
    }
}
