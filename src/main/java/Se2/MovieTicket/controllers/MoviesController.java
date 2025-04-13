package Se2.MovieTicket.controllers;

import Se2.MovieTicket.dto.FilmDTO;
import Se2.MovieTicket.impl.UserDetailsImpl;
import Se2.MovieTicket.model.User;
import Se2.MovieTicket.service.FilmService;
import Se2.MovieTicket.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/")
public class MoviesController {
    @Autowired
    private UserService userService;

    private final FilmService filmService;

    public MoviesController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping("/manage-movies")
    public String manageMovies(
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false) String searchQuery,
            Model model,
            HttpServletRequest request) {

        if (!userService.hasRole("ROLE_ADMIN")) {
            return "redirect:/access-denied";
        }

        addUserToModel(model, request);

        Pageable pageable = PageRequest.of(page - 1, size);
        Page<FilmDTO> filmsPage = (searchQuery != null && !searchQuery.isEmpty())
                ? filmService.searchFilms(searchQuery, pageable).map(filmService::convertToDTO)
                : filmService.getAllFilms(pageable);

        model.addAttribute("films", filmsPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("totalItems", filmsPage.getTotalElements());
        model.addAttribute("totalPages", filmsPage.getTotalPages());
        model.addAttribute("pageSizes", Arrays.asList(5, 10, 20, 50));
        model.addAttribute("currPage", "manage-movies");
        model.addAttribute("searchQuery", searchQuery);

        return "manage-movies";
    }

    @PostMapping("/delete-movies")
    @ResponseBody
    public ResponseEntity<String> deleteSelectedMovies(@RequestBody Map<String, List<Long>> payload) {
        List<Long> ids = payload.get("ids");

        try {
            for (Long id : ids) {
                filmService.deleteFilm(id);
            }
            return ResponseEntity.ok("Deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to delete: " + e.getMessage());
        }
    }

    @GetMapping("/manage-movies/add-movie")
    public String showAddMovieForm(Model model, HttpServletRequest request) {
        model.addAttribute("film", new FilmDTO());
        addUserToModel(model, request); // ✅ This is the missing part!
        model.addAttribute("currPage", "manage-movies");
        return "add-movie";
    }

    @PostMapping("/manage-movies/add-movie")
    public String addMovie(@ModelAttribute FilmDTO filmDTO) {
        filmService.createFilm(filmDTO);
        return "redirect:/manage-movies";
    }

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
