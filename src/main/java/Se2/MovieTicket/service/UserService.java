package Se2.MovieTicket.service;

import Se2.MovieTicket.dto.UserDTO;
import Se2.MovieTicket.impl.UserDetailsImpl;
import Se2.MovieTicket.model.Film;
import Se2.MovieTicket.model.User;
import Se2.MovieTicket.model.UserLikeFilm;
import Se2.MovieTicket.repository.UserLikeFilmRepository;
import Se2.MovieTicket.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
@Autowired
private UserLikeFilmRepository userLikeFilmRepository;

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


    public void deleteUser (Long id) {
        userRepository.deleteById(id);
    }

//    public boolean hasRole(String role) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication == null) {
//            return false;
//        }
//
//        return authentication.getAuthorities().stream()
//                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(role));
//    }


    public Collection<? extends GrantedAuthority> getAuthorities(Long userId) {
        Optional<User> currentUser = userRepository.findById(userId);
        if (currentUser.isPresent()) {
            return Collections.singletonList(new SimpleGrantedAuthority(currentUser.get().getRole()));
        } else {
            return Collections.emptyList(); // Or handle the case when the user is not found
        }
    }


    // Phương thức truy vấn hiệu quả cho việc lấy thông tin người dùng hiện tại
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() ||
                authentication.getPrincipal() == null) {
            return null;
        }

        Object principal = authentication.getPrincipal();
        String username = null;

        if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
            username = ((org.springframework.security.core.userdetails.UserDetails) principal).getUsername();
        } else if (principal instanceof String) {
            username = (String) principal;
        }

        if (username != null) {
            // Tối ưu truy vấn bằng cách chỉ lấy thông tin cần thiết
            return userRepository.findByUsername(username).orElse(null);
        }

        return null;
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // Cập nhật thông tin người dùng với hiệu suất tối ưu
    @Transactional
    public User updateUser(Long id, UserDTO userDTO) {
        return userRepository.findById(id)
                .map(user -> {
                    // Chỉ cập nhật các trường được cung cấp
                    if (userDTO.getUsername() != null) {
                        user.setUsername(userDTO.getUsername());
                    }

                    if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
                        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
                    }

                    if (userDTO.getUserImg() != null) {
                        user.setUserImg(userDTO.getUserImg());
                    }

                    if (userDTO.getEmail() != null) {
                        user.setEmail(userDTO.getEmail());
                    }

                    // Các trường có thể null
                    user.setPhoneNumber(userDTO.getPhoneNumber());
                    user.setSex(userDTO.getSex());
                    user.setDateOfBirth(userDTO.getDateOfBirth());

                    if (userDTO.getRole() != null) {
                        user.setRole(userDTO.getRole());
                    }

                    if (userDTO.getStatus() != null) {
                        user.setStatus(userDTO.getStatus());
                    }

                    return userRepository.save(user);
                })
                .orElse(null);
    }

    // Phương thức mới - chỉ cập nhật hình ảnh
    @Transactional
    public void updateUserImage(Long userId, String imageUrl) {
        userRepository.findById(userId).ifPresent(user -> {
            user.setUserImg(imageUrl);
            userRepository.save(user);
        });
    }

    public void unlikeFilm(User user, Film film) {
        userLikeFilmRepository.findByUserAndFilm(user, film)
                .ifPresent(userLikeFilmRepository::delete);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    public Long getTotalUserCount() {
        return userRepository.count();
    }

    public Page<User> searchUsersByFieldPaginated(String searchField, String searchText, Pageable pageable) {
        // If no search text is provided, return all users paginated
        if (searchText == null || searchText.trim().isEmpty()) {
            return userRepository.findAll(pageable);
        }

        // Use the appropriate repository method based on the search field
        switch (searchField) {
            case "username":
                return userRepository.findByUsernameContainingIgnoreCase(searchText, pageable);
            case "email":
                return userRepository.findByEmailContainingIgnoreCase(searchText, pageable);
            case "phoneNumber":
                return userRepository.findByPhoneNumberContaining(searchText, pageable);
            case "role":
                return userRepository.findByRoleIgnoreCase(searchText, pageable);
            default:
                // Default to username search if field is not recognized
                return userRepository.findByUsernameContainingIgnoreCase(searchText, pageable);
        }
    }

    public Page<User> getAllUsersPaginated(Pageable pageable) {
        // This method simply delegates to the repository's findAll with pagination
        return userRepository.findAll(pageable);
    }

    public void saveUser(User user) {
        // First check if this is updating an existing user
        if (user.getUserId() != null) {
            // Check if password needs to be encoded (if it doesn't look like it's already encoded)
            if (user.getPassword() != null && !user.getPassword().startsWith("$2a$")) {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
            }
        } else {
            // For new users, always encode the password
            if (user.getPassword() != null) {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
            }

            // Set default values for new users if not provided
            if (user.getRole() == null) {
                user.setRole("USER");
            }
            if (user.getStatus() == null) {
                user.setStatus("Active");
            }
            if (user.getUserImg() == null) {
                user.setUserImg("/static/images/anonymous.jpg");
            }
        }

        // Save or update the user
        userRepository.save(user);
    }

    @Transactional
    public void updateUserRole(User user, String newRole) {
        if (user == null || newRole == null || newRole.trim().isEmpty()) {
            throw new IllegalArgumentException("User and role must not be null or empty");
        }

        // Check if the user exists in the database
        User existingUser = userRepository.findById(user.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + user.getUserId()));

        // Normalize role format to remove ROLE_ prefix if present
        String normalizedRole = newRole.startsWith("ROLE_") ? newRole.substring(5) : newRole;


        // Update the role
        existingUser.setRole(normalizedRole);

        // Save the updated user
        userRepository.save(existingUser);

        // If the user is currently authenticated, update their authorities in the security context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getName().equals(existingUser.getUsername())) {
            // Create updated authentication with new role
            UserDetailsImpl userDetails = new UserDetailsImpl(existingUser);
            Authentication newAuth = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                    userDetails, authentication.getCredentials(), userDetails.getAuthorities());

            // Update the security context
            SecurityContextHolder.getContext().setAuthentication(newAuth);
        }
    }

    // Check if current user has a specific role
    public boolean hasRole(String roleName) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }

        // Remove ROLE_ prefix if present in the parameter
        String normalizedRole = roleName.startsWith("ROLE_") ? roleName.substring(5) : roleName;

        return authentication.getAuthorities().stream()
                .map(authority -> {
                    // Remove ROLE_ prefix from authorities if present
                    String auth = authority.getAuthority();
                    return auth.startsWith("ROLE_") ? auth.substring(5) : auth;
                })
                .anyMatch(authority -> authority.equals(normalizedRole));
    }
}