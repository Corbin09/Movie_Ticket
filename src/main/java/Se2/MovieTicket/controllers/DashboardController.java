package Se2.MovieTicket.controllers;

import Se2.MovieTicket.dto.FilmRatingDTO;
import Se2.MovieTicket.model.Film;
import Se2.MovieTicket.service.DashboardService;
import Se2.MovieTicket.service.FilmService;
import Se2.MovieTicket.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import Se2.MovieTicket.model.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import Se2.MovieTicket.impl.UserDetailsImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.*;
@Controller
public class DashboardController {
    private final Logger logger = LoggerFactory.getLogger(DashboardController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private FilmService filmService;

    @GetMapping("/admin-dashboard")
    public String showDashboard(
            @RequestParam(required = false) String range,
            Model model,
            HttpServletRequest request) {

        // User session handling
        HttpSession session = request.getSession(false);
        User sessionUser = null;
        if (session != null) {
            sessionUser = (User) session.getAttribute("user");
            if (sessionUser != null) {
                logger.info("User found in session: {}", sessionUser.getUsername());
                model.addAttribute("user", sessionUser);
            }
        }

        if (sessionUser == null) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl) {
                UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
                Optional<User> userOptional = userService.getUserById(userDetails.getId());

                if (userOptional.isPresent()) {
                    User user = userOptional.get();
                    model.addAttribute("user", user);

                    // Save to session for future requests
                    if (session != null) {
                        session.setAttribute("user", user);
                        logger.info("User saved to session from SecurityContext");
                    }
                }
            }
        }

        LocalDate endDate = LocalDate.now();
        LocalDate startDate;

        if (range == null || range.isEmpty()) {
            range = "week";
        }

        switch (range) {
            case "week": startDate = endDate.minusWeeks(1); break;
            case "month": startDate = endDate.minusMonths(1); break;
            case "quarter": startDate = endDate.minusMonths(3); break;
            case "all": startDate = LocalDate.of(2000, 1, 1); break;
            default: startDate = endDate.minusWeeks(1);
        }

        List<FilmRatingDTO> avgRatings = dashboardService.getAverageRatings();

        // Create a map of film names by ID for the template
        Map<Long, String> filmNames = new HashMap<>();
        for (FilmRatingDTO rating : avgRatings) {
            Film film = filmService.findById(rating.getFilmId());
            if (film != null) {
                filmNames.put(rating.getFilmId(), film.getFilmName());
            }
        }

        var summary = dashboardService.getDashboardSummary(startDate, endDate);

        model.addAttribute("totalUsers", userService.getTotalUserCount());

        // Add all required attributes
        model.addAttribute("filmNames", filmNames);
        model.addAttribute("avgRatings", avgRatings);
        model.addAttribute("genderStats", dashboardService.getUserGenderStats());
        model.addAttribute("currPage", "admin-dashboard");
        model.addAttribute("range", range);

        // Add all required attributes with date filtering
        model.addAttribute("chartData", dashboardService.getRevenueChartData(startDate, endDate));
        model.addAttribute("topUsers", dashboardService.getTopUserSpending(5, startDate, endDate));
        model.addAttribute("summary", dashboardService.getDashboardSummary(startDate, endDate));
        model.addAttribute("filmDetails", dashboardService.getFilmRevenueDetails(startDate, endDate));

        return "dashboard";
    }
}