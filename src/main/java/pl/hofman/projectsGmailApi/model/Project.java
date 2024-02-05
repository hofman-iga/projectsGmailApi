package pl.hofman.projectsGmailApi.model;

import com.google.api.client.util.StringUtils;
import com.google.api.services.gmail.model.Message;

public class Project {

    Message gmailMessage;
    //MessageProcessor messageProcessor;
    String messageId;
    String threadId;
    String name;
    String value;
    String deadline;
    //String[] messageParts;

    public Project(Message gmailMessage
                   //MessageProcessor messageProcessor
    ) {
        //this.messageProcessor = messageProcessor;
        this.gmailMessage = gmailMessage;
        this.messageId = gmailMessage.getId();
        this.threadId = gmailMessage.getThreadId();
        //this.messageParts = messageSplit(gmailMessage);
        this.name = getProjectName();
        this.value = getProjectValue();
        this.deadline = getProjectDeadline();
    }


//    private String getProjectName(String[] messageParts) {
//        return messageParts[2];
//    }

    private String getProjectName() {
        String content = this.gmailMessage.getSnippet();
        if (content.contains("*")) content = content.replace("*", "");
        String name = content.substring(content.indexOf("Project"), content.indexOf("Value")).trim();
        return name.split(" ", 2)[1];
    }


//    private String getProjectValue(String[] messageParts) {
//        String value = " ";
//
//        for (int i = 0; i < messageParts.length; i++) {
//
//            if (messageParts[i].equals("Deadline:") || messageParts[i].equals("Deadline")) {
//                StringBuilder sb = new StringBuilder();
//                for (int j = 4; j < i; j++) {
//                    sb.append(messageParts[j]);
//                }
//                value = sb.toString();
//            }
//        }
//        return value;
//    }

    private String getProjectValue() {
        String content = this.gmailMessage.getSnippet();
        if (content.contains("*")) content = content.replace("*", "");
        String value = content.substring(content.indexOf("Value"), content.indexOf("Deadline")).trim();
        return value.split(" ", 2)[1];
    }

//    private String getProjectDeadline(String[] messageParts) {
//        String deadline = " ";
//
//        for (int i = 0; i < messageParts.length; i++) {
//            if (messageParts[i].equals("Deadline:") || messageParts[i].equals("Deadline")) {
//                if (messageParts[i + 1].equals("ASAP")) {
//                    deadline = messageParts[i + 1];
//                } else {
//                    deadline = (messageParts[i + 1] + " " + messageParts[i + 2]);
//                }
//            }
//        }
//        return deadline;
//    }

    private String getProjectDeadline() {
        String content = this.gmailMessage.getSnippet();
        if (content.contains("*")) content = content.replace("*", "");
        String deadline = content.substring(content.indexOf("Deadline")).trim();
        return deadline.split(" ", 2)[1];
    }

    public String getMessageId() {
        return messageId;
    }

    public String getThreadId() {
        return threadId;
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

    public Message getGmailMessage() {
        return gmailMessage;
    }

//    public String[] messageSplit(Message gmailMsg) {
//        String content = "Hello! Project: SK734 Value 303 w Deadline: ASAP";
//
//        //    String part2 = parts[0]; //"Hello!"
//        //    String part3 = parts[1]; //"Project"
//        //    String part4 = parts[2]; //Project_name
//        //    String part5 = parts[3]; //"Value"
//        //    String part6 = parts[4]; //Value
//        //    String part7 = parts[5]; //"Deadline"
//        //    String part8 = parts[6]; //Date - can be "ASAP"
//        //    String part9 = parts[7]; //Timing
//
//        //String content = gmailMsg.getSnippet();
//        if (content.contains("*")) content = content.replace("*", "");
//
//        String name = content.substring(content.indexOf("Project"), content.indexOf("Value")).trim();
//        String nam = name.split(" ", 2)[1];
//        String value = content.substring(content.indexOf("Value"), content.indexOf("Deadline")).trim();
//        String val = value.split(" ", 2)[1];
//        String deadline = content.substring(content.indexOf("Deadline")).trim();
//        String dead = deadline.split(" ", 2)[1];
//
//        return content.split(" ");
//    }
}
