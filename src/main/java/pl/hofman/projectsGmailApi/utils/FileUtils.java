package pl.hofman.projectsGmailApi.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import pl.hofman.projectsGmailApi.model.Project;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class FileUtils {

    public static String getWrongDeadlineFormatErrorMsg(String messageId, String projectName, String fileName) {

        return String.format("Wrong deadline format. Message ID: %s, project: %s; message saved to: %s", messageId, projectName, fileName);

    }

    public static List<String> createHeaderRowValues() {

        return Arrays.asList("Msg ID", "Date of adding to the file", "Project", "Value", "Deadline", "Date of receiving mail");
    }

    public static CellStyle createHeaderRowStyle(XSSFWorkbook workbook) {

        Font font = workbook.createFont();
        font.setBold(true);

        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setFont(font);

        return style;
    }

    public static Map<Integer, Object> createCellValuesMap(Project project) {

        Map<Integer, Object> valuesMap = new HashMap<>();
        AtomicInteger integer = new AtomicInteger(0);

        //column 1 message id
        String messageId = project.getGmailMessage().getId();

        //column 2 date of adding project to file
        LocalDate currentDate = LocalDate.now();

        //column 3 project name
        String projectName = project.getName();

        //column 4 project value
        String value = project.getValue();

        //column 5 project deadline
        String deadline = project.getDeadline();

        //column 6 date of receiving mail
        Date date = new Date(project.getGmailMessage().getInternalDate());
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String messageReceiptDate = df.format(date);

        Stream.of(messageId, currentDate, projectName, value, deadline, messageReceiptDate)
                .forEach(item -> valuesMap.putIfAbsent(integer.getAndIncrement(), item));

        return valuesMap;
    }

    public static Map<Integer, CellStyle> createCellStylesMap(XSSFWorkbook workbook) {

        Map<Integer, CellStyle> stylesMap = new HashMap<>();
        AtomicInteger integer = new AtomicInteger(0);

        //column 2
        CreationHelper createHelper = workbook.getCreationHelper();
        CellStyle cellStyle1 = workbook.createCellStyle();
        cellStyle1.setDataFormat(createHelper.createDataFormat().getFormat("dd/mm/yyyy"));

        Stream.of(null, cellStyle1, null, null, null, null)
                .forEach(item -> stylesMap.putIfAbsent(integer.getAndIncrement(), item));

        return stylesMap;
    }
}
