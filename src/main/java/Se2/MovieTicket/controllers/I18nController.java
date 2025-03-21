package Se2.MovieTicket.controllers;

import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;

@RestController
public class I18nController {

    private final MessageSource messageSource;

    public I18nController(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @GetMapping("/api/messages")
    public String getMessage(@RequestParam String key, @RequestParam String lang) {
        Locale locale = new Locale(lang);
        return messageSource.getMessage(key, null, locale);
    }
}
