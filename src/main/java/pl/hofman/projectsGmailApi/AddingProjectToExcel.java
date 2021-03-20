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


    public static void main(String... args) throws IOException, GeneralSecurityException, NullPointerException {

        // Build a new authorized API client service.
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Gmail service = new Gmail.Builder(HTTP_TRANSPORT, getJsonFactory(), getCredentials(HTTP_TRANSPORT))
                .setApplicationName(getApplicationName())
                .build();

        String user = "me";

        //Ask user to give number of days in the past (including today) to check mails from
        Scanner scanner = new Scanner(System.in);
        System.out.println("Podaj liczbę dni, z których chcesz sprawdzić wiadomości (wliczając dzisiaj)");
        int daysNumber = scanner.nextInt();

        try {
            //Create ArrayList to put there all messages
            ArrayList<Message> messages = new ArrayList<Message>();

            //List of messages meeting the criteria
            ListMessagesResponse listMessages = service.users().messages().list(user).setQ("subject:WEProof project AND newer_than:" + daysNumber + "d").execute();

            //adding messages to ArrayList
            messages.addAll(listMessages.getMessages());
            System.out.println("Wiadomości spełniające kryteria (strona 1): " + listMessages.toPrettyString());

            //check if there are more than one page available (if yes nextPageToken is displayed with first results of listMessages)
            int k = 2;
            while (listMessages.getNextPageToken() != null) {

                String token = listMessages.getNextPageToken();
                listMessages = service.users().messages().list(user).setQ("subject:WEProof project AND newer_than:" + daysNumber + "d").setPageToken(token).execute();
                messages.addAll(listMessages.getMessages());
                System.out.println("Wiadomości spełniające kryteria (strona " + k + "): " + listMessages.toPrettyString());
                k++;
            }

            System.out.println("");
            System.out.println("Liczba znalezionych wiadomości: " + messages.size());

            //process only messages with project - first in thread (message ID the same as thread ID)
            ArrayList<Message> mainMessages = MessageProcessor.findMainMessages(messages);
            ArrayList<Message> mainGmailMessages = MessageProcessor.findMainGmailMessages(mainMessages, service, user);
            System.out.println();
//            System.out.println("Wyświetlam wiadomości message PROJEKTOWE");
//            MessageProcessor.messagesDisplay(mainMessages);
//            System.out.println();
//            System.out.println("Wyświetlam wiadomości GMAILmessage PROJEKTOWE z detalami");
//            System.out.println();
//            MessageProcessor.projectMessagesDisplay(mainGmailMessages);
            System.out.println("Wiadomości projektowe do pliku:");
            System.out.println();
            MessageProcessor.saveMessageInTheFile(mainGmailMessages);

        } catch (NullPointerException e) {
            System.out.println("Nie znaleziono wiadomości spełniających kryteria.");
        }
    }
}
