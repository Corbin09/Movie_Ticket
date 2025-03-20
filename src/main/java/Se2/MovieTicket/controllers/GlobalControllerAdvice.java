//package Se2.MovieTicket.controllers;
//
////import Se2.MovieTicket.dto.FilmRatingDTO;
//import Se2.MovieTicket.model.*;
//import Se2.MovieTicket.service.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.ControllerAdvice;
//import org.springframework.web.bind.annotation.ModelAttribute;
//import org.springframework.ui.Model;
//
//import java.util.List;
//
//@ControllerAdvice
//public class GlobalControllerAdvice {
//
//    @Autowired
//    private UserService userService;
//
//    @Autowired
//    private FilmService filmService;
//
//
//    @ModelAttribute
//    public void addAttributesToModel(Model model) {
//        // Add user to model
//        User user = userService.getCurrentUser();
//        model.addAttribute("user", user);
//        System.out.println("User added to model: " + user);
//
//        // Add films to model
//        List<Film> films = filmService.getAllFilms();
//        model.addAttribute("films", films);
//        System.out.println("Films added to model: " + films.size());
//    }
//}
