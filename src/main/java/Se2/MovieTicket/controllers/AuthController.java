package Se2.MovieTicket.controllers;

import Se2.MovieTicket.model.Film;
import Se2.MovieTicket.service.FilmService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import Se2.MovieTicket.dto.LoginRequest;
import Se2.MovieTicket.dto.UserDTO;
import Se2.MovieTicket.impl.UserDetailsImpl;
import Se2.MovieTicket.model.User;
import Se2.MovieTicket.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private FilmService filmService;

    @Autowired
    private SecurityContextRepository securityContextRepository;

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
                return "redirect:/pay-ticket";
            } else if ("ROLE_USER".equals(role)) {
                logger.info("Redirecting User to /home");
                return "redirect:/home";
            } else {
                logger.info("Redirecting to default index page");
                return "redirect:/index";
            }
        } catch (Exception e) {
            logger.error("Login failed: {}", e.getMessage());
            model.addAttribute("error", "Invalid credentials");
            return "login";
        }
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        logger.info("Accessing registration page");
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String register(User user, Model model) {
        try {
            logger.info("Attempting to register user: {}", user.getUsername());

            // Check if username already exists by checking all users
            List<User> allUsers = userService.getAllUsers();
            boolean usernameExists = allUsers.stream()
                    .anyMatch(existingUser -> existingUser.getUsername().equals(user.getUsername()));

            if (usernameExists) {
                logger.warn("Username already exists: {}", user.getUsername());
                model.addAttribute("error", "Username already exists");
                return "register";
            }

            // Check if email already exists
            boolean emailExists = user.getEmail() != null && allUsers.stream()
                    .anyMatch(existingUser -> user.getEmail().equals(existingUser.getEmail()));

            if (emailExists) {
                logger.warn("Email already exists: {}", user.getEmail());
                model.addAttribute("error", "Email already exists");
                return "register";
            }

            // Convert User to UserDTO
            UserDTO userDTO = new UserDTO();
            userDTO.setUsername(user.getUsername());
            userDTO.setPassword(user.getPassword());
            userDTO.setEmail(user.getEmail());
            userDTO.setPhoneNumber(user.getPhoneNumber());
            userDTO.setSex(user.getSex());
            userDTO.setDateOfBirth(user.getDateOfBirth());
            userDTO.setRole("User"); // Set default role for new users
            userDTO.setStatus("Active"); // Set default status

            // Save user to database
            userService.createUser(userDTO);

            logger.info("User registered successfully: {}", user.getUsername());
            return "redirect:/login?registered";
        } catch (Exception e) {
            logger.error("Registration failed: {}", e.getMessage());
            model.addAttribute("error", "Registration failed: " + e.getMessage());
            return "register";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        logger.info("Processing logout request");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
            logger.info("User logged out successfully");
        }

        return "redirect:/login?logout";
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
    public String viewMovieTicket(Model model, HttpServletRequest request) {
        logger.info("Accessing view movie ticket page");

        if (!userService.hasRole("ROLE_USER")) {
            logger.warn("Unauthorized access attempt to view-movie-ticket page");
            return "redirect:/access-denied";
        }

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

        List<Film> films = filmService.getAllFilms();
        logger.info("Number of films retrieved: {}", films.size());
        model.addAttribute("films", films);

        return "View-movie-ticket";
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

//    @GetMapping("/home")
//    public String homePage(Model model, HttpServletRequest request) {
//        logger.info("Accessing home page");
//
//        // First try to get user from session
//        HttpSession session = request.getSession(false);
//        User sessionUser = null;
//        if (session != null) {
//            sessionUser = (User) session.getAttribute("user");
//            if (sessionUser != null) {
//                logger.info("User found in session: {}", sessionUser.getUsername());
//                model.addAttribute("user", sessionUser);
//            }
//        }
//
//        // If not in session, try from SecurityContext
//        if (sessionUser == null) {
//            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//            if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl) {
//                UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
//                Optional<User> userOptional = userService.getUserById(userDetails.getId());
//
//                if (userOptional.isPresent()) {
//                    User user = userOptional.get();
//                    model.addAttribute("user", user);
//
//                    // Save to session for future requests
//                    if (session != null) {
//                        session.setAttribute("user", user);
//                        logger.info("User saved to session from SecurityContext");
//                    }
//                }
//            }
//        }
//
//        // Get list of films from the filmService
//        List<Film> films = filmService.getAllFilms();
//        logger.info("Number of films retrieved: {}", films.size());
//
//        // Add the list of films to the model
//        model.addAttribute("films", films);
//
//        return "home";  // Return to home page
//    }
@GetMapping("/home")
public String home(
        @RequestParam(defaultValue = "1") int currentPageNowShowing,
        @RequestParam(defaultValue = "1") int currentPageComingSoon,
        Model model, HttpServletRequest request) {

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

    // Fetch paginated films for Now Showing and Coming Soon
    int size = 8; // Số lượng phim hiển thị trên mỗi trang
    Page<Film> nowShowingPage = filmService.getNowShowingFilms(currentPageNowShowing, size);
    Page<Film> comingSoonPage = filmService.getComingSoonFilms(currentPageComingSoon, size);

    model.addAttribute("nowShowingMovies", nowShowingPage.getContent());
    model.addAttribute("comingSoonMovies", comingSoonPage.getContent());

    model.addAttribute("currentPageNowShowing", currentPageNowShowing);
    model.addAttribute("totalPagesNowShowing", nowShowingPage.getTotalPages());

    model.addAttribute("currentPageComingSoon", currentPageComingSoon);
    model.addAttribute("totalPagesComingSoon", comingSoonPage.getTotalPages());

    // Fetch all films (previously used list)
    List<Film> films = filmService.getAllFilms();
    logger.info("Number of films retrieved: {}", films.size());
    model.addAttribute("films", films);

    return "home";
}
}