package com.translator.demo.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.translator.demo.Model.ChatMessage;
import com.translator.demo.Service.TranslationService;
import com.translator.demo.Service.UserSessionService;

@Controller
public class ChatController {

    @Autowired
    private TranslationService translationService;

    @Autowired
    private UserSessionService userSessionService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // ✅ Step 1: Register user language when they join
    @MessageMapping("/register")
    public void register(ChatMessage message) {
        userSessionService.setUserLanguage(message.getSender(), message.getLanguage());
    }

    // ✅ Step 2: Translate message per-user and send
    @MessageMapping("/sendMessage")
    public void sendMessage(ChatMessage message) {
        // Loop through all users
        userSessionService.getAllUsers().forEach((username, lang) -> {
            String translated = translationService.translate(message.getContent(), lang);

            ChatMessage copy = new ChatMessage(
                message.getSender(),
                translated,
                lang,
                message.getRoom()
            );

            // Send to that user's topic
            messagingTemplate.convertAndSend("/topic/" + message.getRoom() + "/" + username, copy);
        });
    }
}
