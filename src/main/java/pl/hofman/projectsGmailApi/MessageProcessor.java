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

    //finding main project messages from the list (containing projects data) - thread id = msg id
    //this messages has only id and thread id, no content is available/accessible
    public static ArrayList<Message> findMainMessages(ArrayList<Message> messages) throws IOException, NullPointerException {

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
            String[] parts = MessageProcessor.messageSplit(msg);
            try {
                FileInputStream inputStream = new FileInputStream(new File(fileName));
                workbook = (XSSFWorkbook) XSSFWorkbookFactory.create(inputStream);
//                sheet = workbook.getSheetAt(0);

            } catch (FileNotFoundException e) {
                workbook = FileProcessor.createFile();
            }
            MessageProcessor.singleProjectMessagesDisplay(msg);
            FileProcessor.addNewMsgToTheFile(fileName, workbook, msg, parts);
        }
    }

    public static String[] messageSplit(Message gmailMsg) {

        String content = gmailMsg.getSnippet();
        String[] parts = content.split(" ");
        return parts;
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

            String[] parts = MessageProcessor.messageSplit(msg);
            String part3 = parts[1]; //"Project"
            String part4 = parts[2]; //Project_name
            String part5 = parts[3]; //"Value"
            String part6 = parts[4]; //Value
            String part7 = parts[5]; //"Deadline"
            String part8 = parts[6]; //Date
            String part9 = parts[7]; //Timing


            System.out.println("Message id: " + msg.getId());
            System.out.println("Message thread: " + msg.getThreadId());
            System.out.println(part3 + " " + part4);
            System.out.println(part5 + " " + part6);
            System.out.println(part7 + " " + part8 + " " + part9);
            System.out.println("");
        }
    }

    public static void singleProjectMessagesDisplay(Message gmailMessage) {

        String[] parts = MessageProcessor.messageSplit(gmailMessage);
        String part3 = parts[1]; //"Project"
        String part4 = parts[2]; //Project_name
        String part5 = parts[3]; //"Value"
        String part6 = parts[4]; //Value
        String part7 = parts[5]; //"Deadline"
        String part8 = parts[6]; //Date
        String part9 = parts[7]; //Timing

        System.out.println("***");
        System.out.println("Message id: " + gmailMessage.getId());
        System.out.println("Message thread: " + gmailMessage.getThreadId());
        System.out.println(part3 + " " + part4);
        System.out.println(part5 + " " + part6);
        System.out.println(part7 + " " + part8 + " " + part9);
    }
}

