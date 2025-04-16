package Se2.MovieTicket.controllers;

import Se2.MovieTicket.dto.ActorDTO;
import Se2.MovieTicket.dto.FilmDTO;
import Se2.MovieTicket.impl.UserDetailsImpl;
import Se2.MovieTicket.model.Actor;
import Se2.MovieTicket.model.Film;
import Se2.MovieTicket.model.User;
import Se2.MovieTicket.service.ActorService;
import Se2.MovieTicket.service.FilmService;
import Se2.MovieTicket.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/")
public class ActorController {


    @Autowired
    private ActorService actorService;

    @Autowired
    private FilmService filmService;

    @Autowired
    private UserService userService;




    @GetMapping("/detail-actor")
    public String detailActor(
            @RequestParam("id") Long id,
            @RequestParam(defaultValue = "1") int currentPage,
            Model model,
            HttpServletRequest request) {
        // Add user to model (similar to other methods)
        addUserToModel(model, request);
        model.addAttribute("currPage", "home");
        // Get actor details
        Actor actor = actorService.findActorById(id);

        if (actor == null) {
            return "redirect:/home"; // Or error page
        }

        // Convert Actor entity to ActorDTO
        ActorDTO actorDTO = new ActorDTO();
        actorDTO.setActorId(actor.getActorId());
        actorDTO.setActorName(actor.getActorName());
        actorDTO.setActorImg(actor.getActorImg());
        actorDTO.setActorDescription(actor.getActorDescription());

        // Format for dates
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        // Set up pagination
        int size = 8; // Number of movies per page

        // Assuming you'll create this method in your service
        Page<Film> actorFilmsPage = filmService.getFilmsByActorId(id, currentPage, size);

        // Convert to DTOs with formatted dates
        List<FilmDTO> actorMovies = actorFilmsPage.getContent().stream().map(film -> {
            FilmDTO filmDTO = new FilmDTO();
            filmDTO.setFilmId(film.getFilmId());
            filmDTO.setFilmName(film.getFilmName());
            filmDTO.setFilmImg(film.getFilmImg());
            filmDTO.setFilmTrailer(film.getFilmTrailer());
            filmDTO.setFilmDescription(film.getFilmDescription());

            // Set both the original date and formatted date
            filmDTO.setReleaseDate(film.getReleaseDate());
            filmDTO.setFormattedReleaseDate(film.getReleaseDate().format(formatter));

            // For consistency with your Coming Soon formatting
            filmDTO.setReleaseDateFormatted(film.getReleaseDate().format(formatter));

            // Get category names for the film
            List<String> categoryNames = filmService.getCategoryNamesByFilmId(film.getFilmId());
            filmDTO.setCategoryNames(categoryNames);

            filmDTO.setDuration(film.getDuration());
            filmDTO.setFilmType(film.getFilmType());
            filmDTO.setCountry(film.getCountry());
            filmDTO.setAgeLimit(film.getAgeLimit());

            return filmDTO;
        }).collect(Collectors.toList());

        // Add data to model
        model.addAttribute("actor", actorDTO);
        model.addAttribute("movies", actorMovies);
        model.addAttribute("movieSectionTitle", "Movies Starring " + actor.getActorName());

        // Add pagination attributes
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", actorFilmsPage.getTotalPages());

        return "details-actor";
    }

    // Add this method to your controller
    private void addUserToModel(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        User sessionUser = (session != null) ? (User) session.getAttribute("user") : null;

        if (sessionUser == null) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl) {
                UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
                sessionUser = userService.getUserById(userDetails.getId()).orElse(null);

                if (sessionUser != null && session != null) {
                    session.setAttribute("user", sessionUser);
                }
            }
        }

        if (sessionUser != null) {
            model.addAttribute("user", sessionUser);
        }
    }


}