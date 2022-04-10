package pl.hofman.projectsGmailApi;

import com.google.api.services.gmail.model.Message;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbookFactory;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FileProcessor {

    MessageProcessor messageProcessor;

    public FileProcessor(MessageProcessor messageProcessor) {
        this.messageProcessor = messageProcessor;
    }

    public void saveMessagesInTheFile(List<Message> mainGmailMessagesInThread) throws NullPointerException, IOException {
        XSSFWorkbook workbook;
        String fileName = null;
        System.out.printf("--------------------------------------------\n" +
                        "Zapisywanie wiadomości projektowych do pliku:\n" +
                        "--------------------------------------------\n\n" );

        for (Message msg : mainGmailMessagesInThread) {
            Project project = new Project(msg, messageProcessor);
            fileName = chooseFileName(msg, project);
            //String projectName = project.getProjectName(msg, messageProcessor);

            try {
                FileInputStream inputStream = new FileInputStream(new File(fileName));
                workbook = (XSSFWorkbook) XSSFWorkbookFactory.create(inputStream);
//                sheet = workbook.getSheetAt(0);
            } catch (FileNotFoundException e) {

                workbook = createFile();
            } catch (IOException e) {
                throw new IOException("Not possible to create file " + fileName);
            }
            messageProcessor.singleProjectMessagesDisplay(msg);

            try {
                addNewMsgToTheFile(fileName, workbook, project, msg);
            } catch (IOException e) {
                throw new IOException("Not possible to add message " + msg.getId() + " to the file " + fileName);
            }

        }
    }

    private String chooseFileName(Message gmailMessage, Project project) {
        String fileName = null;

        String deadline = project.getDeadline();
        String projectName = project.getName();
        System.out.println("*****");
        System.out.println("DEADLINE FROM FILE PROCESSOR: " + deadline + " PROJECT NAME: " + projectName);

        if (deadline.length() < 2) {
            System.out.println("Zły format deadline'u. Message ID: " + gmailMessage.getId()
                    + "; wiadomość zapisana do pliku Error.xlsx");
            fileName = "Error.xlsx";

        } else if (deadline.equals("ASAP")) {
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
            switch (deadline.substring(3, 5)) {
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
                default:
                    System.out.println("Zły format deadline'u. Message ID: " + gmailMessage.getId()
                            + ", projekt: " + projectName + "; wiadomość zapisana do pliku Error.xlsx");
                    fileName = "Error.xlsx";
            }
        }
        return fileName;
    }


    private void addNewMsgToTheFile(String fileName, XSSFWorkbook workbook, Project project, Message gmailMessage) throws IOException {

        boolean checkIfExists = false;
        String projectName = project.getName();
        String value = project.getValue();
        String deadline = project.getDeadline();
        XSSFSheet sheet = workbook.getSheetAt(0);

        //check all rows if message that was found already exist in the Excel file (checking by ID of message)
        //this is done for all found messages
        for (Row row : sheet) {
            Cell cel = row.getCell(0);
            if (cel.getStringCellValue().equals(gmailMessage.getId())) {
                System.out.println("---Ta wiadomość już w pliku istnieje---");
                checkIfExists = true;
                break;
            }
        }
        int rowCount = sheet.getLastRowNum(); //last filled row index

        //if message is not yet in the file - add it
        if (!checkIfExists) {

            //creating new row
            Row row = sheet.createRow(rowCount + 1);

            //creating new cells

            //Msg ID
            Cell cell = row.createCell(0);
            cell.setCellValue(gmailMessage.getId());

            //Date of adding to the file
            CreationHelper createHelper = workbook.getCreationHelper();
            CellStyle cellStyle1 = workbook.createCellStyle();
            cellStyle1.setDataFormat(createHelper.createDataFormat().getFormat("dd/mm/yyyy"));
            Cell cell1 = row.createCell(1);
            cell1.setCellValue(LocalDate.now());
            cell1.setCellStyle(cellStyle1);

            //Project name
            Cell cell2 = row.createCell(2);
            cell2.setCellValue(projectName);

            //Value
            Cell cell3 = row.createCell(3);
            cell3.setCellValue(value);

            //Deadline
            Cell cell4 = row.createCell(4);
            cell4.setCellValue(deadline);

            //Date of receiving mail
            Cell cell5 = row.createCell(5);
            Date date = new Date(gmailMessage.getInternalDate());
            DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            cell5.setCellValue(df.format(date));


            // Write the output to the file
            FileOutputStream fileOut = new FileOutputStream(fileName);
            //Files.newOutputStream("C:\Users\iga\Java\projectsGmailApi\target",) fileOut = Files.newInputStream(fileName);
            workbook.write(fileOut);
            workbook.close();
            fileOut.close();
        }
    }

    public XSSFWorkbook createFile() {
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
