package Se2.MovieTicket.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@RestController
public class LanguageController {
    @Autowired
    private MessageSource messageSource;

    @GetMapping("/api/language")
    public Map<String, String> getLanguageStrings(@RequestParam(value = "lang", defaultValue = "en") String lang) {
        Locale locale = new Locale(lang);
        Map<String, String> translations = new HashMap<>();

        // Load tất cả key-value từ file properties
        translations.put("greeting", messageSource.getMessage("greeting", null, locale));
        translations.put("logout", messageSource.getMessage("logout", null, locale));
        translations.put("home", messageSource.getMessage("home", null, locale));
        translations.put("profile", messageSource.getMessage("profile", null, locale));

        return translations;
    }
}

