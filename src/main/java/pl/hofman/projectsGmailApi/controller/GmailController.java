package pl.hofman.projectsGmailApi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import pl.hofman.projectsGmailApi.service.FileProcessor;
import pl.hofman.projectsGmailApi.service.MessageProcessor;

@RestController
public class GmailController {

    private MessageProcessor messageProcessor;

    private FileProcessor fileProcessor;

    @Autowired
    GmailController(MessageProcessor messageProcessor, FileProcessor fileProcessor) {
    this.messageProcessor = messageProcessor;
    this.fileProcessor = fileProcessor;
    }
}
