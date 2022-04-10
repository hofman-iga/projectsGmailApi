package pl.hofman.projectsGmailApi;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbookFactory;
import org.mortbay.util.IO;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MessageProcessor {

//    String part2 = parts[0]; //"Hello!"
//    String part3 = parts[1]; //"Project"
//    String part4 = parts[2]; //Project_name
//    String part5 = parts[3]; //"Value"
//    String part6 = parts[4]; //Value
//    String part7 = parts[5]; //"Deadline"
//    String part8 = parts[6]; //Date - can be "ASAP"
//    String part9 = parts[7]; //Timing

    //ListMessagesResponse messagesByQuery;

    public MessageProcessor(){
    }

    private  ListMessagesResponse listAllMessagesByQuery(Gmail service, String user, String userQuery, int daysNumber) throws IOException {
        try {
            return setQuery(service, user, userQuery, daysNumber).execute();
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        }
    }
    private Gmail.Users.Messages.List setQuery(Gmail service, String user, String userQuery, int daysNumber) throws IOException {
        try{
            return service.users().messages().list(user).setQ(userQuery + daysNumber + "d");
        } catch (IOException e) {
            throw new IOException("No messages matching the query.");
        }
    }

    public List<Message> findAllMessagesFromPages(Gmail service, String user, String userQuery, int daysNumber) throws IOException {

        //Create ArrayList to put there all messages
        ArrayList<Message> messages = new ArrayList<Message>();


        //List of messages meeting the criteria
        ListMessagesResponse listMessagesResponse = listAllMessagesByQuery(service, user, userQuery, daysNumber);

        //adding messages to ArrayList
        messages.addAll(listMessagesResponse.getMessages());
        System.out.println("Wiadomości spełniające kryteria (strona 1): " + listMessagesResponse.toPrettyString());

        //check if there are more than one page available (if yes nextPageToken is displayed with first results of listMessages)
        int k = 2;
        while (listMessagesResponse.getNextPageToken() != null) {

            String token = listMessagesResponse.getNextPageToken();
            listMessagesResponse = setQuery(service, user, userQuery, daysNumber).setPageToken(token).execute();
            messages.addAll(listMessagesResponse.getMessages());
            System.out.println("Wiadomości spełniające kryteria (strona " + k + "): " + listMessagesResponse.toPrettyString());
            k++;
        }
     return messages;
    }


    //finding main project messages from the list (containing projects data) - thread id = msg id
    //this messages has only id and thread id, no content is available/accessible
    public List<Message> findMainMessages(Gmail service, String user, String userQuery, int daysNumber) throws NullPointerException, IOException {
        List<Message> messages = findAllMessagesFromPages(service, user, userQuery, daysNumber);

        ArrayList<Message> mainMessagesInThread = new ArrayList<>();

        for (int i = 0; i < messages.size(); i++) {

            Message message = messages.get(i);

            if (message.getId().equals(message.getThreadId())) {
                mainMessagesInThread.add(message);
            }
        }
        return mainMessagesInThread;
    }

    //finding full messages (with details like content) by id and saving them to the list
    public List<Message> findMainGmailMessages(Gmail service, String user, String userQuery, int daysNumber) throws NullPointerException, IOException {
        List<Message> mainMessagesInThread = findMainMessages(service, user, userQuery, daysNumber);

        ArrayList<Message> mainGmailMessagesInThread = new ArrayList<>();

        for (Message msg : mainMessagesInThread) {

            try {
                Message gmailMessage = service.users().messages().get(user, msg.getId()).setFormat("full").execute();
                mainGmailMessagesInThread.add(gmailMessage);
            } catch (IOException e) {
                throw new IOException("No messages found to display");
            }
        }
        return mainGmailMessagesInThread;
    }

    /** moved to FileProcessor **/
//    public void saveMessagesInTheFile(List<Message> mainGmailMessagesInThread) throws IOException, NullPointerException {
//        XSSFWorkbook workbook;
//        String fileName = null;
//
//        for (Message msg : mainGmailMessagesInThread) {
//            fileName = fileProcessor.choosingFile(msg);
//            String projectName = MessageProcessor.getProjectName(msg);
//
//            try {
//                FileInputStream inputStream = new FileInputStream(new File(fileName));
//                workbook = (XSSFWorkbook) XSSFWorkbookFactory.create(inputStream);
////                sheet = workbook.getSheetAt(0);
//            } catch (FileNotFoundException e) {
//
//                workbook = fileProcessor.createFile();
//            }
//            MessageProcessor.singleProjectMessagesDisplay(msg);
//
//            fileProcessor.addNewMsgToTheFile(fileName, workbook, msg);
//
//        }
//    }

    public String[] messageSplit(Message gmailMsg) {

        String content = gmailMsg.getSnippet();
        String[] parts = content.split(" ");
        return parts;
    }

    /** moved to Project **/
//    public static String getProjectName (Message gmailMessage) {
//        String[] parts = MessageProcessor.messageSplit(gmailMessage);
//        String projectName = parts[2];
//        return projectName;
//    }
//
//    public static String getValue (Message gmailMessage) {
//        String[] parts = MessageProcessor.messageSplit(gmailMessage);
//        String value = " ";
//
//        for (int i = 0; i < parts.length; i++) {
//
//            if (parts[i].equals("Deadline:") || parts[i].equals("Deadline")){
//                StringBuilder sb = new StringBuilder();
//                for (int j = 4; j < i; j++) {
//                    sb.append(parts[j]);
//                }
//                value = sb.toString();
//            }
//        }
//        return value;
//    }
//
//    public static String getDeadline (Message gmailMessage) {
//        String[] parts = MessageProcessor.messageSplit(gmailMessage);
//        String deadline = " ";
//
//        for (int i = 0; i < parts.length; i++) {
//            if (parts[i].equals("Deadline:") || parts[i].equals("Deadline")){
//                if (parts[i+1].equals("ASAP")) {
//                    deadline = parts[i+1];
//                } else {
//                    deadline = (parts[i+1] + " " + parts[i+2]);
//                }
//            }
//        }
//        return deadline;
//    }


    public static void messagesDisplay(List<Message> messages) {
//        ArrayList<Message> msgs = mainMessagesInThread;
        for (int i = 0; i < messages.size(); i++) {

            Message message = messages.get(i);
            System.out.println("");
            System.out.println("Message " + i + " ID: " + message.getId());
            System.out.println("Message id and message thread id: " + message.getId() + " thread: " + message.getThreadId());
            System.out.println("Index 'i': " + i);
        }
    }

    /** moved to Project **/
    //Displays project details from msgs
    public void projectMessagesDisplay(List<Message> gmailMessages) {

        for (Message msg : gmailMessages) {
            Project project = new Project(msg, this);


            String projectName = project.getName();
            String projectValue = project.getValue();
            String projectDeadline = project.getDeadline();


            System.out.println("Message id: " + msg.getId());
            System.out.println("Message thread: " + msg.getThreadId());
            System.out.println("Project: " + projectName);
            System.out.println("Value: " + projectValue);
            System.out.println("Deadline: " + projectDeadline);
            System.out.println("");
        }
    }

    public void singleProjectMessagesDisplay(Message gmailMessage) {

        Project project = new Project(gmailMessage, this);

        //String[] parts = MessageProcessor.messageSplit(gmailMessage);
        String projectName = project.getName();
        String projectValue = project.getValue();
        String projectDeadline = project.getDeadline();

        System.out.println("***");
        System.out.println("Message id: " + gmailMessage.getId());
        System.out.println("Message thread: " + gmailMessage.getThreadId());
        System.out.println("Project: " + projectName);
        System.out.println("Value: " + projectValue);
        System.out.println("Deadline: " + projectDeadline);
    }
}

