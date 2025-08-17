package com.translator.demo.Service;

import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
public class TranslationService {

    private Map<String, Map<String, String>> mockTranslations = new HashMap<>();
    private Map<String, Map<String, String>> translationCache = new HashMap<>();


    public TranslationService() {
        addTranslation("Hello", "Hello", "Hola", "Bonjour", "नमस्ते");
        addTranslation("How are you?", "How are you?", "¿Cómo estás?", "Comment ça va?", "कैसे हो?");
        addTranslation("Good morning", "Good morning", "Buenos días", "Bonjour", "सुप्रभात");
        addTranslation("Good night", "Good night", "Buenas noches", "Bonne nuit", "शुभ रात्रि");
        addTranslation("Thank you", "Thank you", "Gracias", "Merci", "धन्यवाद");
        addTranslation("Welcome", "Welcome", "Bienvenido", "Bienvenue", "स्वागत है");
        addTranslation("Sorry", "Sorry", "Lo siento", "Désolé", "माफ़ कीजिए");
        addTranslation("Yes", "Yes", "Sí", "Oui", "हाँ");
        addTranslation("No", "No", "No", "Non", "नहीं");
        addTranslation("See you later", "See you later", "Hasta luego", "À plus tard", "फिर मिलेंगे");
        addTranslation("I love coding", "I love coding", "Me encanta programar", "J’aime coder", "मुझे कोडिंग पसंद है");
        addTranslation("Let's collaborate", "Let's collaborate", "Colaboremos", "Collaborons", "चलो सहयोग करें");
    }

    private void addTranslation(String key, String en, String es, String fr, String hi) {
        Map<String, String> map = new HashMap<>();
        map.put("en", en);
        map.put("es", es);
        map.put("fr", fr);
        map.put("hi", hi);
        mockTranslations.put(key, map);
    }

    public String translate(String text, String targetLang) {
        if (translationCache.containsKey(text) && translationCache.get(text).containsKey(targetLang)) {
            return translationCache.get(text).get(targetLang);
        }
        String translated = mockTranslations.getOrDefault(text, new HashMap<>())
                                            .getOrDefault(targetLang, text);

        translationCache.computeIfAbsent(text, k -> new HashMap<>())
                        .put(targetLang, translated);

        return translated;
    }
}


//since there are limited pre-loaded phrases, caching doesn’t really change visible performance because translations are already in memory.
// real benefit when we start using apis
