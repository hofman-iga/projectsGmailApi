package pl.hofman.projectsGmailApi;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class MessageProcessor {

    public MessageProcessor() {
    }

    //finding full messages (with details like content) by id and saving them to the list
    public List<Message> findMainGmailMessages(Gmail service, String user, String userQuery, int daysNumber) throws NullPointerException, IOException {
        List<Message> mainMessagesInThread = findMainMessages(service, user, userQuery, daysNumber);

        List<Message> mainGmailMessagesInThread = mainMessagesInThread.stream()
                .map(msg -> getMainGmailMessage(service, user, msg))
                .collect(Collectors.toList());
        System.out.printf("\nLiczba znalezionych wiadomości projektowych: %d\n", mainGmailMessagesInThread.size());
        return mainGmailMessagesInThread;
    }

    private Message getMainGmailMessage(Gmail service, String user, Message message) {
        try {
            return service.users().messages().get(user, message.getId()).setFormat("full").execute();
        } catch (IOException e) {
            throw new RuntimeException("User message " + message.getId() + " not found");
        }
    }

    //finding main project messages from the list (containing projects data) - thread id = msg id
    //this messages has only id and thread id, no content is available/accessible
    private List<Message> findMainMessages(Gmail service, String user, String userQuery, int daysNumber) throws NullPointerException, IOException {
        List<Message> messages = findAllMessagesFromPages(service, user, userQuery, daysNumber);

        List<Message> mainMessagesInThread = messages.stream()
                .filter(msg -> msg.getId().equals(msg.getThreadId()))
                .collect(Collectors.toList());

        return mainMessagesInThread;
    }

    private List<Message> findAllMessagesFromPages(Gmail service, String user, String userQuery, int daysNumber) throws IOException {

        //List of messages meeting the criteria
        ListMessagesResponse listMessagesResponse = listAllMessagesByQuery(service, user, userQuery, daysNumber);

        //Create list and add messages to it
        List<Message> messages = listMessagesResponse.getMessages();

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

    private ListMessagesResponse listAllMessagesByQuery(Gmail service, String user, String userQuery, int daysNumber) throws IOException {
        try {
            return setQuery(service, user, userQuery, daysNumber).execute();
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        }
    }

    private Gmail.Users.Messages.List setQuery(Gmail service, String user, String userQuery, int daysNumber) throws IOException {
        try {
            return service.users().messages().list(user).setQ(userQuery + daysNumber + "d");
        } catch (IOException e) {
            throw new IOException("No messages matching the query.");
        }
    }

    public String[] messageSplit(Message gmailMsg) {

        //    String part2 = parts[0]; //"Hello!"
        //    String part3 = parts[1]; //"Project"
        //    String part4 = parts[2]; //Project_name
        //    String part5 = parts[3]; //"Value"
        //    String part6 = parts[4]; //Value
        //    String part7 = parts[5]; //"Deadline"
        //    String part8 = parts[6]; //Date - can be "ASAP"
        //    String part9 = parts[7]; //Timing

        String content = gmailMsg.getSnippet();
        String[] parts = content.split(" ");
        return parts;
    }

    //Displays project details from msgs
    public void projectMessagesDisplay(List<Message> gmailMessages) {
        System.out.printf("--------------------------------------------\n" +
                "Wiadomości projektowe, szczegóły:\n" +
                "--------------------------------------------\n\n");

        for (Message msg : gmailMessages) {
            Project project = new Project(msg, this);

            String projectName = project.getName();
            String projectValue = project.getValue();
            String projectDeadline = project.getDeadline();

            System.out.printf("Message id: %s\n" +
                            "Message thread: %s\n" +
                            "Project name: %s\n" +
                            "Project value: %s\n" +
                            "Project deadline: %s\n\n",
                    msg.getId(), msg.getThreadId(), projectName, projectValue, projectDeadline);
        }
    }

    public void singleProjectMessagesDisplay(Message gmailMessage) {

        Project project = new Project(gmailMessage, this);

        //String[] parts = MessageProcessor.messageSplit(gmailMessage);
        String projectName = project.getName();
        String projectValue = project.getValue();
        String projectDeadline = project.getDeadline();

        System.out.printf("***\n" +
                        "Message id: %s\n" +
                        "Message thread: %s\n" +
                        "Project name: %s\n" +
                        "Project value: %s\n" +
                        "Project deadline: %s\n\n",
                gmailMessage.getId(), gmailMessage.getThreadId(), projectName, projectValue, projectDeadline);
    }

    public static void messagesDisplay(List<Message> messages) {
//        ArrayList<Message> msgs = mainMessagesInThread;
        for (int i = 0; i < messages.size(); i++) {

            Message message = messages.get(i);

            System.out.printf("\n" +
                            "Message %d id: %s\n" +
                            "Message id and message thread id: %s thread: %s\n" +
                            "Index 'i': %d\n",
                    i, message.getId(), message.getId(), message.getThreadId(), i);
        }
    }
}

