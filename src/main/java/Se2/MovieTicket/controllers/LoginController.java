package Se2.MovieTicket.controllers;

import Se2.MovieTicket.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import Se2.MovieTicket.models.User;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@Controller
public class LoginController {
    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/process-login") // Trùng với loginProcessingUrl ở trên
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        Model model) {

        Optional<User> user = userService.findByUsername(username);
        if (user.isPresent()) {
            String storedHash = user.get().getPassword();
            System.out.println("🔹 Stored Hash (DB): "  + " " + username + " " + password);

            if (passwordEncoder.matches(password, storedHash)) {
                System.out.println("✅ Login Successful!");
                return "index";
            } else {
                System.out.println("❌ Login Failed: Incorrect Password");
                model.addAttribute("error", "Invalid username or password");
                return "login";
            }
        } else {
            System.out.println("❌ Login Failed: Username Not Found");
            model.addAttribute("error", "Invalid username or password");
            return "login";
        }
    }
}
