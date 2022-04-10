package pl.hofman.projectsGmailApi;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbookFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class MessageProcessor {

//    String part2 = parts[0]; //"Hello!"
//    String part3 = parts[1]; //"Project"
//    String part4 = parts[2]; //Project_name
//    String part5 = parts[3]; //"Value"
//    String part6 = parts[4]; //Value
//    String part7 = parts[5]; //"Deadline"
//    String part8 = parts[6]; //Date - can be "ASAP"
//    String part9 = parts[7]; //Timing



    //finding main project messages from the list (containing projects data) - thread id = msg id
    //this messages has only id and thread id, no content is available/accessible
    public static ArrayList<Message> findMainMessages(ArrayList<Message> messages) throws NullPointerException {

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
    public static ArrayList<Message> findMainGmailMessages(ArrayList<Message> mainMessagesInThread, Gmail service, String user) throws IOException, NullPointerException {

        ArrayList<Message> mainGmailMessagesInThread = new ArrayList<>();

        for (Message msg : mainMessagesInThread) {
            Message gmailMessage = service.users().messages().get(user, msg.getId()).setFormat("full").execute();
            mainGmailMessagesInThread.add(gmailMessage);
        }
        return mainGmailMessagesInThread;
    }

    public static void saveMessageInTheFile(ArrayList<Message> mainGmailMessagesInThread) throws IOException, NullPointerException {
        XSSFWorkbook workbook;
        String fileName = null;

        for (Message msg : mainGmailMessagesInThread) {
            fileName = FileProcessor.choosingFile(msg);
            String projectName = MessageProcessor.getProjectName(msg);

            try {
                FileInputStream inputStream = new FileInputStream(new File(fileName));
                workbook = (XSSFWorkbook) XSSFWorkbookFactory.create(inputStream);
//                sheet = workbook.getSheetAt(0);
            } catch (FileNotFoundException e) {

                workbook = FileProcessor.createFile();
            }
            MessageProcessor.singleProjectMessagesDisplay(msg);

            FileProcessor.addNewMsgToTheFile(fileName, workbook, msg);

        }
    }

    public static String[] messageSplit(Message gmailMsg) {

        String content = gmailMsg.getSnippet();
        String[] parts = content.split(" ");
        return parts;
    }
    public static String getProjectName (Message gmailMessage) {
        String[] parts = MessageProcessor.messageSplit(gmailMessage);
        String projectName = parts[2];
        return projectName;
    }

    public static String getValue (Message gmailMessage) {
        String[] parts = MessageProcessor.messageSplit(gmailMessage);
        String value = " ";

        for (int i = 0; i < parts.length; i++) {

            if (parts[i].equals("Deadline:") || parts[i].equals("Deadline")){
                StringBuilder sb = new StringBuilder();
                for (int j = 4; j < i; j++) {
                    sb.append(parts[j]);
                }
                value = sb.toString();
            }
        }
        return value;
    }

    public static String getDeadline (Message gmailMessage) {
        String[] parts = MessageProcessor.messageSplit(gmailMessage);
        String deadline = " ";

        for (int i = 0; i < parts.length; i++) {
            if (parts[i].equals("Deadline:") || parts[i].equals("Deadline")){
                if (parts[i+1].equals("ASAP")) {
                    deadline = parts[i+1];
                } else {
                    deadline = (parts[i+1] + " " + parts[i+2]);
                }
            }
        }
        return deadline;
    }


    public static void messagesDisplay(ArrayList<Message> messages) {
//        ArrayList<Message> msgs = mainMessagesInThread;
        for (int i = 0; i < messages.size(); i++) {

            Message message = messages.get(i);
            System.out.println("");
            System.out.println("Message " + i + " ID: " + message.getId());
            System.out.println("Message id and message thread id: " + message.getId() + " thread: " + message.getThreadId());
            System.out.println("Index 'i': " + i);
        }
    }

    //Displays project details from msgs
    public static void projectMessagesDisplay(ArrayList<Message> gmailMessages) {

        for (Message msg : gmailMessages) {


            String projectName = MessageProcessor.getProjectName(msg);
            String projectValue = MessageProcessor.getValue(msg);
            String projectDeadline = MessageProcessor.getDeadline(msg);


            System.out.println("Message id: " + msg.getId());
            System.out.println("Message thread: " + msg.getThreadId());
            System.out.println("Project: " + projectName);
            System.out.println("Value: " + projectValue);
            System.out.println("Deadline: " + projectDeadline);
            System.out.println("");
        }
    }

    public static void singleProjectMessagesDisplay(Message gmailMessage) {

        //String[] parts = MessageProcessor.messageSplit(gmailMessage);
        String projectName = MessageProcessor.getProjectName(gmailMessage);
        String projectValue = MessageProcessor.getValue(gmailMessage);
        String projectDeadline = MessageProcessor.getDeadline(gmailMessage);

        System.out.println("***");
        System.out.println("Message id: " + gmailMessage.getId());
        System.out.println("Message thread: " + gmailMessage.getThreadId());
        System.out.println("Project: " + projectName);
        System.out.println("Value: " + projectValue);
        System.out.println("Deadline: " + projectDeadline);
    }
}

