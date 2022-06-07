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
import java.util.*;
import java.util.stream.IntStream;


public class FileProcessor {

    MessageProcessor messageProcessor;

    public FileProcessor(MessageProcessor messageProcessor) {
        this.messageProcessor = messageProcessor;
    }

    public void saveMessagesInTheFile(List<Message> mainGmailMessagesInThread) throws NullPointerException, IOException {
        XSSFWorkbook workbook;
        String fileName = null;
        System.out.println("--------------------------------------------\n" +
                "Zapisywanie wiadomości projektowych do pliku:\n" +
                "--------------------------------------------\n\n");

        for (Message msg : mainGmailMessagesInThread) {
            Project project = new Project(msg, messageProcessor);
            fileName = chooseFileName(project);

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

    private String chooseFileName(Project project) {
        String fileName = null;
        String excelFileExtension = ".xlsx";
        String errorFileName = "Error";

        String deadline = project.getDeadline();
        String projectName = project.getName();

        if (deadline.length() < 2) {
            fileName = errorFileName + excelFileExtension;
            System.out.println(FileProcessorUtils.getWrongDeadlineFormatErrorMsg(project.getGmailMessage().getId(),
                    projectName, fileName));

        } else if (deadline.equals("ASAP")) {
            Date date = new Date(project.getGmailMessage().getInternalDate());
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
            df.format(date);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            String month = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH);

            fileName = month + excelFileExtension;

        } else {
            String deadlineMonth = deadline.substring(3, 5);
            fileName = Optional.ofNullable(MonthEnum.getMonthByNumber(deadlineMonth))
                    .map(monthEnum -> (monthEnum.getName() + excelFileExtension))
                    .orElseGet(() -> {
                        System.out.println(FileProcessorUtils.getWrongDeadlineFormatErrorMsg(project.getGmailMessage().getId(), projectName, errorFileName + excelFileExtension));
                        return (errorFileName + excelFileExtension);
                    });
        }
        return fileName;
    }

    private void addNewMsgToTheFile(String fileName, XSSFWorkbook workbook, Project project, Message gmailMessage) throws IOException {

        boolean checkIfExists = false;

        Map<Integer, Object> cellValues = FileProcessorUtils.createCellValuesMap(project);
        Map<Integer, CellStyle> cellStyles = FileProcessorUtils.createCellStylesMap(workbook);

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

            //creating new columns in row
            IntStream.range(0, cellValues.size())
                    .forEach(i -> createCell(row, i, cellStyles.get(i), cellValues.get(i)));

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

        CellStyle headerRowStyle = FileProcessorUtils.createHeaderRowStyle(workbook);

        List<String> headerRowCellsValues = FileProcessorUtils.createHeaderRowValues();

        // creating cells
        IntStream.range(0, headerRowCellsValues.size())
                .forEach(i -> createCell(row0, i, headerRowStyle, headerRowCellsValues.get(i)));

        sheet.setColumnWidth(0, 0);
        sheet.setColumnWidth(1, 0);
        sheet.setColumnWidth(2, 3000);
        sheet.setColumnWidth(3, 3000);
        sheet.setColumnWidth(4, 3000);
        sheet.setColumnWidth(5, 5100);

        return workbook;
    }

    private Cell createCell(Row row, int index, CellStyle style, Object cellValue) {

        Cell cell = row.createCell(index);

        if ((cellValue.getClass().equals(String.class))) {
            cell.setCellValue((String) cellValue);
        } else {
            cell.setCellValue((LocalDate) cellValue);
        }
        cell.setCellStyle(style);
        return cell;
    }

}
