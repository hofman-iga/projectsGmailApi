package pl.hofman.projectsGmailApi;


import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;


import java.io.*;
import java.security.GeneralSecurityException;
import java.util.*;

import static pl.hofman.projectsGmailApi.AuthGmail.*;

public class AddingProjectToExcel {


    public static void main(String... args) throws GeneralSecurityException, IOException {

        // Build a new authorized API client service.
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Gmail service = new Gmail.Builder(HTTP_TRANSPORT, getJsonFactory(), getCredentials(HTTP_TRANSPORT))
                .setApplicationName(getApplicationName())
                .build();

        String user = "me";

        String userQuery = "subject:WEProof project AND newer_than:";

        MessageProcessor messageProcessor = new MessageProcessor();
        FileProcessor fileProcessor = new FileProcessor(messageProcessor);

        //Ask user to give number of days in the past (including today) to check mails from

        System.out.println("Podaj liczbę dni, z których chcesz sprawdzić wiadomości (wliczając dzisiaj)");
        int daysNumber=0;
        boolean ifNumber = true;

        while (ifNumber) {
            try {
                Scanner scanner = new Scanner(System.in);
                daysNumber = scanner.nextInt();
                ifNumber = false;
            } catch (InputMismatchException e) {
                System.out.println("Zły format danych, wprowadź liczbę");
            }
        }
        /** moved to MessageProcessor **/
//        try {
//            //Create ArrayList to put there all messages
//            ArrayList<Message> messages = new ArrayList<Message>();
//
//            /** moved to MessageProcessor **/
//            //List of messages meeting the criteria
//            ListMessagesResponse listMessages = service.users().messages().list(user).setQ("subject:WEProof project AND newer_than:" + daysNumber + "d").execute();
//
//            //adding messages to ArrayList
//            messages.addAll(listMessages.getMessages());
//            System.out.println("Wiadomości spełniające kryteria (strona 1): " + listMessages.toPrettyString());
//
//            //check if there are more than one page available (if yes nextPageToken is displayed with first results of listMessages)
//            int k = 2;
//            while (listMessages.getNextPageToken() != null) {
//
//                String token = listMessages.getNextPageToken();
//                listMessages = service.users().messages().list(user).setQ("subject:WEProof project AND newer_than:" + daysNumber + "d").setPageToken(token).execute();
//                messages.addAll(listMessages.getMessages());
//                System.out.println("Wiadomości spełniające kryteria (strona " + k + "): " + listMessages.toPrettyString());
//                k++;
//            }
        List<Message> mainGmailMessages = messageProcessor.findMainGmailMessages(service, user, userQuery, daysNumber);
            System.out.println("");
//            System.out.println("Liczba znalezionych wiadomości: " + messageProcessor
//                    .findAllMessagesFromPages(service, user, userQuery, daysNumber).size());
        System.out.println("Liczba znalezionych wiadomości: " + mainGmailMessages.size());

            /** moved to MessageProcessor **/
            //process only messages with project - first in thread (message ID the same as thread ID)
//            ArrayList<Message> mainMessages = MessageProcessor.findMainMessages(messages);
//            ArrayList<Message> mainGmailMessages = MessageProcessor.findMainGmailMessages(mainMessages, service, user);
            // zamienione na, patrz wyzej: List<Message> mainGmailMessages = messageProcessor.findMainGmailMessages(service, user, userQuery, daysNumber);

            System.out.println();
//            System.out.println("Wyświetlam wiadomości message PROJEKTOWE");
//            MessageProcessor.messagesDisplay(mainMessages);
//            System.out.println();
            //System.out.println("Wyświetlam wiadomości GMAILmessage PROJEKTOWE z detalami");
            System.out.println();
            System.out.println("--------------------------------------------");
            System.out.println("Wiadomości spełniające kryteria, szczegóły:");
            System.out.println("--------------------------------------------");
            messageProcessor.projectMessagesDisplay(mainGmailMessages);
            System.out.println("--------------------------------------------");
            System.out.println("Zapisywanie wiaomości projektowych do pliku:");
            System.out.println("--------------------------------------------");
            System.out.println();
            fileProcessor.saveMessagesInTheFile(mainGmailMessages);

//        } catch (NullPointerException e) {
//            System.out.println("Nie udało się zapisać wiadomości do pliku.");
//        }
    }
}
