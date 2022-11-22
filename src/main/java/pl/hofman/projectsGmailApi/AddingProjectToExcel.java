package pl.hofman.projectsGmailApi;


import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import org.apache.commons.math3.stat.descriptive.summary.Product;


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

        String userQuery = "subject:\"WEProof project\" OR subject:Nagajob AND newer_than:";

        MessageProcessor messageProcessor = new MessageProcessor();
        FileProcessor fileProcessor = new FileProcessor(messageProcessor);

        //Ask user to give number of days in the past (including today) to check mails from
        int daysNumber = askUserForNumberOfDays();

        List<Message> mainGmailMessages = messageProcessor.findMainGmailMessages(service, user, userQuery, daysNumber);

        messageProcessor.projectMessagesDisplay(mainGmailMessages);

        fileProcessor.saveMessagesInTheFile(mainGmailMessages);
    }

    private static int askUserForNumberOfDays() {

        System.out.println("Podaj liczbę dni, z których chcesz sprawdzić wiadomości (wliczając dzisiaj)");

        int daysNumber = 0;
        boolean ifNumber = true;

        while (ifNumber) {
            try {
                Scanner scanner = new Scanner(System.in);
                daysNumber = scanner.nextInt();
                ifNumber = false;
            } catch (InputMismatchException e) {
                System.out.println("Zły format danych, wprowadź liczbę.");
            }
        }
        return daysNumber;
    }
}
