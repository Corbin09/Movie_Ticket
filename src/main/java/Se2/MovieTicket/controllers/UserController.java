package Se2.MovieTicket.controllers;

import Se2.MovieTicket.dto.UserDTO;
import Se2.MovieTicket.model.*;
import Se2.MovieTicket.repository.FilmRepository;
import Se2.MovieTicket.repository.UserRepository;
import Se2.MovieTicket.service.FilmService;
import Se2.MovieTicket.service.NewsService;
import Se2.MovieTicket.service.TicketService;
import Se2.MovieTicket.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpSession;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import org.springframework.transaction.annotation.Transactional;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private NewsService newsService;

    @Autowired
    private TicketService ticketService;
@Autowired
private FilmService filmService;


@Autowired
private FilmRepository filmRepository;

@Autowired
private UserRepository userRepository;
    @GetMapping("/detail-profile")
    public String viewProfile(HttpSession session, Model model) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return "redirect:/login";
        }

        // Save user to session
        session.setAttribute("loggedInUser", currentUser);

        // Get user posts
        List<News> userPosts = newsService.findNewsByUser(currentUser);

        // Get tickets directly with a single query
        List<Ticket> userTickets = ticketService.getTicketsByUserDirectly(currentUser.getUserId());

        // Get all films
        List<Film> allFilms = filmService.getAllFilms();

        // IMPORTANT: Get liked films directly from database instead of from user object
        List<Film> likedFilms = filmService.getLikedFilmsByUserId(currentUser.getUserId());

        // Add everything to model
        model.addAttribute("userPosts", userPosts);
        model.addAttribute("userTickets", userTickets);
        model.addAttribute("allFilms", allFilms);
        model.addAttribute("likedFilms", likedFilms);
        model.addAttribute("news", new News());
        model.addAttribute("user", currentUser);
        model.addAttribute("currPage", "profile");

        return "detail-profile";
    }

    @PostMapping("/update-profile")
    @ResponseBody
    public ResponseEntity<?> updateProfile(
            @RequestParam("username") String username,
            @RequestParam("email") String email,
            @RequestParam(value = "phoneNumber", required = false) String phoneNumber,
            @RequestParam(value = "sex", required = false) String sex,
            @RequestParam(value = "dateOfBirth", required = false) String dateOfBirth,
            @RequestParam(value = "userImg", required = false) MultipartFile userImg,
            @RequestParam(value = "_csrf", required = false) String csrf) {

        try {
            System.out.println("Received update request:");
            System.out.println("Username: " + username);
            System.out.println("Email: " + email);
            System.out.println("Phone: " + phoneNumber);
            System.out.println("Sex: " + sex);
            System.out.println("DOB: " + dateOfBirth);
            System.out.println("Has image: " + (userImg != null && !userImg.isEmpty()));

            User currentUser = userService.getCurrentUser();
            if (currentUser == null) {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "User not found"));
            }

            // Validate input fields
            Map<String, String> errors = new HashMap<>();

            // Validate username
            if (username == null || username.trim().length() < 3) {
                errors.put("username", "Username must be at least 3 characters");
            } else if (!username.equals(currentUser.getUsername())) {
                // Check if username already exists
                if (userService.getUserByUsername(username).isPresent()) {
                    errors.put("username", "Username already taken");
                }
            }

            // Validate email
            if (email == null || !email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
                errors.put("email", "Please enter a valid email");
            }

            // Validate phone
            if (phoneNumber != null && !phoneNumber.trim().isEmpty() && !phoneNumber.matches("^\\d{10,}$")) {
                errors.put("phoneNumber", "Please enter a valid phone number (at least 10 digits)");
            }

            // Return validation errors if any
            if (!errors.isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("errors", errors);
                return ResponseEntity.badRequest().body(response);
            }

            // Process image if uploaded
            String imageUrl = currentUser.getUserImg();

            if (userImg != null && !userImg.isEmpty()) {
                try {
                    // Use a path relative to the application's working directory
                    String uploadDir = System.getProperty("user.dir") + "/src/main/resources/static/images/users";
                    String fileName = UUID.randomUUID() + "_" + userImg.getOriginalFilename();
                    Path uploadPath = Paths.get(uploadDir);

                    if (!Files.exists(uploadPath)) {
                        Files.createDirectories(uploadPath);
                    }

                    // Check if directory is writable
                    if (!Files.isWritable(uploadPath)) {
                        Map<String, Object> response = new HashMap<>();
                        response.put("success", false);
                        response.put("message", "Upload directory is not writable");
                        return ResponseEntity.badRequest().body(response);
                    }

                    Files.copy(userImg.getInputStream(), uploadPath.resolve(fileName));
                    imageUrl = "/images/users/" + fileName;
                } catch (IOException e) {
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", false);
                    response.put("message", "Failed to upload image: " + e.getMessage());
                    return ResponseEntity.badRequest().body(response);
                }
            }

            // Convert dateOfBirth from String to Date if needed
            Date parsedDate = null;
            if (dateOfBirth != null && !dateOfBirth.isEmpty()) {
                try {
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    parsedDate = format.parse(dateOfBirth);
                } catch (ParseException e) {
                    return ResponseEntity.badRequest().body(Map.of(
                            "success", false,
                            "message", "Invalid date format"
                    ));
                }
            }

            // Update user information
            UserDTO userDTO = new UserDTO();
            userDTO.setUsername(username);
            userDTO.setEmail(email);
            userDTO.setPhoneNumber(phoneNumber);
            userDTO.setSex(sex);
            userDTO.setDateOfBirth(parsedDate); // Using the parsed Date object
            userDTO.setUserImg(imageUrl);
            userDTO.setRole(currentUser.getRole());
            userDTO.setStatus(currentUser.getStatus());

            User updatedUser = userService.updateUser(currentUser.getUserId(), userDTO);

            if (updatedUser != null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);

                // Chỉ trả về thông tin cần thiết của user
                Map<String, Object> userResponse = new HashMap<>();
                userResponse.put("userId", updatedUser.getUserId());
                userResponse.put("username", updatedUser.getUsername());
                userResponse.put("email", updatedUser.getEmail());
                userResponse.put("userImg", updatedUser.getUserImg());
                userResponse.put("phoneNumber", updatedUser.getPhoneNumber());
                userResponse.put("sex", updatedUser.getSex());
                userResponse.put("dateOfBirth", updatedUser.getDateOfBirth());

                response.put("user", userResponse);
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Failed to update profile"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Server error: " + e.getMessage()
            ));
        }
    }


    @PostMapping("/upload-news")
    public String uploadNews(@RequestParam("newsHeader") String newsHeader,
                             @RequestParam("newsContent") String newsContent,
                             @RequestParam("newsFooter") String newsFooter,
                             @RequestParam("film.filmId") Long filmId,
                             @RequestParam("user.userId") Long userId,
                             @RequestParam(value = "newsImgFile", required = false) MultipartFile newsImgFile,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {

        User currentUser  = userService.getCurrentUser ();
        if (currentUser  == null) {
            return "redirect:/login";
        }

        // Create a complete News object
        News news = new News();
        news.setNewsHeader(newsHeader);
        news.setNewsContent(newsContent);
        news.setNewsFooter(newsFooter);

        // Set the current time using LocalDateTime
        news.setNewsTime(LocalDateTime.now()); // Change this line

        // IMPORTANT: Load actual Film entity from repository
        Film film = filmRepository.findById(filmId)
                .orElseThrow(() -> new EntityNotFoundException("Film not found with ID: " + filmId));
        news.setFilm(film);
        // Set user properly - use currentUser  directly
        news.setUser (currentUser );

        // Handle image upload if exists
        if (newsImgFile != null && !newsImgFile.isEmpty()) {
            try {
                // Save file and get path
                String fileName = StringUtils.cleanPath(newsImgFile.getOriginalFilename());
                String uploadDir = "src/main/resources/static/images/news/";
                String uniqueFileName = System.currentTimeMillis() + "_" + fileName;
                Path uploadPath = Paths.get(uploadDir);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                Path filePath = uploadPath.resolve(uniqueFileName);
                Files.copy(newsImgFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                // Set news image path
                news.setNewsImg("/images/news/" + uniqueFileName);
            } catch (IOException e) {
                redirectAttributes.addFlashAttribute("error", "Failed to upload image: " + e.getMessage());
                return "redirect:/user/detail-profile";
            }
        }

        // Save news with all attributes
        try {
            newsService.saveNews(news);
            // Add success message to be displayed
            redirectAttributes.addFlashAttribute("success", "Your post has been created successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to create post: " + e.getMessage());
            e.printStackTrace(); // Add this to see the full error in logs
        }

        return "redirect:/user/detail-profile";
    }


    @DeleteMapping("/delete-news")
    @ResponseBody
    public ResponseEntity<?> deleteNews(@RequestBody Map<String, List<Long>> requestBody) {
        List<Long> ids = requestBody.get("ids");
        System.out.println("Received IDs: " + ids); // Debug log

        if (ids == null || ids.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No ids provided");
        }

        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in");
        }

        for (Long newsId : ids) {
            News news = newsService.getNewsById(newsId);
            if (news != null && news.getUser().getUserId().equals(currentUser.getUserId())) {
                newsService.deleteNews(newsId);
            }
        }

        return ResponseEntity.ok("Deleted successfully");
    }

    // Fixed DeleteMapping for unliking films
    @DeleteMapping("/unlike-film")
    @ResponseBody
    public ResponseEntity<?> unlikeFilm(@RequestBody Map<String, List<Long>> requestBody) {
        List<Long> ids = requestBody.get("ids");
        if (ids == null || ids.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No ids provided");
        }

        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in");
        }

        for (Long filmId : ids) {
            Optional<Film> filmOptional = filmService.getFilmById(filmId);
            filmOptional.ifPresent(film -> userService.unlikeFilm(currentUser, film));
        }

        return ResponseEntity.ok("Unliked successfully");
    }

    // Helper method to save uploaded images
    private String saveImage(MultipartFile file) throws IOException {
        // Define the directory where images will be stored
        String uploadDir = "src/main/resources/static/images/news/";

        // Create the directory if it doesn't exist
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // Generate a unique filename
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

        // Save the file
        Path filePath = Paths.get(uploadDir + fileName);
        Files.write(filePath, file.getBytes());

        // Return the path that will be stored in the database
        return "/images/news/" + fileName;
    }









}