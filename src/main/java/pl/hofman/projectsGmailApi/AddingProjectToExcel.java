package pl.hofman.projectsGmailApi;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Base64;
import com.google.api.client.util.StringUtils;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbookFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.*;
import java.security.GeneralSecurityException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.util.*;

public class AddingProjectToExcel {
    private static final String APPLICATION_NAME = "Gmail API Java Quickstart";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES = Collections.singletonList(GmailScopes.GMAIL_MODIFY);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    /**
     * Creates an authorized Credential object.
     *
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        InputStream in = AddingProjectToExcel.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8080).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    public static void main(String... args) throws IOException, GeneralSecurityException, NullPointerException {
        // Build a new authorized API client service.
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Gmail service = new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();

        String user = "me";

        //Ask user to give number of days in the past (including today) to check mails from
        Scanner scanner = new Scanner(System.in);
        System.out.println("Podaj liczbę dni, z których chcesz sprawdzić wiadomości (wliczając dzisiaj)");
        int daysNumber = scanner.nextInt();

        //List of messages meeting the criteria
        ListMessagesResponse listMessages = service.users().messages().list(user).setQ("subject:WEProof project AND newer_than:"+daysNumber+"d").execute();
        System.out.println("Wiadomości spełniające kryteria: " + listMessages.toPrettyString());

        //Create ArrayList to put there all messages
        List<Message> messages = new ArrayList<Message>();

        try {
            messages.addAll(listMessages.getMessages());

            //Print all found messages + number of found messages
            Long msgCount = listMessages.getResultSizeEstimate();
            System.out.println("");
            System.out.println("Liczba znalezionych wiadomości: " + msgCount);

            //process all found messages
            for (int i = 0; i < msgCount; ) {

                Message message = messages.get(i);
                System.out.println("");
                System.out.println("Message " + i + " ID: " + message.getId());

                Message gmailMessage = service.users().messages().get(user, message.getId()).setFormat("full").execute();

                String html = new String();

                //check if there is attachment (formula of finding message body is different when there is attachment)
                if (gmailMessage.getPayload().getParts().get(1).getBody().getAttachmentId() == null) {

                    System.out.println("Nie ma załącznika.");
                    html = StringUtils.newStringUtf8(Base64.decodeBase64(gmailMessage.getPayload().getParts().get(1).getBody().getData()));

                } else {
                    System.out.println("Jest załącznik.");
                    html = StringUtils.newStringUtf8(Base64.decodeBase64(gmailMessage.getPayload().getParts().get(0).getParts().get(1).getBody().getData()));
                }

                //parse message into html format
                Document doc = Jsoup.parse(html);
                //only text from html:
                //String contentText = doc.body().text();

                //extracting particular part of message between certain html tags
                Elements bTexts = doc.getElementsByTag("span");
                String btextText = bTexts.text();
                //System.out.println(btextText);

                //spliting content to parts to put it to cells in Excel file
                String[] parts = btextText.split(" ");
                String part3 = parts[2]; // "Project"
                String part4 = parts[3]; //Project_name
                String part5 = parts[4]; //"Value"
                String part6 = parts[5]; // Value
                String part7 = parts[6]; //"Deadline"
                String part8 = parts[7]; // Date
                String part9 = parts[8]; // Timing

                System.out.println(part3 + " " + part4);
                System.out.println(part5 + " " + part6);
                System.out.println(part7 + " " + part8 + " " + part9);

                //checking month of deadline
                String fileName = new String();

                if (part8.equals("ASAP")) {
                   Date date = new Date(gmailMessage.getInternalDate());
                   DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
                   df.format(date);
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    String month = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH);

                    //LocalDate currentDate = LocalDate.now();
                    //Month currentMonth = currentDate.getMonth();
                    fileName = month+".xlsx";

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

                XSSFWorkbook workbook = new XSSFWorkbook();
                XSSFSheet sheet;

                try {
                    FileInputStream inputStream = new FileInputStream(new File(fileName));
                    workbook = (XSSFWorkbook) XSSFWorkbookFactory.create(inputStream);
                    sheet = workbook.getSheetAt(0);

                } catch (FileNotFoundException e) {
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

                    //sheet.autoSizeColumn(1);
                   // sheet.autoSizeColumn(2);
                    //sheet.autoSizeColumn(3);
                    //sheet.autoSizeColumn(4);
                    //sheet.autoSizeColumn(5);
                    sheet.setColumnWidth(0,0);
                    sheet.setColumnWidth(1,0);
                    sheet.setColumnWidth(2,3000);
                    sheet.setColumnWidth(3,3000);
                    sheet.setColumnWidth(4,3000);
                    sheet.setColumnWidth(5,5100);
                }

                String checkIfExists = new String();

                //check all rows if message that was found already exist in the Excel file (checking by ID of message)
                //this is done for all found messages
                for (Row row : sheet) {
                    Cell cel = row.getCell(0);
                    if (cel.getStringCellValue().equals(message.getId())) {
                        System.out.println("Ta wiadomość już istnieje");
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
                    cell.setCellValue(message.getId());

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
                i++;
            }
        } catch (NullPointerException e){
            System.out.println("Nie znaleziono wiadomości spełniających kryteria.");
        }
    }
}
