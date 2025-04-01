package Se2.MovieTicket.controllers;

import Se2.MovieTicket.dto.*;
import Se2.MovieTicket.model.*;
import Se2.MovieTicket.repository.UserRepository;
import Se2.MovieTicket.service.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import Se2.MovieTicket.impl.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private ShowtimeService showtimeService;
    @Autowired
    private UserService userService;
    @Autowired
    private RegionService regionService;
    @Autowired
    private FilmService filmService;

    @Autowired
    private RoomService roomService;

    @Autowired
    private SecurityContextRepository securityContextRepository;

    @Autowired
    private OrderService orderService;

    @Autowired
    private SeatService seatService;



    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "logout", required = false) String logout,
                            @RequestParam(value = "registered", required = false) String registered,
                            Model model) {
        logger.info("Accessing login page");

        if (error != null) {
            logger.warn("Login error detected");
            model.addAttribute("error", "Invalid username or password");
        }

        if (logout != null) {
            logger.info("User logged out");
            model.addAttribute("message", "You have been logged out successfully");
        }

        if (registered != null) {
            logger.info("User registered successfully");
            model.addAttribute("message", "Registration successful. Please log in.");
        }

        return "login";
    }

    @PostMapping("/login")
    public String login(LoginRequest loginRequest, Model model, HttpServletRequest request, HttpServletResponse response) {
        try {
            logger.info("Attempting to log in user: {}", loginRequest.getUsername());

            // Create authentication token
            UsernamePasswordAuthenticationToken authRequest =
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword());

            // Authenticate the user
            Authentication authentication = authenticationManager.authenticate(authRequest);

            // Get the security context and set the authentication
            SecurityContext securityContext = SecurityContextHolder.getContext();
            securityContext.setAuthentication(authentication);

            // Save the security context to the session
            securityContextRepository.saveContext(securityContext, request, response);

            // Log authentication details
            logger.info("Authentication successful for user: {}", loginRequest.getUsername());
            logger.info("Authorities: {}", authentication.getAuthorities());

            // Get user details
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            Long userId = userDetails.getId();

            Optional<User> userOptional = userService.getUserById(userId);
            if (userOptional.isEmpty()) {
                logger.warn("User not found for username: {}", loginRequest.getUsername());
                model.addAttribute("error", "User not found");
                return "login";
            }

            User user = userOptional.get();
            String role = user.getRole();
            logger.info("User {} logged in successfully with role: {}", user.getUsername(), role);

            // Save user to session
            HttpSession session = request.getSession();
            logger.info("🔍 Session ID: " + session.getId());
            session.setAttribute("user", user);
            session.setAttribute("username", user.getUsername());
            logger.info("User saved to session: {}", user.getUsername());

            if ("ROLE_ADMIN".equals(role)) {
                logger.info("Redirecting Admin to /pay-ticket");
                return "redirect:/welcome-admin";
            } else if ("ROLE_USER".equals(role)) {
                logger.info("Redirecting User to /home");
                return "redirect:/home";
            } else {
                logger.info("Redirecting to default index page");
                return "redirect:/pick-seat";
            }
        } catch (Exception e) {
            logger.error("Login failed: {}", e.getMessage());
            model.addAttribute("error", "Invalid credentials");
            return "login";
        }
    }

    @Autowired
    private BCryptPasswordEncoder passwordEncoder; // Mã hóa mật khẩu

    //    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CinemaService cinemaService;

    @GetMapping("/register")
    public String registerPage(Model model) {
        logger.info("Accessing registration page");
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String register(HttpServletRequest request, Model model) {
        // Extract form data
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String email = request.getParameter("email");
        String phoneNumber = request.getParameter("phoneNumber");
        String userImg = request.getParameter("userImg");
        String sex = request.getParameter("sex");
        String dateOfBirthStr = request.getParameter("dateOfBirth");

        // Validate username (required)
        if (username == null || username.trim().isEmpty()) {
            model.addAttribute("usernameError", "Username is required");
            return "register";
        }

        // Check if username already exists
        if (userRepository.findByUsername(username).isPresent()) {
            model.addAttribute("usernameError", "Username already exists");
            return "register";
        }

        // Validate password (required and min length)
        if (password == null || password.trim().isEmpty()) {
            model.addAttribute("passwordError", "Password is required");
            return "register";
        }

        if (password.length() < 6) {
            model.addAttribute("passwordError", "Password must be at least 6 characters");
            return "register";
        }

        // Confirm passwords match
        if (!password.equals(confirmPassword)) {
            model.addAttribute("confirmPasswordError", "Passwords do not match");
            return "register";
        }

        // Validate email (if provided)
        if (email != null && !email.isEmpty()) {
            String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
            if (!email.matches(emailRegex)) {
                model.addAttribute("emailError", "Please enter a valid email address");
                return "register";
            }

            // Check if email already exists
            if (userRepository.findByEmail(email).isPresent()) {
                model.addAttribute("emailError", "Email already exists");
                return "register";
            }
        }

        // Validate phone number (if provided)
        if (phoneNumber != null && !phoneNumber.isEmpty()) {
            String phoneRegex = "^[\\+]?[(]?[0-9]{3}[)]?[-\\s\\.]?[0-9]{3}[-\\s\\.]?[0-9]{4,6}$";
            if (!phoneNumber.matches(phoneRegex)) {
                model.addAttribute("phoneNumberError", "Please enter a valid phone number");
                return "register";
            }
        }

        // Validate image URL (if provided)
        if (userImg != null && !userImg.isEmpty()) {
            try {
                new URL(userImg);
            } catch (MalformedURLException e) {
                model.addAttribute("userImgError", "Please enter a valid URL");
                return "register";
            }
        }

        // Validate and parse date of birth (if provided)
        Date dateOfBirth = null;
        if (dateOfBirthStr != null && !dateOfBirthStr.isEmpty()) {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                dateFormat.setLenient(false);
                dateOfBirth = dateFormat.parse(dateOfBirthStr);
            } catch (ParseException e) {
                model.addAttribute("dateOfBirthError", "Invalid date format. Please use yyyy-MM-dd.");
                return "register";
            }
        }

        try {
            // Encode password
            String encodedPassword = passwordEncoder.encode(password);

            // Create UserDTO
            UserDTO userDTO = new UserDTO();
            userDTO.setUsername(username);
            userDTO.setPassword(encodedPassword);
            userDTO.setEmail(email);
            userDTO.setPhoneNumber(phoneNumber);
            userDTO.setUserImg(userImg);
            userDTO.setSex(sex);
            userDTO.setDateOfBirth(dateOfBirth);
            userDTO.setRole("USER");
            userDTO.setStatus("ACTIVE");

            // Save user
            userService.createUser(userDTO);
            logger.info("User registered successfully: {}", username);

            // Redirect to login page with success message
            return "redirect:/login?registered";

        } catch (Exception e) {
            logger.error("Registration failed: {}", e.getMessage());
            model.addAttribute("error", "Registration failed: " + e.getMessage());
            return "register";
        }
    }

    @GetMapping("/access-denied")
    public String accessDenied() {
        logger.warn("Access denied page accessed");
        return "error/access-denied";
    }

    @GetMapping("/pay-ticket")
    public String payTicket(Model model, HttpServletRequest request) {
        logger.info("Accessing pay-ticket page");

        if (!userService.hasRole("ROLE_ADMIN")) {
            logger.warn("Unauthorized access attempt to pay-ticket page");
            return "redirect:/access-denied";
        }

        // First try to get user from session
        HttpSession session = request.getSession(false);
        if (session != null) {
            User sessionUser = (User) session.getAttribute("user");
            if (sessionUser != null) {
                logger.info("User found in session: {}", sessionUser.getUsername());
                model.addAttribute("user", sessionUser);
                return "pay-ticket";
            }
        }

        // If not in session, try from SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl) {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            Optional<User> userOptional = userService.getUserById(userDetails.getId());

            if (userOptional.isPresent()) {
                User user = userOptional.get();
                model.addAttribute("user", user);
                logger.info("User found in SecurityContext: {} - {}", user.getUsername(), user.getUserImg());

                // Save to session for future requests
                if (session != null) {
                    session.setAttribute("user", user);
                    logger.info("User saved to session from SecurityContext");
                }
            } else {
                logger.warn("User not found in database");
            }
        } else {
            logger.warn("No authenticated user found");
        }

        return "pay-ticket";
    }

    @GetMapping("/View-movie-ticket")
    public String viewMovieTicket(
            @RequestParam("id") Long filmId,
            @RequestParam(required = false) String date,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year,
            Model model, HttpServletRequest request) {

        logger.info("Accessing view movie ticket page for film ID: {}", filmId);

        // Get user from session or SecurityContext
        HttpSession session = request.getSession(false);
        User sessionUser = (session != null) ? (User) session.getAttribute("user") : null;

        if (sessionUser == null) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl) {
                UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
                sessionUser = userService.getUserById(userDetails.getId()).orElse(null);

                if (sessionUser != null && session != null) {
                    session.setAttribute("user", sessionUser);
                    logger.info("User saved to session from SecurityContext");
                }
            }
        }

        if (sessionUser != null) {
            logger.info("User found: {}", sessionUser.getUsername());
            model.addAttribute("user", sessionUser);
        }

        // Xử lý date, month, year
        LocalDate selectedDate;
        YearMonth yearMonth;
        LocalDate today = LocalDate.now();

        // Xử lý tham số ngày và tháng
        if (date != null && !date.isEmpty()) {
            try {
                selectedDate = LocalDate.parse(date);
                logger.info("Selected date from request: {}", selectedDate);
            } catch (DateTimeParseException e) {
                logger.warn("Invalid date format: {}. Using today's date instead.", date);
                selectedDate = today;
            }
        } else {
            selectedDate = today;
            logger.info("No date provided, using today's date: {}", selectedDate);
        }

        // Xử lý tham số tháng và năm
        if (month != null && year != null) {
            try {
                yearMonth = YearMonth.of(year, month);
                logger.info("Using provided month and year: {}-{}", month, year);
            } catch (DateTimeException e) {
                logger.warn("Invalid month/year: {}/{}. Using month/year from selected date.", month, year);
                yearMonth = YearMonth.of(selectedDate.getYear(), selectedDate.getMonth());
            }
        } else {
            yearMonth = YearMonth.of(selectedDate.getYear(), selectedDate.getMonth());
            logger.info("Using month/year from selected date: {}", yearMonth);
        }

        // Fetch film and convert to DTO
        Optional<Film> filmOptional = filmService.getFilmById(filmId);
        if (filmOptional.isPresent()) {
            Film film = filmOptional.get();

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

            model.addAttribute("film", filmDTO);
            logger.info("Film details loaded for ID: {}", filmId);
        } else {
            logger.warn("Film not found with ID: {}", filmId);
            return "redirect:/films";
        }

        // Fetch cinema/showtimes dựa trên date được chọn
        List<CinemaWithShowtimesDTO> cinemasWithShowtimes = cinemaService.getCinemasWithShowtimesForFilmAndDate(filmId, selectedDate);
        model.addAttribute("cinemas", cinemasWithShowtimes);
        logger.info("Loaded {} cinemas with showtimes for film ID: {} and date: {}",
                cinemasWithShowtimes.size(), filmId, selectedDate);

        // Add calendar data với tháng và năm được chọn
        model.addAttribute("currentMonth", yearMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault()));
        model.addAttribute("currentYear", yearMonth.getYear());
        model.addAttribute("currentMonthNum", yearMonth.getMonthValue());
        model.addAttribute("weekDays", getWeekDays());
        model.addAttribute("weeks", getCalendarWeeks(yearMonth, selectedDate, filmId));
        model.addAttribute("selectedDate", selectedDate.toString());

        return "View-movie-ticket";
    }
    private String getCurrentMonthName() {
        return LocalDate.now().getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault());
    }

    private int getCurrentYear() {
        return LocalDate.now().getYear();
    }

    private int getCurrentMonthNumber() {
        return LocalDate.now().getMonthValue();
    }

    private List<DayDTO> getWeekDays() {
        List<DayDTO> days = new ArrayList<>();
        for (DayOfWeek day : DayOfWeek.values()) {
            boolean isWeekend = (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY);
            String shortName = day.getDisplayName(TextStyle.SHORT, Locale.getDefault());
            days.add(new DayDTO(shortName, isWeekend));
        }
        return days;
    }

    private List<WeekDTO> getCalendarWeeks(YearMonth yearMonth, LocalDate selectedDate, Long filmId) {
        List<WeekDTO> weeks = new ArrayList<>();

        LocalDate today = LocalDate.now();
        LocalDate firstDayOfMonth = yearMonth.atDay(1);
        LocalDate lastDayOfMonth = yearMonth.atEndOfMonth();

        // Lấy ngày đầu tiên của tuần từ thứ Hai
        LocalDate firstCalendarDate = firstDayOfMonth.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        LocalDate date = firstCalendarDate;
        while (!date.isAfter(lastDayOfMonth)) {
            List<DayDTO> days = new ArrayList<>();

            for (int i = 0; i < 7; i++) {
                boolean isToday = date.equals(today);
                boolean isPast = date.isBefore(today);
                boolean isCurrentMonth = date.getMonth() == yearMonth.getMonth();
                boolean hasShowtimes = showtimeService.hasShowtimesForFilmAndDate(filmId, date);
                String formattedDate = date.toString();  // yyyy-MM-dd format
                String shortName = date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.getDefault());

                days.add(new DayDTO(String.valueOf(date.getDayOfMonth()),   // dayOfMonth (số ngày)
                        shortName,                             // shortName (Mon, Tue...)
                        isToday,                               // isToday
                        isPast,                                // isPast
                        hasShowtimes,                          // hasShowtimes
                        formattedDate,                         // fullDate
                        date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY,  // isWeekend
                        date.equals(selectedDate),             // isSelected
                        isCurrentMonth                         // isCurrentMonth (để làm mờ các ngày không thuộc tháng hiện tại)
                ));

                date = date.plusDays(1);  // Chuyển sang ngày tiếp theo
            }
            weeks.add(new WeekDTO(days));  // Thêm tuần với danh sách các ngày
        }
        return weeks;
    }


    // Dummy method to check if showtimes exist on a specific date (logic tùy chỉnh)
    private boolean checkIfHasShowtimes(LocalDate date) {
        // Thêm logic kiểm tra (ví dụ, lấy showtimes từ database nếu cần)
        return false;  // Default logic
    }


    private LocalDate getSelectedDate() {
        return LocalDate.now();
    }


    @GetMapping("/index")
    public String index(Model model, HttpServletRequest request) {
        logger.info("Accessing index page");

        // First try to get user from session
        HttpSession session = request.getSession(false);
        User sessionUser = null;
        if (session != null) {
            sessionUser = (User) session.getAttribute("user");
            if (sessionUser != null) {
                logger.info("User found in session: {}", sessionUser.getUsername());
                model.addAttribute("user", sessionUser);
                return "index";
            }
        }

        // If not in session, try from SecurityContext
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
                    logger.info("User saved to session from SecurityContext");
                }
            }
        }

        return "index";
    }


    @GetMapping("/home")
    public String home(
            @RequestParam(defaultValue = "1") int currentPageNowShowing,
            @RequestParam(defaultValue = "1") int currentPageComingSoon,
            Model model, HttpServletRequest request) {
        model.addAttribute("currPage", "home");
        logger.info("Accessing home page");

        // First try to get user from session
        HttpSession session = request.getSession(false);
        User sessionUser = null;
        if (session != null) {
            sessionUser = (User) session.getAttribute("user");
            if (sessionUser != null) {
                logger.info("User found in session: {}", sessionUser.getUsername());
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
                        logger.info("User saved to session from SecurityContext");
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
        logger.info("Number of films retrieved: {}", films.size());
        model.addAttribute("films", films);

        // Add flag to indicate that we're not in search mode
        model.addAttribute("searchPerformed", false);

        return "home";  // Trả về trang template home.html
    }

    @GetMapping("/showtime")
    public String getShowtimes(
            @RequestParam(required = false) Long regionId,
            @RequestParam(required = false) Long cinemaId,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "12") int size,
            @RequestParam(name = "showTime", required = false) String showTime,
            Model model, HttpServletRequest request) {
        model.addAttribute("currPage", "showtime");
        logger.info("Accessing showtimes page with filters - regionId: {}, cinemaId: {}, page: {}, size: {}",
                regionId, cinemaId, page, size);

        // Handle user session and security
        handleUserSession(model, request);

        // Get all regions for dropdown
        List<Region> regions = regionService.getAllRegions();
        model.addAttribute("regions", regions);

        // Create pageable for pagination
        Pageable pageable = PageRequest.of(page - 1, size);

        // Initialize variables
        Region selectedRegion = null;
        Cinema selectedCinema = null;
        List<CinemaDTO> cinemas = new ArrayList<>();
        Page<FilmDTO> filmsPage;

        // Handle region selection
        if (regionId != null) {
            selectedRegion = regionService.getRegionById(regionId).orElse(null);
            model.addAttribute("selectedRegion", selectedRegion);

            // Get cinemas for selected region
            if (selectedRegion != null) {
                cinemas = cinemaService.getCinemasByRegionId(regionId);
                model.addAttribute("cinemas", cinemas);
            }
        }

        // Handle cinema selection
        if (cinemaId != null && selectedRegion != null) {
            selectedCinema = cinemaService.getCinemaById(cinemaId).orElse(null);
            model.addAttribute("selectedCinema", selectedCinema);
        }

        // Apply filters progressively and get the appropriate films
        if (regionId == null) {
            // No filters - show all films with pagination
            filmsPage = filmService.getAllFilms(pageable);
        } else if (cinemaId == null) {
            // Only region filter - show films available in that region based on showtimes
            filmsPage = filmService.getFilmsByRegionThroughShowtimes(regionId, pageable);
        } else {
            // Region and cinema filters - show all films for the cinema based on showtimes
            filmsPage = filmService.getFilmsByCinemaThroughShowtimes(cinemaId, pageable);
        }

        // Add films to model
        List<FilmDTO> films = filmsPage.getContent();
        List<ShowtimeDTO> uniqueShowtimes = new ArrayList<>();

        // Populate uniqueShowtimes based on different contexts
        if (cinemaId != null) {
            // If cinema is selected, collect unique showtimes for that cinema
            for (FilmDTO film : films) {
                List<ShowtimeDTO> showtimes = showtimeService.getAllShowtimesByCinemaAndFilm(
                        cinemaId, film.getFilmId());

                for (ShowtimeDTO showtime : showtimes) {
                    if (!uniqueShowtimes.contains(showtime)) {
                        uniqueShowtimes.add(showtime);
                    }
                }
            }

            // Sort uniqueShowtimes by showtime (ascending order)
            uniqueShowtimes.sort(Comparator.comparing(ShowtimeDTO::getShowTime));

            model.addAttribute("uniqueShowtimes", uniqueShowtimes);
        }

        model.addAttribute("films", films);
        model.addAttribute("allFilms", films);

        // Lọc danh sách showtimes dựa trên các filter được chọn
        if (showTime != null) {
            System.out.println("Filtering by selected showtimeId: " + showTime);

            // Lọc danh sách phim dựa trên showtimeId đã chọn
            List<FilmDTO> filteredFilms = films.stream()
                    .filter(film -> {
                        boolean hasShowtime = film.getShowtimes().stream().anyMatch(st -> st.getShowTime().equals(showTime));
                        System.out.println("Filtering by selected showtimeId: " + showTime);
                        System.out.println("Film: " + film.getFilmId() + " | Has Showtime: " + hasShowtime);
                        return hasShowtime;
                    })
                    .collect(Collectors.toList());

            System.out.println("Total filtered films: " + filteredFilms.size());

            // Áp dụng pagination cho danh sách đã lọc
            int totalItems = filteredFilms.size();
            int fromIndex = Math.min((page - 1) * size, totalItems);
            int toIndex = Math.min(fromIndex + size, totalItems);
            System.out.println("Pagination - fromIndex: " + fromIndex + ", toIndex: " + toIndex);
            System.out.println("Total item with showtime filter: " + totalItems);
            List<FilmDTO> paginatedFilms = filteredFilms.subList(fromIndex, toIndex);
            System.out.println("Paginated films count: " + paginatedFilms.size());

            // Thêm danh sách phim đã phân trang vào model
            model.addAttribute("films", paginatedFilms);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", (int) Math.ceil((double) totalItems / size));
            System.out.println("TtotalPages: " + (int) Math.ceil((double) totalItems / size));
            model.addAttribute("totalItems", totalItems);
        } else {
            System.out.println("No specific showTime selected, loading all showtimes.");

            for (FilmDTO film : films) {
                List<ShowtimeDTO> allShowtimes = showtimeService.getAllShowtimesByCinemaAndFilm(cinemaId, film.getFilmId());
                film.setShowtimes(allShowtimes);
                System.out.println("Film: " + film.getFilmId() + " | Total Showtimes: " + allShowtimes.size());
            }

            System.out.println("Total films without filter: " + films.size());

            model.addAttribute("films", films);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", filmsPage.getTotalPages());
            model.addAttribute("totalItems", filmsPage.getTotalElements());
        }

        // Age restriction note
        model.addAttribute("ageRestriction", true);
        return "showtime";
    }
//
    /**
     * Handle user session and add user to model if authenticated
     */
    private void handleUserSession(Model model, HttpServletRequest request) {
        // Get user from session or SecurityContext
        HttpSession session = request.getSession(false);
        User sessionUser = (session != null) ? (User) session.getAttribute("user") : null;

        if (sessionUser == null) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl) {
                UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
                sessionUser = userService.getUserById(userDetails.getId()).orElse(null);

                if (sessionUser != null && session != null) {
                    session.setAttribute("user", sessionUser);
                    logger.info("User saved to session from SecurityContext");
                }
            }
        }

        if (sessionUser != null) {
            logger.info("User found: {}", sessionUser.getUsername());
            model.addAttribute("user", sessionUser);
        }
    }


    @GetMapping("/pick-seat")
    public String pickSeat(
            @RequestParam("filmId") Long filmId,
            @RequestParam("cinemaId") Long cinemaId,
            @RequestParam("showtimeId") Long showtimeId,
            @RequestParam("selectedDate") String selectedDate,
            Model model, HttpServletRequest request) {

        logger.info("Accessing pick-seat page with params: filmId={}, cinemaId={}, showtimeId={}, selectedDate={}",
                filmId, cinemaId, showtimeId, selectedDate);

        // Get user from session or SecurityContext
        HttpSession session = request.getSession(false);
        User sessionUser = (session != null) ? (User) session.getAttribute("user") : null;

        if (sessionUser == null) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl) {
                UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
                sessionUser = userService.getUserById(userDetails.getId()).orElse(null);

                if (sessionUser != null && session != null) {
                    session.setAttribute("user", sessionUser);
                    logger.info("User saved to session from SecurityContext");
                }
            }
        }

        if (sessionUser != null) {
            logger.info("User found: {}", sessionUser.getUsername());
            model.addAttribute("user", sessionUser);
        }

        // Get film details
        Optional<Film> filmOptional = filmService.getFilmById(filmId);
        if (!filmOptional.isPresent()) {
            logger.warn("Film not found with ID: {}", filmId);
            return "redirect:/films";
        }
        model.addAttribute("film", convertToFilmDTO(filmOptional.get()));

        // Get cinema details
        Optional<Cinema> cinemaOptional = cinemaService.getCinemaById(cinemaId);
        if (!cinemaOptional.isPresent()) {
            logger.warn("Cinema not found with ID: {}", cinemaId);
            return "redirect:/films";
        }
        model.addAttribute("cinema", cinemaOptional.get());

        // Get showtime details
        Optional<Showtime> showtimeOptional = showtimeService.getShowtimeById(showtimeId);
        if (!showtimeOptional.isPresent()) {
            logger.warn("Showtime not found with ID: {}", showtimeId);
            return "redirect:/films";
        }
        Showtime showtime = showtimeOptional.get();
        model.addAttribute("showtime", showtime);

        // Get room details from showtime
        Room room = showtime.getRoom();
        model.addAttribute("room", room);

        // Get all seats in the room
        List<SeatDTO> seats = seatService.getSeatsByRoomId(room.getRoomId());
        model.addAttribute("seats", seats);

        // Nhóm ghế theo hàng và sắp xếp số ghế trong mỗi hàng
        Map<String, List<SeatDTO>> seatsByRow = seats.stream()
                .collect(Collectors.groupingBy(
                        SeatDTO::getSeatRow,
                        TreeMap::new,  // Dùng TreeMap để đảm bảo thứ tự hàng A-Z
                        Collectors.collectingAndThen(Collectors.toList(), list -> {
                            list.sort(Comparator.comparing(SeatDTO::getSeatNumber)); // Sắp xếp theo số ghế
                            return list;
                        })
                ));

        model.addAttribute("seatsByRow", seatsByRow);

        // Get seat status for this showtime
        Map<String, String> seatStatusMap = seatService.getSeatStatusMap(showtimeId);
        model.addAttribute("seatStatusMap", seatStatusMap);

        // Parse selected date
        LocalDate parsedDate = LocalDate.parse(selectedDate);
        model.addAttribute("selectedDate", parsedDate);
        model.addAttribute("formattedDate", parsedDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        return "pick-seat";
    }


    /**
     * Trích xuất danh sách các hàng ghế duy nhất từ danh sách ghế
     */
    private List<String> distinctSeatRows(List<SeatDTO> seats) {
        return seats.stream()
                .map(SeatDTO::getSeatRow)  // Lấy danh sách hàng ghế
                .distinct()                // Loại bỏ trùng lặp
                .sorted()                  // Sắp xếp theo thứ tự A-Z
                .collect(Collectors.toList());
    }


    // Helper method to convert Film to FilmDTO
    private FilmDTO convertToFilmDTO(Film film) {
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

        // Extract related data
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
    }























//----------------------------ADMIN PERMISSION------------------------------------------------------------------------------------------------
    @GetMapping("/welcome-admin")
    public String showWelcomeAdminPage(Model model, HttpServletRequest request) {
        // Kiểm tra quyền ADMIN
        if (!userService.hasRole("ROLE_ADMIN")) {
            return "redirect:/access-denied";
        }

        // Thêm user vào model để Thymeleaf hiển thị trong header
        addUserToModel(model, request);

        return "welcome-admin"; // Trả về file `welcome-admin.html`
    }

    @GetMapping("/manage-orders")
    public String manageOrders(
            @RequestParam(required = false) String searchCriteria,
            @RequestParam(required = false) String searchQuery,
            Model model,
            HttpServletRequest request) {

        // Get user from session or SecurityContext
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

        // Check if user has admin role
        if (!userService.hasRole("ROLE_ADMIN")) {
            return "redirect:/access-denied";
        }

        // Get orders based on search criteria and query
        List<Order> orders;

        try {
            if (searchCriteria != null && !searchCriteria.isEmpty()) {
                // Search based on selected criteria and query (even if query is empty)
                orders = orderService.searchOrdersByCriteria(searchCriteria, searchQuery != null ? searchQuery : "");
            } else {
                // Get all orders if no search criteria provided
                orders = orderService.getAllOrders();
            }
        } catch (Exception e) {
            orders = new ArrayList<>();
            model.addAttribute("errorMessage", "Error fetching orders: " + e.getMessage());
        }

        // Add orders to model
        model.addAttribute("orders", orders);

        // Pass the selected search options to the view
        model.addAttribute("searchCriteria", searchCriteria);
        model.addAttribute("searchQuery", searchQuery);

        // Add currPage attribute for menu active state
        model.addAttribute("currPage", "manage-orders");

        return "manage-orders";
    }




    @GetMapping("/manage-rooms")
    public String manageRooms(
            @RequestParam(required = false) String searchText,
            @RequestParam(required = false) String searchField,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int pageSize,
            Model model,
            HttpServletRequest request) {

        // Check if user has admin role
        if (!userService.hasRole("ROLE_ADMIN")) {
            return "redirect:/access-denied";
        }

        // Add user to model
        addUserToModel(model, request);

        // Get all cinemas for the dropdown filter
        List<Cinema> cinemas = cinemaService.getAllCinemas();
        model.addAttribute("cinemas", cinemas);

        // Create pageable object for database pagination
        Pageable pageable = PageRequest.of(page - 1, pageSize);

        // Get rooms with pagination directly from database
        Page<Room> roomsPage;

        try {
            if (searchText != null && !searchText.isEmpty() && searchField != null && !searchField.isEmpty()) {
                // Search rooms by specific field with pagination
                roomsPage = roomService.searchRoomsByFieldPaginated(searchField, searchText, pageable);
            } else {
                // Get all rooms with pagination
                roomsPage = roomService.getAllRoomsPaginated(pageable);
            }

            // Get seat counts for displayed rooms in a single query
            Map<Long, Long> seatCounts = roomService.getSeatCountsForRooms(
                    roomsPage.getContent().stream()
                            .map(Room::getRoomId)
                            .collect(Collectors.toList())
            );

            model.addAttribute("seatCounts", seatCounts);
            model.addAttribute("rooms", roomsPage.getContent());

            // Add pagination parameters
            model.addAttribute("currentPage", page);
            model.addAttribute("pageSize", pageSize);
            model.addAttribute("totalItems", roomsPage.getTotalElements());
            model.addAttribute("totalPages", roomsPage.getTotalPages());

        } catch (Exception e) {
            model.addAttribute("rooms", new ArrayList<>());
            model.addAttribute("errorMessage", "Error fetching rooms: " + e.getMessage());
            model.addAttribute("currentPage", 1);
            model.addAttribute("pageSize", pageSize);
            model.addAttribute("totalItems", 0);
            model.addAttribute("totalPages", 0);
        }

        // Pass the selected search options to the view
        model.addAttribute("currentSearchField", searchField);
        model.addAttribute("currentSearchText", searchText);

        // Add currPage attribute for sidebar active menu
        model.addAttribute("currPage", "manage-rooms");

        return "manage-rooms";
    }

    @PostMapping("/delete-rooms")
    @Transactional
    public String deleteRooms(@RequestParam("roomIds") List<Long> roomIds,
                              RedirectAttributes redirectAttributes,
                              HttpServletRequest request) {
        // Check if user has admin role
        if (!userService.hasRole("ROLE_ADMIN")) {
            return "redirect:/access-denied";
        }

        try {
            int deletedCount = roomService.deleteRoomsByIds(roomIds);
            redirectAttributes.addFlashAttribute("successMessage",
                    deletedCount + " room(s) successfully deleted.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Error deleting rooms: " + e.getMessage());
        }

        return "redirect:/manage-rooms";
    }

    @GetMapping("/add-room")
    public String addRoomForm(Model model, HttpServletRequest request) {
        // Check if user has admin role
        if (!userService.hasRole("ROLE_ADMIN")) {
            return "redirect:/access-denied";
        }

        // Add user to model
        addUserToModel(model, request);

        // Add necessary attributes for the form
        model.addAttribute("room", new Room());
        model.addAttribute("cinemas", cinemaService.getAllCinemas());
        model.addAttribute("currPage", "manage-rooms");

        return "addroom";
    }



    @PostMapping("/rooms/save")
    public String saveRoom(@Valid @ModelAttribute("room") Room room,
                           BindingResult bindingResult,
                           RedirectAttributes redirectAttributes,
                           Model model,
                           HttpServletRequest request) {
        // Get user from session or SecurityContext
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

        // Check if user has admin role
        if (!userService.hasRole("ROLE_ADMIN")) {
            return "redirect:/access-denied";
        }

        // Validate the input
        if (bindingResult.hasErrors()) {
            model.addAttribute("cinemas", cinemaService.getAllCinemas());
            model.addAttribute("currPage", "manage-rooms");
            return "addroom";
        }

        try {
            // Save the room
            roomService.saveRoom(room);
            model.addAttribute("successMessage", "Success! Room has been added successfully.");
            model.addAttribute("cinemas", cinemaService.getAllCinemas());
            model.addAttribute("currPage", "manage-rooms");
            return "addroom";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Failed to add room: " + e.getMessage());
            model.addAttribute("cinemas", cinemaService.getAllCinemas());
            model.addAttribute("currPage", "manage-rooms");
            return "addroom";
        }
    }

    @GetMapping("/edit-room")
    public String showEditRoomForm(@RequestParam Long id,
                                   Model model,
                                   HttpServletRequest request) {
        // Check if user has admin role
        if (!userService.hasRole("ROLE_ADMIN")) {
            return "redirect:/access-denied";
        }

        // Add user to model
        addUserToModel(model, request);

        // Get the room by ID with eager loading of necessary relations
        Optional<Room> roomOptional = roomService.getRoomByIdWithDetails(id);

        if (roomOptional.isEmpty()) {
            // Room not found, redirect with error message
            return "redirect:/manage-rooms?error=Room+not+found";
        }

        // Add room to the model
        model.addAttribute("room", roomOptional.get());

        // Add cinemas for the dropdown (optimize by fetching only necessary fields)
        model.addAttribute("cinemas", cinemaService.getCinemasBasicInfo());

        // Set current page for navigation
        model.addAttribute("currPage", "manage-rooms");

        return "editroom";
    }

    @PostMapping("/edit-room")
    public String updateRoom(@Valid @ModelAttribute("room") Room room,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes,
                             Model model,
                             HttpServletRequest request) {
        // Check if user has admin role
        if (!userService.hasRole("ROLE_ADMIN")) {
            return "redirect:/access-denied";
        }

        // Add user to model
        addUserToModel(model, request);

        // Add cinemas for the dropdown (needed if returning to the form page)
        model.addAttribute("cinemas", cinemaService.getCinemasBasicInfo());
        model.addAttribute("currPage", "manage-rooms");

        // Validate the input
        if (bindingResult.hasErrors()) {
            return "editroom";
        }

        try {
            // Update the room - use a specialized method to avoid unnecessary operations
            roomService.updateRoomDirect(room);

            // Set success attributes
            model.addAttribute("successMessage", "Success! Room has been updated successfully.");
            model.addAttribute("showSuccessOverlay", true);

            return "editroom";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Failed to update room: " + e.getMessage());
            return "editroom";
        }
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












@GetMapping("/logout")
public String logout(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
    logger.info("User logging out");

    // Xóa tất cả session attributes và hủy session hiện tại
    session.invalidate();

    // Xóa JSESSIONID cookie để tránh các vấn đề về session fixation
    Cookie cookie = new Cookie("JSESSIONID", null);
    cookie.setPath("/");
    cookie.setHttpOnly(true);
    cookie.setMaxAge(0);  // Xóa cookie ngay lập tức
    response.addCookie(cookie);

    // Chuyển hướng người dùng về trang login với thông báo logout thành công
    return "redirect:/login?logout";
}}
