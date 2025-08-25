package com.translator.demo.Service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;

@Service
public class TranslationService {


    private final StringRedisTemplate redisTemplate;

    private static final String API_KEY = "RTKS08R-RJ4MTTK-GGJ2787-C157J7T";
    private static final String API_URL = "https://api.lecto.ai/v1/translate/text";

    private static final long CACHE_TTL = 24;

    public TranslationService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public String translate(String text, String targetLang) {
        String cacheKey = text + ":" + targetLang;
        String cached = null;
        try{
            // 1. Check Redis cache
            cached = redisTemplate.opsForValue().get(cacheKey);
        } catch(Exception e){
            System.err.println("⚠️ Redis failed" + e.getMessage());
        }

        if (cached != null) {
            System.out.println("✅ Cache hit for: " + cacheKey);
            return cached;
        }else {
            System.out.println("❌ Cache miss for: " + cacheKey + " -> Calling API");
        }

        try {
            // 2. Call API (same as before)
            String translatedText = callTranslationApi(text, targetLang);

            // 3. Store in Redis with TTL
            redisTemplate.opsForValue().set(cacheKey, translatedText, CACHE_TTL, TimeUnit.HOURS);

            return translatedText;

        } catch (Exception e) {
            e.printStackTrace();
            return text; // fallback
        }
        
    }

    private String callTranslationApi(String text, String targetLang) throws Exception {
        // Your existing HTTP POST logic here
        // Return extracted translation
        URL url = new URL(API_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("x-api-key", API_KEY);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        String body = "{"
                + "\"texts\": [\"" + text + "\"],"
                + "\"to\": [\"" + targetLang + "\"],"
                + "\"from\": \"en\""
                + "}";

        try (OutputStream os = conn.getOutputStream()) {
            os.write(body.getBytes());
            os.flush();
        }

        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
        }

        return extractTranslation(response.toString());
    }
    
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
