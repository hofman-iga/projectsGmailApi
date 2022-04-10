package pl.hofman.projectsGmailApi;

import com.google.api.services.gmail.model.Message;

public class Project {

    Message gmailMessage;
    MessageProcessor messageProcessor;
    String name;
    String value;
    String deadline;

    public Project(Message gmailMessage, MessageProcessor messageProcessor){
        this.messageProcessor = messageProcessor;
        this.gmailMessage = gmailMessage;
        this.name = getProjectName(gmailMessage, messageProcessor);
        this.value = getProjectValue(gmailMessage, messageProcessor);
        this.deadline = getProjectDeadline(gmailMessage, messageProcessor);
    }

    private String getProjectName (Message gmailMessage, MessageProcessor messageProcessor) {
        String[] messageParts = messageProcessor.messageSplit(gmailMessage);
        String projectName = messageParts[2];
        return projectName;
    }

    private String getProjectValue (Message gmailMessage, MessageProcessor messageProcessor) {
        String[] messageParts = messageProcessor.messageSplit(gmailMessage);
        String value = " ";

        for (int i = 0; i < messageParts.length; i++) {

            if (messageParts[i].equals("Deadline:") || messageParts[i].equals("Deadline")){
                StringBuilder sb = new StringBuilder();
                for (int j = 4; j < i; j++) {
                    sb.append(messageParts[j]);
                }
                value = sb.toString();
            }
        }
        return value;
    }

    private String getProjectDeadline (Message gmailMessage, MessageProcessor messageProcessor) {
        String[] messageParts = messageProcessor.messageSplit(gmailMessage);
        String deadline = " ";

        for (int i = 0; i < messageParts.length; i++) {
            if (messageParts[i].equals("Deadline:") || messageParts[i].equals("Deadline")){
                if (messageParts[i+1].equals("ASAP")) {
                    deadline = messageParts[i+1];
                } else {
                    deadline = (messageParts[i+1] + " " + messageParts[i+2]);
                }
            }
        }
        return deadline;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public String getDeadline() {
        return deadline;
    }
}
