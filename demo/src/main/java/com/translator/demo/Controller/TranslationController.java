package com.translator.demo.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.translator.demo.Service.TranslationService;

@RestController
@RequestMapping("api/translate")
public class TranslationController {
    private final TranslationService translationService;

    public TranslationController(TranslationService translationService){
        this.translationService = translationService;
    }

    @GetMapping
    public String translate(@RequestParam String text, @RequestParam String targetLang){
        return translationService.translate(text, targetLang);
    }

    @PostMapping
    public String translatePost(@RequestBody TranslateRequest request){
        return translationService.translate(request.getText(), request.getTargetLang());
    }

    static class TranslateRequest{
        private String text;
        private String targetLang;
        public String getText() {
            return text;
        }
        public void setText(String text) {
            this.text = text;
        }
        public String getTargetLang() {
            return targetLang;
        }
        public void setTargetLang(String targetLang) {
            this.targetLang = targetLang;
        }
    }
}
