package com.translator.demo.Service;

import org.springframework.stereotype.Service;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@Service
public class UserSessionService {
    private Map<String, String> userLangMap = new ConcurrentHashMap<>();

    public void setUserLanguage(String username, String lang) {
        userLangMap.put(username, lang);
    }

    public String getUserLanguage(String username) {
        return userLangMap.getOrDefault(username, "en"); // default English
    }

    public Map<String, String> getAllUsers() {
        return userLangMap;
    }
}
