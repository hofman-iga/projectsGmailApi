package pl.hofman.projectsGmailApi.utils;

import com.google.api.services.gmail.model.Message;
import pl.hofman.projectsGmailApi.model.Project;

public class MessageUtils {

    public static void singleProjectMessagesDisplay(Project projectMessage, Message gmailMessage) {

        String projectName = projectMessage.getName();
        String projectValue = projectMessage.getValue();
        String projectDeadline = projectMessage.getDeadline();

        System.out.printf("***\n" +
                        "Message id: %s\n" +
                        "Message thread: %s\n" +
                        "Project name: %s\n" +
                        "Project value: %s\n" +
                        "Project deadline: %s\n\n",
                gmailMessage.getId(), gmailMessage.getThreadId(), projectName, projectValue, projectDeadline);
    }
}
