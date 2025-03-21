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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
//    @Autowired
//    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EntityManager em;

    public List<User> filterUsers(String username) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<User> cq = cb.createQuery(User.class);
        Root<User> user = cq.from(User.class);

        List<Predicate> predicates = new ArrayList<>();
        if (username != null && !username.isEmpty()) {
            predicates.add(cb.like(user.get("username"), "%" + username + "%"));
        }

        cq.where(predicates.toArray(new Predicate[0]));
        return em.createQuery(cq).getResultList();
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }


    public User createUser(UserDTO userDTO) {
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setPassword(userDTO.getPassword()); // Bỏ mã hóa vì đã được mã hóa trước đó
        user.setEmail(userDTO.getEmail());
        user.setPhoneNumber(userDTO.getPhoneNumber());
        user.setSex(userDTO.getSex());
        user.setDateOfBirth(userDTO.getDateOfBirth());
        user.setRole(userDTO.getRole() != null ? userDTO.getRole() : "USER");
        user.setStatus(userDTO.getStatus() != null ? userDTO.getStatus() : "Active");
        user.setUserImg("/static/images/anonymous.jpg");

        System.out.println("Saving user: " + user.toString());

        User savedUser = userRepository.save(user);
        System.out.println("Saved user ID: " + savedUser.getUserId());

        return savedUser;
    }




    public User updateUser (Long id, UserDTO userDTO) {
        Optional<User> userData = userRepository.findById(id);
        if (userData.isPresent()) {
            User user = userData.get();
            user.setUsername(userDTO.getUsername());
            if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
                user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
            }
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




    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal() == null) {
            return null;
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetailsImpl) {
            Long userId = ((UserDetailsImpl) principal).getId();
            return userRepository.findById(userId).orElse(null);
        } else if (principal instanceof org.springframework.security.core.userdetails.User) {
            String username = ((org.springframework.security.core.userdetails.User) principal).getUsername();
            return userRepository.findByUsername(username).orElse(null);
        } else if (principal instanceof String) {
            return userRepository.findByUsername((String) principal).orElse(null);
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

    public Optional<User> getUserByUsername(String username) {
        // Assuming you have a userRepository field in your UserService class
        return userRepository.findByUsername(username);
    }


}