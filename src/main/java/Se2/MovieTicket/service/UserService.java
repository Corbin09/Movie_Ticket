package Se2.MovieTicket.service;

import Se2.MovieTicket.dto.UserDTO;
import Se2.MovieTicket.impl.UserDetailsImpl;
import Se2.MovieTicket.model.User;
import Se2.MovieTicket.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public User createUser (UserDTO userDTO) {
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setPassword(userDTO.getPassword());
        user.setUserImg(userDTO.getUserImg());
        user.setEmail(userDTO.getEmail());
        user.setPhoneNumber(userDTO.getPhoneNumber());
        user.setSex(userDTO.getSex());
        user.setDateOfBirth(userDTO.getDateOfBirth());
        user.setRole(userDTO.getRole());
        user.setResetToken(userDTO.getResetToken());
        user.setResetTokenExpire(userDTO.getResetTokenExpire());
        user.setStatus(userDTO.getStatus());
        return userRepository.save(user);
    }

    public User updateUser (Long id, UserDTO userDTO) {
        Optional<User> userData = userRepository.findById(id);
        if (userData.isPresent()) {
            User user = userData.get();
            user.setUsername(userDTO.getUsername());
            user.setPassword(userDTO.getPassword());
            user.setUserImg(userDTO.getUserImg());
            user.setEmail(userDTO.getEmail());
            user.setPhoneNumber(userDTO.getPhoneNumber());
            user.setSex(userDTO.getSex());
            user.setDateOfBirth(userDTO.getDateOfBirth());
            user.setRole(userDTO.getRole());
            user.setResetToken(userDTO.getResetToken());
            user.setResetTokenExpire(userDTO.getResetTokenExpire());
            user.setStatus(userDTO.getStatus());
            return userRepository.save(user);
        }
        return null;
    }

    public void deleteUser (Long id) {
        userRepository.deleteById(id);
    }

    public boolean hasRole(String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }

        return authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(role));
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetailsImpl) {
            Long userId = ((UserDetailsImpl) principal).getId();
            return userRepository.findById(userId).orElse(null);
        }
        return null;
    }

    public Collection<? extends GrantedAuthority> getAuthorities(Long userId) {
        Optional<User> currentUser = userRepository.findById(userId);
        if (currentUser.isPresent()) {
            return Collections.singletonList(new SimpleGrantedAuthority(currentUser.get().getRole()));
        } else {
            return Collections.emptyList(); // Or handle the case when the user is not found
        }
    }
}