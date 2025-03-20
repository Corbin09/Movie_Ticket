package Se2.MovieTicket.services;

import Se2.MovieTicket.models.User;
import Se2.MovieTicket.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User saveUser(String username, String password) {
        User user = new User();
        user.setUsername(username);

        String encodedPassword = passwordEncoder.encode(password);
        user.setPassword(encodedPassword);

        System.out.println("🔹 Saving User: " + username);
        System.out.println("🔹 Encoded Password: " + encodedPassword); // Debugging line

        return userRepository.save(user);
    }
}
