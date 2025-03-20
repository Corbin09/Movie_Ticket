package Se2.MovieTicket.controllers;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import Se2.MovieTicket.dto.FilmDTO;
import Se2.MovieTicket.impl.UserDetailsImpl;
import Se2.MovieTicket.model.Film;
import Se2.MovieTicket.model.User;
import Se2.MovieTicket.service.FilmService;
import Se2.MovieTicket.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/films")
public class FilmController {
    @Autowired
    private FilmService filmService;
    private static final Logger logger = LoggerFactory.getLogger(FilmController.class);

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<Film>> getAllFilms() {
        if (!userService.hasRole("Admin") && !userService.hasRole("User ")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        List<Film> films = filmService.getAllFilms();
        return films.isEmpty() ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(films, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Film> getFilmById(@PathVariable("id") Long id) {
        if (!userService.hasRole("Admin") && !userService.hasRole("User ")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return filmService.getFilmById(id)
                .map(film -> new ResponseEntity<>(film, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<Film> createFilm(@RequestBody FilmDTO filmDTO) {
        if (!userService.hasRole("Admin")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        Film newFilm = filmService.createFilm(filmDTO);
        return new ResponseEntity<>(newFilm, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Film> updateFilm(@PathVariable("id") Long id, @RequestBody FilmDTO filmDTO) {
        if (!userService.hasRole("Admin")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        Film updatedFilm = filmService.updateFilm(id, filmDTO);
        return updatedFilm != null ? new ResponseEntity<>(updatedFilm, HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteFilm(@PathVariable("id") Long id) {
        if (!userService.hasRole("Admin")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        filmService.deleteFilm(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Film>> searchFilms(@RequestParam(required = false) String name)  {
        List<Film> films = filmService.searchFilms(name);
        return films.isEmpty() ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(films, HttpStatus.OK);
    }

    @GetMapping("/filter")
    public ResponseEntity<List<Film>> filterFilms(@RequestParam(required = false) String name,
                                                  @RequestParam(required = false) Date releaseDate,
                                                  @RequestParam(required = false) String country,
                                                  @RequestParam(required = false) String type,
                                                  @RequestParam(required = false) Integer age) {
        List<Film> films = filmService.filterFilms(name, releaseDate, country, type, age);
        return new ResponseEntity<>(films, HttpStatus.OK);
    }

    @GetMapping("/home")
    public String home(
            @RequestParam(defaultValue = "1") int currentPageNowShowing,
            @RequestParam(defaultValue = "1") int currentPageComingSoon,
            Model model, HttpServletRequest request) {

        logger.info("Accessing home page");

        // Lấy user từ session nếu có
        HttpSession session = request.getSession(false);
        User sessionUser = null;
        if (session != null) {
            sessionUser = (User) session.getAttribute("user");
            if (sessionUser != null) {
                logger.info("User found in session: {}", sessionUser.getUsername());
                model.addAttribute("user", sessionUser);
            }
        }

        // Nếu không có user trong session, lấy từ SecurityContext
        if (sessionUser == null) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl) {
                UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
                Optional<User> userOptional = userService.getUserById(userDetails.getId());

                if (userOptional.isPresent()) {
                    User user = userOptional.get();
                    model.addAttribute("user", user);

                    // Lưu vào session để sử dụng cho các request sau
                    if (session != null) {
                        session.setAttribute("user", user);
                        logger.info("User saved to session from SecurityContext");
                    }
                }
            }
        }

        // Số lượng phim hiển thị trên mỗi trang
        int pageSize = 4;

        // Phân trang cho Now Showing
        Page<Film> nowShowingPage = filmService.getNowShowingFilms(currentPageNowShowing, pageSize);
        model.addAttribute("nowShowingMovies", nowShowingPage.getContent());
        model.addAttribute("currentPageNowShowing", currentPageNowShowing);
        model.addAttribute("totalPagesNowShowing", nowShowingPage.getTotalPages());

        // Phân trang cho Coming Soon
        Page<Film> comingSoonPage = filmService.getComingSoonFilms(currentPageComingSoon, pageSize);
        model.addAttribute("comingSoonMovies", comingSoonPage.getContent());
        model.addAttribute("currentPageComingSoon", currentPageComingSoon);
        model.addAttribute("totalPagesComingSoon", comingSoonPage.getTotalPages());

        // Lấy toàn bộ danh sách phim để hiển thị nếu cần
        List<Film> films = filmService.getAllFilms();
        logger.info("Number of films retrieved: {}", films.size());
        model.addAttribute("films", films);

        return "home";
    }


}