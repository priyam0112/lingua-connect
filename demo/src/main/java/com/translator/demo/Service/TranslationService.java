package com.translator.demo.Service;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@Service
public class TranslationService {

    private Map<String, Map<String, String>> translationCache = new HashMap<>();

    private static final String API_KEY = "RTKS08R-RJ4MTTK-GGJ2787-C157J7T";
    private static final String API_URL = "https://api.lecto.ai/v1/translate/text";

    public String translate(String text, String targetLang) {
        // Check cache first
        if (translationCache.containsKey(text) && translationCache.get(text).containsKey(targetLang)) {
            return translationCache.get(text).get(targetLang);
        }

        try {
            // Create connection
            URL url = new URL(API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("x-api-key", API_KEY);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // Request body (single text + targetLang)
            String body = "{"
                    + "\"texts\": [\"" + text + "\"],"
                    + "\"to\": [\"" + targetLang + "\"],"
                    + "\"from\": \"en\""
                    + "}";

            try (OutputStream os = conn.getOutputStream()) {
                os.write(body.getBytes());
                os.flush();
            }

            // Read response
            StringBuilder response = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
            }

            String responseBody = response.toString();

            // Extract translation manually without JSON lib
            String translatedText = extractTranslation(responseBody);

            // Cache result
            translationCache.computeIfAbsent(text, k -> new HashMap<>())
                    .put(targetLang, translatedText);

            return translatedText;

        } catch (Exception e) {
            e.printStackTrace();
            return text; // fallback: return original text
        }
    }

    /**
     * Naive extractor for the first translated string
     */
    private String extractTranslation(String response) {
        // Example response: {"translations":[{"to":"es","translated":["Hola"]}], "from":"en"}
        String marker = "\"translated\":[\"";
        int start = response.indexOf(marker);
        if (start == -1) return response; // fallback

        start += marker.length();
        int end = response.indexOf("\"", start);
        if (end == -1) return response;

        return response.substring(start, end);
    }
}
