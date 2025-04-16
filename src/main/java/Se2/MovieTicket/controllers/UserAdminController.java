package Se2.MovieTicket.controllers;

import Se2.MovieTicket.impl.UserDetailsImpl;
import Se2.MovieTicket.model.*;
import Se2.MovieTicket.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/")
public class UserAdminController {
    @Autowired
    private UserService userService;

    //---------------------------------------------------------ADMIN----------------------------------
    @GetMapping("/manage-users")
    public String manageUsers(
            @RequestParam(required = false) String searchText,
            @RequestParam(required = false) String searchField,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int pageSize,
            Model model,
            HttpServletRequest request) {

        // Check if user has admin role

        // Add user to model
        addUserToModel(model, request);

        // Create pageable object for database pagination
        Pageable pageable = PageRequest.of(page - 1, pageSize);

        // Get users with pagination directly from database
        Page<User> usersPage;

        try {
            if (searchText != null && !searchText.isEmpty() && searchField != null && !searchField.isEmpty()) {
                // Search users by specific field with pagination
                usersPage = userService.searchUsersByFieldPaginated(searchField, searchText, pageable);
            } else {
                // Get all users with pagination
                usersPage = userService.getAllUsersPaginated(pageable);
            }

            model.addAttribute("users", usersPage.getContent());

            // Add pagination parameters
            model.addAttribute("currentPage", page);
            model.addAttribute("pageSize", pageSize);
            model.addAttribute("totalItems", usersPage.getTotalElements());
            model.addAttribute("totalPages", usersPage.getTotalPages());

        } catch (Exception e) {
            model.addAttribute("users", new ArrayList<>());
            model.addAttribute("errorMessage", "Error fetching users: " + e.getMessage());
            model.addAttribute("currentPage", 1);
            model.addAttribute("pageSize", pageSize);
            model.addAttribute("totalItems", 0);
            model.addAttribute("totalPages", 0);
        }

        // Pass the selected search options to the view
        model.addAttribute("currentSearchField", searchField);
        model.addAttribute("currentSearchText", searchText);

        // Available search fields for users
        Map<String, String> searchFields = new HashMap<>();
        searchFields.put("username", "Username");
        searchFields.put("email", "Email");
        searchFields.put("phoneNumber", "Phone Number");
        searchFields.put("role", "Role");
        model.addAttribute("searchFields", searchFields);

        // Add currPage attribute for sidebar active menu
        model.addAttribute("currPage", "manage-users");
        model.addAttribute("activeMenu", "users");

        return "manage-users";
    }

    @PostMapping("/users/update-role")
    public ResponseEntity<Map<String, Object>> updateUserRole(@RequestBody Map<String, Object> payload) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Extract user ID and new role from request
            Long userId = Long.parseLong(payload.get("userId").toString());
            String newRole = payload.get("role").toString();


            // Get current authenticated user for logging
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            if (!userService.hasRole("ADMIN")) {
                response.put("success", false);
                response.put("message", "Unauthorized");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }

            // Get the user by ID
            Optional<User> userOptional = userService.getUserById(userId);
            if (!userOptional.isPresent()) {
                response.put("success", false);
                response.put("message", "User not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            // Update user role
            User user = userOptional.get();
            userService.updateUserRole(user, newRole);


            response.put("success", true);
            response.put("message", "User role updated successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error updating user role: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    private void addUserToModel(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        User sessionUser = (session != null) ? (User) session.getAttribute("user") : null;

        if (sessionUser == null) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl) {
                UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
                sessionUser = userService.getUserById(userDetails.getId()).orElse(null);

                if (sessionUser != null && session != null) {
                    session.setAttribute("user", sessionUser);
                }
            }
        }

        if (sessionUser != null) {
            model.addAttribute("user", sessionUser);
        }
    }


    }