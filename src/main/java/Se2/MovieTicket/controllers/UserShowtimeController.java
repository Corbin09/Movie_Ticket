package Se2.MovieTicket.controllers;//package Se2.MovieTicket.controllers;
//
//import Se2.MovieTicket.dto.ShowtimeDTO;
//import Se2.MovieTicket.dto.FilmDTO;
//import Se2.MovieTicket.dto.CinemaDTO;
//import Se2.MovieTicket.impl.UserDetailsImpl;
//import Se2.MovieTicket.model.Region;
//import Se2.MovieTicket.model.Cinema;
//import Se2.MovieTicket.model.User;
//import Se2.MovieTicket.service.*;
//
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpSession;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.*;
//import java.util.stream.Collectors;
//
//@Controller
//public class UserShowtimeController {
//    @Autowired
//    private ShowtimeService showtimeService;
//
//    @Autowired
//    private UserService userService;
//
//    @Autowired
//    private RegionService regionService;
//
//    @Autowired
//    private CinemaService cinemaService;
//
//    @Autowired
//    private FilmService filmService;
//
//    @GetMapping("/user/showtimes")
//    public String getShowtimes(
//            @RequestParam(required = false) Long regionId,
//            @RequestParam(required = false) Long cinemaId,
//            @RequestParam(required = false, defaultValue = "1") int page,
//            @RequestParam(required = false, defaultValue = "12") int size,
//            @RequestParam(name = "showTime", required = false) String showTime,
//            Model model, HttpServletRequest request) {
//        model.addAttribute("currPage", "showtime");
//
//
//        // Handle user session and security
//        handleUserSession(model, request);
//
//        // Get all regions for dropdown
//        List<Region> regions = regionService.getAllRegions();
//        model.addAttribute("regions", regions);
//
//        // Create pageable for pagination
//        Pageable pageable = PageRequest.of(page - 1, size);
//
//        // Initialize variables
//        Region selectedRegion = null;
//        Cinema selectedCinema = null;
//        List<CinemaDTO> cinemas = new ArrayList<>();
//        Page<FilmDTO> filmsPage;
//
//        // Handle region selection
//        if (regionId != null) {
//            selectedRegion = regionService.getRegionById(regionId).orElse(null);
//            model.addAttribute("selectedRegion", selectedRegion);
//
//            // Get cinemas for selected region
//            if (selectedRegion != null) {
//                cinemas = cinemaService.getCinemasByRegionId(regionId);
//                model.addAttribute("cinemas", cinemas);
//            }
//        }
//
//        // Handle cinema selection
//        if (cinemaId != null && selectedRegion != null) {
//            selectedCinema = cinemaService.getCinemaById(cinemaId).orElse(null);
//            model.addAttribute("selectedCinema", selectedCinema);
//        }
//
//        // Apply filters progressively and get the appropriate films
//        if (regionId == null) {
//            // No filters - show all films with pagination
//            filmsPage = filmService.getAllFilms(pageable);
//        } else if (cinemaId == null) {
//            // Only region filter - show films available in that region based on showtimes
//            filmsPage = filmService.getFilmsByRegionThroughShowtimes(regionId, pageable);
//        } else {
//            // Region and cinema filters - show all films for the cinema based on showtimes
//            filmsPage = filmService.getFilmsByCinemaThroughShowtimes(cinemaId, pageable);
//        }
//
//        // Add films to model
//        List<FilmDTO> films = filmsPage.getContent();
//        List<ShowtimeDTO> uniqueShowtimes = new ArrayList<>();
//
//        // Populate uniqueShowtimes based on different contexts
//        if (cinemaId != null) {
//            // If cinema is selected, collect unique showtimes for that cinema
//            for (FilmDTO film : films) {
//                List<ShowtimeDTO> showtimes = showtimeService.getAllShowtimesByCinemaAndFilm(
//                        cinemaId, film.getFilmId());
//
//                for (ShowtimeDTO showtime : showtimes) {
//                    if (!uniqueShowtimes.contains(showtime)) {
//                        uniqueShowtimes.add(showtime);
//                    }
//                }
//            }
//
//            // Sort uniqueShowtimes by showtime (ascending order)
//            uniqueShowtimes.sort(Comparator.comparing(ShowtimeDTO::getShowTime));
//
//            model.addAttribute("uniqueShowtimes", uniqueShowtimes);
//        }
//
//        model.addAttribute("films", films);
//        model.addAttribute("allFilms", films);
//
//        // Lọc danh sách showtimes dựa trên các filter được chọn
//        if (showTime != null) {
//            System.out.println("Filtering by selected showtimeId: " + showTime);
//
//            // Lọc danh sách phim dựa trên showtimeId đã chọn
//            List<FilmDTO> filteredFilms = films.stream()
//                    .filter(film -> {
//                        boolean hasShowtime = film.getShowtimes().stream().anyMatch(st -> st.getShowTime().equals(showTime));
//                        System.out.println("Filtering by selected showtimeId: " + showTime);
//                        System.out.println("Film: " + film.getFilmId() + " | Has Showtime: " + hasShowtime);
//                        return hasShowtime;
//                    })
//                    .collect(Collectors.toList());
//
//            System.out.println("Total filtered films: " + filteredFilms.size());
//
//            // Áp dụng pagination cho danh sách đã lọc
//            int totalItems = filteredFilms.size();
//            int fromIndex = Math.min((page - 1) * size, totalItems);
//            int toIndex = Math.min(fromIndex + size, totalItems);
//            System.out.println("Pagination - fromIndex: " + fromIndex + ", toIndex: " + toIndex);
//            System.out.println("Total item with showtime filter: " + totalItems);
//            List<FilmDTO> paginatedFilms = filteredFilms.subList(fromIndex, toIndex);
//            System.out.println("Paginated films count: " + paginatedFilms.size());
//
//            // Thêm danh sách phim đã phân trang vào model
//            model.addAttribute("films", paginatedFilms);
//            model.addAttribute("currentPage", page);
//            model.addAttribute("totalPages", (int) Math.ceil((double) totalItems / size));
//            System.out.println("TtotalPages: " + (int) Math.ceil((double) totalItems / size));
//            model.addAttribute("totalItems", totalItems);
//        } else {
//            System.out.println("No specific showTime selected, loading all showtimes.");
//
//            for (FilmDTO film : films) {
//                List<ShowtimeDTO> allShowtimes = showtimeService.getAllShowtimesByCinemaAndFilm(cinemaId, film.getFilmId());
//                film.setShowtimes(allShowtimes);
//                System.out.println("Film: " + film.getFilmId() + " | Total Showtimes: " + allShowtimes.size());
//            }
//
//            System.out.println("Total films without filter: " + films.size());
//
//            model.addAttribute("films", films);
//            model.addAttribute("currentPage", page);
//            model.addAttribute("totalPages", filmsPage.getTotalPages());
//            model.addAttribute("totalItems", filmsPage.getTotalElements());
//        }
//
//        // Age restriction note
//        model.addAttribute("ageRestriction", true);
//        return "showtime";
//    }
//    /**
//     * Handle user session and add user to model if authenticated
//     */
//    private void handleUserSession(Model model, HttpServletRequest request) {
//        // Get user from session or SecurityContext
//        HttpSession session = request.getSession(false);
//        User sessionUser = (session != null) ? (User) session.getAttribute("user") : null;
//
//        if (sessionUser == null) {
//            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//            if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl) {
//                UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
//                sessionUser = userService.getUserById(userDetails.getId()).orElse(null);
//
//                if (sessionUser != null && session != null) {
//                    session.setAttribute("user", sessionUser);
//                }
//            }
//        }
//
//        if (sessionUser != null) {
//            model.addAttribute("user", sessionUser);
//        }
//    }
//
//}