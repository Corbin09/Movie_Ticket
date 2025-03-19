package Se2.MovieTicket.controllers;

import Se2.MovieTicket.models.User;
import Se2.MovieTicket.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RegisterController {
    @Autowired
    private UserService userService;

    @GetMapping("/signup")
    public String showRegisterForm() {
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String username,
                               @RequestParam String password,
                               @RequestParam String confirmPassword,
                               Model model) {
        System.out.println("🔹 Register Request: " + username);

        if (!password.equals(confirmPassword)) {
            System.out.println("❌ Passwords do not match!");
            model.addAttribute("error", "Passwords do not match!");
            return "register";
        }

        try {
            User savedUser = userService.saveUser(username, password);
            System.out.println("✅ User saved: " + savedUser.getUsername());

            model.addAttribute("message", "User registered successfully! You can now log in.");
            return "login";
        } catch (Exception e) {
            System.out.println("❌ Error saving user: " + e.getMessage());
            model.addAttribute("error", "An error occurred while saving the user. Please try again.");
            return "register";
        }
    }
}
