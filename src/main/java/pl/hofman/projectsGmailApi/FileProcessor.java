package pl.hofman.projectsGmailApi;

import com.google.api.services.gmail.model.Message;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class FileProcessor {

    public static String choosingFile(Message gmailMessage) {
        String fileName = null;
        String[] parts = MessageProcessor.messageSplit(gmailMessage);
        String part8 = parts[6];
        if (part8.equals("ASAP")) {
            Date date = new Date(gmailMessage.getInternalDate());
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
            df.format(date);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            String month = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH);

            //LocalDate currentDate = LocalDate.now();
            //Month currentMonth = currentDate.getMonth();
            fileName = month + ".xlsx";

        } else {
            switch (part8.substring(3, 5)) {
                case "01":
                    fileName = "January.xlsx";
                    break;
                case "02":
                    fileName = "February.xlsx";
                    break;
                case "03":
                    fileName = "March.xlsx";
                    break;
                case "04":
                    fileName = "April.xlsx";
                    break;
                case "05":
                    fileName = "May.xlsx";
                    break;
                case "06":
                    fileName = "June.xlsx";
                    break;
                case "07":
                    fileName = "July.xlsx";
                    break;
                case "08":
                    fileName = "August.xlsx";
                    break;
                case "09":
                    fileName = "September.xlsx";
                    break;
                case "10":
                    fileName = "October.xlsx";
                    break;
                case "11":
                    fileName = "November.xlsx";
                    break;
                case "12":
                    fileName = "December.xlsx";
                    break;
            }
        }
        return fileName;
    }


    public static void addNewMsgToTheFile(String fileName, XSSFWorkbook workbook, Message gmailMessage,
                                          String[] parts) throws IOException {
        String checkIfExists = new String();
        String part4 = parts[2];
        String part6 = parts[4];
        String part8 = parts[6];
        String part9 = parts[7];
        XSSFSheet sheet = workbook.getSheetAt(0);
        //check all rows if message that was found already exist in the Excel file (checking by ID of message)
        //this is done for all found messages
        for (Row row : sheet) {
            Cell cel = row.getCell(0);
            if (cel.getStringCellValue().equals(gmailMessage.getId())) {
                System.out.println("---Ta wiadomość już istnieje---");
                checkIfExists = "yes";
                break;
            }
        }
        int rowCount = sheet.getLastRowNum(); //last filled row index

        //if message is not yet in the file - add it
        if (!checkIfExists.equals("yes")) {

            //creating new row
            Row row = sheet.createRow(rowCount + 1);

            //creating new cells

            //Msg ID
            Cell cell = row.createCell(0);
            cell.setCellValue(gmailMessage.getId());

            //Date of adding to the file
            CreationHelper createHelper = workbook.getCreationHelper();
            CellStyle cellStyle1 = workbook.createCellStyle();
            cellStyle1.setDataFormat(
                    createHelper.createDataFormat().getFormat("dd/mm/yyyy"));
            Cell cell1 = row.createCell(1);
            cell1.setCellValue(LocalDate.now());
            cell1.setCellStyle(cellStyle1);

            //Project name
            Cell cell2 = row.createCell(2);
            cell2.setCellValue(part4);

            //Value
            Cell cell3 = row.createCell(3);
            cell3.setCellValue(part6);

            //Deadline
            Cell cell4 = row.createCell(4);

            if (part9.equals("Work")) {
                cell4.setCellValue(part8);
            } else {
                cell4.setCellValue(part8 + " " + part9);
            }

            //Date of receiving mail
            Cell cell5 = row.createCell(5);
            Date date = new Date(gmailMessage.getInternalDate());
            DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            cell5.setCellValue(df.format(date));


            // Write the output to the file
            FileOutputStream fileOut = new FileOutputStream(fileName);
            workbook.write(fileOut);
            workbook.close();
            fileOut.close();
        }
    }


    public static XSSFWorkbook createFile() {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet;
        workbook = new XSSFWorkbook();
        sheet = workbook.createSheet("Sheet1");
        Row row0 = sheet.createRow(0);

        Font font = workbook.createFont();
        font.setBold(true);
        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setFont(font);

        Cell cellA = row0.createCell(0);
        cellA.setCellValue("Msg ID");
        cellA.setCellStyle(style);

        Cell cellB = row0.createCell(1);
        cellB.setCellValue("Date of adding to the file ");
        cellB.setCellStyle(style);

        Cell cellC = row0.createCell(2);
        cellC.setCellValue("Project");
        cellC.setCellStyle(style);

        Cell cellD = row0.createCell(3);
        cellD.setCellValue("Value");
        cellD.setCellStyle(style);

        Cell cellE = row0.createCell(4);
        cellE.setCellValue("Deadline");
        cellE.setCellStyle(style);

        Cell cellF = row0.createCell(5);
        cellF.setCellValue("Date of receiving mail");
        cellF.setCellStyle(style);


        sheet.setColumnWidth(0, 0);
        sheet.setColumnWidth(1, 0);
        sheet.setColumnWidth(2, 3000);
        sheet.setColumnWidth(3, 3000);
        sheet.setColumnWidth(4, 3000);
        sheet.setColumnWidth(5, 5100);

        return workbook;
    }

}
