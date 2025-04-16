package Se2.MovieTicket.controllers;

import Se2.MovieTicket.dto.FilmDTO;
import Se2.MovieTicket.impl.UserDetailsImpl;
import Se2.MovieTicket.model.Film;
import Se2.MovieTicket.model.User;
import Se2.MovieTicket.service.FilmService;
import Se2.MovieTicket.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class SearchController {

    @Autowired
    private FilmService filmService;

    @Autowired
    private UserService userService;

    @GetMapping("/user-search")
    public String searchFilms(
            @RequestParam("query") String query,
            @RequestParam(value = "page", defaultValue = "1") int page,
            Model model, HttpServletRequest request) {

        // Format date
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        // Try to get user from session first
        HttpSession session = request.getSession(false);
        User sessionUser = null;
        if (session != null) {
            sessionUser = (User) session.getAttribute("user");
            if (sessionUser != null) {
                model.addAttribute("user", sessionUser);
            }
        }

        // If user not found in session, check SecurityContext
        if (sessionUser == null) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl) {
                UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
                Optional<User> userOptional = userService.getUserById(userDetails.getId());

                if (userOptional.isPresent()) {
                    User user = userOptional.get();
                    model.addAttribute("user", user);

                    // Save user to session for later use
                    if (session != null) {
                        session.setAttribute("user", user);
                    }
                }
            }
        }

        model.addAttribute("currPage", "home"); // Keep the home menu item active

        // Set up pagination for search results
        int pageSize = 8; // Number of films per page
        Pageable pageable = PageRequest.of(page - 1, pageSize);

        // Perform the search query
        Page<Film> searchResultsPage = filmService.searchFilms(query, pageable);

        // Convert Films to FilmDTOs
        List<FilmDTO> searchResultsDTOs = searchResultsPage.getContent().stream().map(film -> {
            FilmDTO filmDTO = new FilmDTO();
            filmDTO.setFilmId(film.getFilmId());
            filmDTO.setFilmName(film.getFilmName());
            filmDTO.setFilmImg(film.getFilmImg());
            filmDTO.setFilmTrailer(film.getFilmTrailer());
            filmDTO.setFilmDescription(film.getFilmDescription());
            filmDTO.setReleaseDate(film.getReleaseDate());
            filmDTO.setDuration(film.getDuration());
            filmDTO.setFilmType(film.getFilmType());
            filmDTO.setCountry(film.getCountry());
            filmDTO.setAgeLimit(film.getAgeLimit());

            // Extract related data using DTOs
            filmDTO.setDirectorNames(film.getFilmDirectors().stream()
                    .map(fd -> fd.getDirector().getDirectorName())
                    .collect(Collectors.toList()));

            filmDTO.setActorNames(film.getFilmActors().stream()
                    .map(fa -> fa.getActor().getActorName())
                    .collect(Collectors.toList()));

            filmDTO.setCategoryNames(film.getFilmCategories().stream()
                    .map(fc -> fc.getCategory().getCategoryName())
                    .collect(Collectors.toList()));

            return filmDTO;
        }).collect(Collectors.toList());

        // Add search attributes to model
        model.addAttribute("searchPerformed", true);
        model.addAttribute("searchQuery", query);
        model.addAttribute("searchResults", searchResultsDTOs);
        model.addAttribute("searchResultsCount", searchResultsPage.getTotalElements());
        model.addAttribute("currentPageSearch", page);
        model.addAttribute("totalPagesSearch", searchResultsPage.getTotalPages());

        return "home"; // Reuse the home template
    }
}