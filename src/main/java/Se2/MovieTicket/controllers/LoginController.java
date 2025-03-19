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

            if (passwordEncoder.matches(password, storedHash)) {
                return "index";
            } else {
                model.addAttribute("error", "Invalid username or password");
                return "login";
            }
        } else {
            model.addAttribute("error", "Invalid username or password");
            return "login";
        }
    }
}
