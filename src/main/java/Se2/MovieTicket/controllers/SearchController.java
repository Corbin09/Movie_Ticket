package Se2.MovieTicket.controllers;

import Se2.MovieTicket.impl.UserDetailsImpl;
import Se2.MovieTicket.model.Film;
import Se2.MovieTicket.model.User;
import Se2.MovieTicket.service.FilmService;
import Se2.MovieTicket.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class SearchController {

    @Autowired
    private FilmService filmService;

    @Autowired
    private UserService userService;

    @GetMapping("/user-search")
    public String searchFilms(
            @RequestParam("query") String query,
            @RequestParam(value = "page", defaultValue = "1") int page,
            Model model, HttpServletRequest request) {

        // Try to get user from session first
        HttpSession session = request.getSession(false);
        User sessionUser = null;
        if (session != null) {
            sessionUser = (User) session.getAttribute("user");
            if (sessionUser != null) {
                model.addAttribute("user", sessionUser);
            }
        }

        // If user not found in session, check SecurityContext
        if (sessionUser == null) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl) {
                UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
                Optional<User> userOptional = userService.getUserById(userDetails.getId());

                if (userOptional.isPresent()) {
                    User user = userOptional.get();
                    model.addAttribute("user", user);

                    // Save user to session for later use
                    if (session != null) {
                        session.setAttribute("user", user);
                    }
                }
            }
        }

        model.addAttribute("currentPage", "home"); // Keep the home menu item active

        // Set up pagination for search results
        int pageSize = 8; // Number of films per page
        Pageable pageable = PageRequest.of(page - 1, pageSize);

        // Perform the search query
        Page<Film> searchResultsPage = filmService.searchFilms(query, pageable);

        // Add search attributes to model
        model.addAttribute("searchPerformed", true);
        model.addAttribute("searchQuery", query);
        model.addAttribute("searchResults", searchResultsPage.getContent());
        model.addAttribute("searchResultsCount", searchResultsPage.getTotalElements());
        model.addAttribute("currentPageSearch", page);
        model.addAttribute("totalPagesSearch", searchResultsPage.getTotalPages());

        return "home"; // Reuse the home template
    }}