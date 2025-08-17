package com.translator.demo.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
// import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.translator.demo.Model.ChatMessage;
import com.translator.demo.Service.TranslationService;

@Controller
public class ChatController {

    @Autowired
    private TranslationService translationService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/sendMessage")
    // @SendTo("/topic/messages")
    public void sendMessage(ChatMessage message) { //earlier return type was ChatMessage
        // Translate message to user preferred language
        String translated = translationService.translate(message.getContent(), message.getLanguage());
        message.setContent(translated);
        messagingTemplate.convertAndSend("/topic/" + message.getRoom(), message);
    }
}
