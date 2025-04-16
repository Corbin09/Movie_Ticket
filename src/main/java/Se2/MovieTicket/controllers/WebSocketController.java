package Se2.MovieTicket.controllers;

import Se2.MovieTicket.service.FilmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebSocketController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private FilmService filmService;

    @GetMapping("/sendMovieData")
    public String sendMovieData() {
        messagingTemplate.convertAndSend("/topic/movies", filmService.getAllFilms());
        return "redirect:/admin-dashboard";
    }
}
