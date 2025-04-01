package Se2.MovieTicket.controllers;

import Se2.MovieTicket.dto.UserDTO;
import Se2.MovieTicket.model.User;
import Se2.MovieTicket.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/profile")
    public String viewProfile(Model model) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", currentUser);
        model.addAttribute("currPage", "profile");
        return "user/profile";
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
                response.put("user", updatedUser);
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
    }}