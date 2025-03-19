package Se2.MovieTicket.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import Se2.MovieTicket.dto.LoginRequest;
import Se2.MovieTicket.dto.UserDTO;
import Se2.MovieTicket.impl.UserDetailsImpl;
import Se2.MovieTicket.model.User;
import Se2.MovieTicket.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
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
    public String login(LoginRequest loginRequest, Model model) {
        try {
            logger.info("Attempting to log in user: {}", loginRequest.getUsername());

            // Authenticate the user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Get user details from the authentication object
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            Long userId = userDetails.getId();

            // Get user from database
            Optional<User> userOptional = userService.getUserById(userId);

            if (userOptional.isEmpty()) {
                logger.warn("User not found during login for username: {}", loginRequest.getUsername());
                model.addAttribute("error", "User not found");
                return "login";
            }

            User user = userOptional.get();
            String role = user.getRole();
            logger.info("User {} logged in successfully with role: {}", loginRequest.getUsername(), role);

            // Redirect based on user role
            switch (role) {
                case "ROLE_ADMIN":
                    logger.info("Redirecting Admin to /pay-ticket");
                    return "redirect:/pay-ticket";
                case "ROLE_USER":
                    logger.info("Redirecting User to /View-movie-ticket");
                    return "redirect:/View-movie-ticket";
                default:
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
    public String payTicket() {
        logger.info("Accessing pay ticket page");
        // Check if user has Admin role
        if (!userService.hasRole("ROLE_ADMIN")) {
            logger.warn("Unauthorized access attempt to pay-ticket page");
            return "redirect:/access-denied";
        }
        return "pay-ticket";
    }

    @GetMapping("/View-movie-ticket")
    public String viewMovieTicket() {
        logger.info("Accessing view movie ticket page");
        // Check if user has User role
        if (!userService.hasRole("ROLE_USER")) {
            logger.warn("Unauthorized access attempt to view-movie-ticket page");
            return "redirect:/access-denied";
        }
        return "View-movie-ticket";
    }

    @GetMapping("/index")
    public String index() {
        logger.info("Accessing index page");
        return "index";
    }
}