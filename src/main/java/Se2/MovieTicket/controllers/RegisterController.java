package Se2.MovieTicket.controllers;

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
        // Kiểm tra mật khẩu nhập lại
        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match!");
            return "register";
        }

        // Lưu user nếu mật khẩu hợp lệ
        userService.saveUser(username, password);
        model.addAttribute("message", "User registered successfully! You can now log in.");
        System.out.println("User registered successfully!");
        return "login";
    }
}
