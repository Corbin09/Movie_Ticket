package Se2.MovieTicket.controllers;

import Se2.MovieTicket.dto.*;
import Se2.MovieTicket.impl.UserDetailsImpl;
import Se2.MovieTicket.model.*;
import Se2.MovieTicket.repository.FilmRatingRepository;
import Se2.MovieTicket.repository.FilmRepository;
import Se2.MovieTicket.repository.UserLikeFilmRepository;
import Se2.MovieTicket.repository.UserReviewRepository;
import Se2.MovieTicket.service.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/")
public class HomeController {
    @GetMapping("/home")
    public String home(
            @RequestParam(defaultValue = "1") int currentPageNowShowing,
            @RequestParam(defaultValue = "1") int currentPageComingSoon,
            Model model, HttpServletRequest request) {
        model.addAttribute("currPage", "home");

        // First try to get user from session
        HttpSession session = request.getSession(false);
        User sessionUser = null;
        if (session != null) {
            sessionUser = (User) session.getAttribute("user");
            if (sessionUser != null) {
                model.addAttribute("user", sessionUser);
            }
        }

        // If not in session, try from SecurityContext
        if (sessionUser == null) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl) {
                UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
                Optional<User> userOptional = userService.getUserById(userDetails.getId());

                if (userOptional.isPresent()) {
                    User user = userOptional.get();
                    model.addAttribute("user", user);

                    // Save to session for future requests
                    if (session != null) {
                        session.setAttribute("user", user);
                    }
                }
            }
        }

        // Format ngày tháng cho các phim
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        // Fetch paginated films for Now Showing and Coming Soon, and map to DTO with formatted date
        int size = 8; // Số lượng phim hiển thị trên mỗi trang
        Page<Film> nowShowingPage = filmService.getNowShowingFilms(currentPageNowShowing, size);
        Page<Film> comingSoonPage = filmService.getComingSoonFilms(currentPageComingSoon, size);

        // Convert danh sách phim Now Showing thành DTO và format releaseDate
        List<FilmDTO> nowShowingMovies = nowShowingPage.getContent().stream().map(film -> {
            FilmDTO filmDTO = new FilmDTO();
            filmDTO.setFilmId(film.getFilmId());
            filmDTO.setFilmName(film.getFilmName());
            filmDTO.setFilmImg(film.getFilmImg());
            filmDTO.setFilmTrailer(film.getFilmTrailer());
            filmDTO.setFilmDescription(film.getFilmDescription());

            // Format releaseDate
            filmDTO.setReleaseDate(film.getReleaseDate());
            filmDTO.setFormattedReleaseDate(film.getReleaseDate().format(formatter));

            // Get category names for the film
            List<String> categoryNames = filmService.getCategoryNamesByFilmId(film.getFilmId());
            filmDTO.setCategoryNames(categoryNames);

            filmDTO.setDuration(film.getDuration());
            filmDTO.setFilmType(film.getFilmType());
            filmDTO.setCountry(film.getCountry());
            filmDTO.setAgeLimit(film.getAgeLimit());
            return filmDTO;
        }).collect(Collectors.toList());

        // Tương tự, Convert danh sách phim Coming Soon thành DTO
        List<FilmDTO> comingSoonMovies = comingSoonPage.getContent().stream().map(film -> {
            FilmDTO filmDTO = new FilmDTO();
            filmDTO.setFilmId(film.getFilmId());
            filmDTO.setFilmName(film.getFilmName());
            filmDTO.setFilmImg(film.getFilmImg());
            filmDTO.setFilmTrailer(film.getFilmTrailer());
            filmDTO.setFilmDescription(film.getFilmDescription());

            // Format releaseDate
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

        // Add formatted lists to the model
        model.addAttribute("nowShowingMovies", nowShowingMovies);
        model.addAttribute("comingSoonMovies", comingSoonMovies);

        model.addAttribute("currentPageNowShowing", currentPageNowShowing);
        model.addAttribute("totalPagesNowShowing", nowShowingPage.getTotalPages());

        model.addAttribute("currentPageComingSoon", currentPageComingSoon);
        model.addAttribute("totalPagesComingSoon", comingSoonPage.getTotalPages());

        // Fetch all films (if needed for other purposes)
        List<Film> films = filmService.getAllFilms();
        model.addAttribute("films", films);

        // Add flag to indicate that we're not in search mode
        model.addAttribute("searchPerformed", false);

        return "home";  // Trả về trang template home.html
    }
    @Autowired
    private UserLikeFilmService userLikeFilmService;


    @Autowired
    private UserReviewService userReviewService;


    @Autowired
    private UserLikeFilmRepository userLikeFilmRepository;

    @Autowired
    private FilmRatingRepository filmRatingRepository;

    @Autowired
    private UserReviewRepository userReviewRepository;

    @Autowired
    private UserService userService;

    @Autowired
     private FilmService filmService;



    @GetMapping("/detail-movie")
    public String viewMovieDetail(
            @RequestParam("id") Long filmId,
            Model model, HttpServletRequest request) {

        model.addAttribute("currPage", "home");
        // Get user from session or SecurityContext
        User sessionUser = getUserFromSessionOrContext(request);

        if (sessionUser != null) {
            model.addAttribute("user", sessionUser);

            // Check if user has liked this film
            boolean userLikedFilm = userLikeFilmService.hasUserLikedFilm(sessionUser.getUserId(), filmId);
            model.addAttribute("userLikedFilm", userLikedFilm);

            // Check if user has reviewed this film
            UserReview userReview = userReviewService.findUserReviewByUserAndFilm(sessionUser.getUserId(), filmId);
            model.addAttribute("userReview", userReview);
        } else {
            model.addAttribute("userLikedFilm", false);
        }

        // Fetch film and convert to DTO
        Optional<Film> filmOptional = filmService.getFilmById(filmId);
        if (filmOptional.isPresent()) {
            Film film = filmOptional.get();

            FilmDTO filmDTO = convertFilmToDTO(film);
            model.addAttribute("film", filmDTO);

            // Get film rating
            FilmRating filmRating = film.getFilmRating();
            if (filmRating != null) {
                model.addAttribute("filmRating", filmRating);

                // Calculate star display (for CSS)
                double starDisplay = Math.round(filmRating.getFilmRate() * 2) / 2.0; // Round to nearest 0.5
                model.addAttribute("starDisplay", starDisplay);
            } else {
                // Default values if no ratings exist
                model.addAttribute("filmRating", new FilmRating());
                model.addAttribute("starDisplay", 0.0);
            }

            // Get user reviews
            List<UserReviewDTO> userReviews = getUserReviewsForFilm(film);
            model.addAttribute("userReviews", userReviews);

        } else {
            return "redirect:/home";
        }

        return "details-movie";
    }

    @PostMapping("/detail-movie/save-film")
    public ResponseEntity<?> saveFilm(@RequestBody Map<String, Object> payload, HttpServletRequest request) {
        // Get user from session or SecurityContext
        User currentUser = getUserFromSessionOrContext(request);

        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in");
        }

        // Extract filmId from the request body
        Long filmId;
        try {
            filmId = Long.parseLong(payload.get("filmId").toString());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid film ID");
        }

        // Check if user already liked the film
        UserLikeFilmId id = new UserLikeFilmId(currentUser.getUserId(), filmId);
        Optional<UserLikeFilm> existingLike = userLikeFilmRepository.findById(id);

        if (existingLike.isPresent()) {
            // User already liked - remove like
            userLikeFilmRepository.delete(existingLike.get());
            return ResponseEntity.ok("Film removed from favorites");
        } else {
            // User hasn't liked - add like
            UserLikeFilm userLikeFilm = new UserLikeFilm();
            userLikeFilm.setId(id);
            userLikeFilm.setUser(currentUser);

            Optional<Film> filmOptional = filmService.getFilmById(filmId);
            if (filmOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Film not found");
            }

            userLikeFilm.setFilm(filmOptional.get());
            userLikeFilmRepository.save(userLikeFilm);
            return ResponseEntity.ok("Film added to favorites");
        }
    }

    @PostMapping("/detail-movie/add-review")
    public ResponseEntity<?> addReview(@RequestBody Map<String, Object> payload, HttpServletRequest request) {
        try {
            // Get user from session
            User currentUser = getUserFromSessionOrContext(request);
            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in");
            }

            Long userId = currentUser.getUserId();
            Long filmId = Long.parseLong(payload.get("filmId").toString());
            Integer star = Integer.parseInt(payload.get("star").toString());
            String comment = (String) payload.get("comment");
            boolean isUpdate = Boolean.parseBoolean(payload.get("isUpdate").toString());

            // Get the film
            Optional<Film> filmOptional = filmService.getFilmById(filmId);
            if (filmOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Film not found");
            }

            // Check if review exists using a direct count query
            boolean exists = userReviewRepository.existsByUserIdAndFilmId(userId, filmId);
            Integer oldStar = null;

            if (exists) {
                // Get the old star value for rating calculation
                UserReview existingReview = userReviewRepository.findByUserIdAndFilmId(userId, filmId);
                if (existingReview != null) {
                    oldStar = existingReview.getStar();
                }

                // Update using a direct query
                userReviewRepository.updateReview(userId, filmId, star, comment, new Date());
            } else {
                // Create new review using a direct insert or through repository
                UserReview newReview = new UserReview();
                UserReviewId reviewId = new UserReviewId(userId, filmId);
                newReview.setId(reviewId);
                newReview.setUser(currentUser);
                newReview.setFilm(filmOptional.get());
                newReview.setStar(star);
                newReview.setComments(comment);
                newReview.setDatePosted(new Date());
                userReviewRepository.save(newReview);
            }

            // Update film rating
            updateFilmRating(filmId, oldStar, star, !exists);

            return ResponseEntity.ok("Review submitted successfully");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error submitting review: " + e.getMessage());
        }
    }

    /**
     * Helper method to update film rating
     */
    private void updateFilmRating(Long filmId, Integer oldStar, Integer newStar, boolean isNewReview) {
        FilmRating rating = filmRatingRepository.findById(filmId)
                .orElse(new FilmRating());

        if (rating.getFilmId() == null) {
            // New rating entry
            rating.setFilmId(filmId);
            Optional<Film> filmOptional = filmService.getFilmById(filmId);

            if (filmOptional.isPresent()) {
                rating.setFilm(filmOptional.get());
                rating.setSumRate(1);
                rating.setSumStar(newStar);
            }
        } else {
            // Update existing rating
            if (isNewReview) {
                // New review
                rating.setSumRate(rating.getSumRate() + 1);
                rating.setSumStar(rating.getSumStar() + newStar);
            } else if (oldStar != null) {
                // Updated review - adjust sum of stars
                rating.setSumStar(rating.getSumStar() - oldStar + newStar);
            }
        }

        // Calculate the film rate (average)
        if (rating.getSumRate() > 0) {
            double filmRate = (double) rating.getSumStar() / rating.getSumRate();
            rating.setFilmRate(filmRate);
        }

        // Save the updated rating
        filmRatingRepository.save(rating);
    }

    // Helper methods
    private User getUserFromSessionOrContext(HttpServletRequest request) {
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

        return sessionUser;
    }

    private FilmDTO convertFilmToDTO(Film film) {
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

        // Chuyển đổi directors thành DirectorDTO
        filmDTO.setDirectors(film.getFilmDirectors().stream()
                .map(fd -> {
                    DirectorDTO directorDTO = new DirectorDTO();
                    directorDTO.setDirectorId(fd.getDirector().getDirectorId());
                    directorDTO.setDirectorName(fd.getDirector().getDirectorName());
                    return directorDTO;
                })
                .collect(Collectors.toList()));

        // Chuyển đổi actors thành ActorDTO
        filmDTO.setActors(film.getFilmActors().stream()
                .map(fa -> {
                    ActorDTO actorDTO = new ActorDTO();
                    actorDTO.setActorId(fa.getActor().getActorId());
                    actorDTO.setActorName(fa.getActor().getActorName());
                    return actorDTO;
                })
                .collect(Collectors.toList()));

        // Vẫn giữ categoryNames như cũ, hoặc bạn có thể chuyển đổi tương tự
        filmDTO.setCategoryNames(film.getFilmCategories().stream()
                .map(fc -> fc.getCategory().getCategoryName())
                .collect(Collectors.toList()));

        return filmDTO;
    }

    private List<UserReviewDTO> getUserReviewsForFilm(Film film) {
        if (film.getUserReviews() == null || film.getUserReviews().isEmpty()) {
            return new ArrayList<>();
        }

        return film.getUserReviews().stream()
                .map(review -> {
                    UserReviewDTO dto = new UserReviewDTO();
                    dto.setUserId(review.getUser().getUserId());
                    dto.setFilmId(review.getFilm().getFilmId());
                    dto.setUsername(review.getUser().getUsername());
                    dto.setUserAvatar(review.getUser().getUserImg());
                    dto.setComments(review.getComments());
                    dto.setStar(review.getStar());
                    dto.setDatePosted(review.getDatePosted());
                    return dto;
                })
                .sorted(Comparator.comparing(UserReviewDTO::getDatePosted).reversed())
                .collect(Collectors.toList());
    }

    @GetMapping("/welcome-admin")
    public String showWelcomeAdminPage(Model model, HttpServletRequest request) {
        // Kiểm tra quyền ADMIN


        // Thêm user vào model để Thymeleaf hiển thị trong header
        addUserToModel(model, request);

        return "welcome-admin"; // Trả về file `welcome-admin.html`
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