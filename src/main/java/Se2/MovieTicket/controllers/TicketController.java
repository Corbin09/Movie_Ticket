package Se2.MovieTicket.controllers;

import Se2.MovieTicket.dto.TicketDTO;
import Se2.MovieTicket.impl.UserDetailsImpl;
import Se2.MovieTicket.model.Order;
import Se2.MovieTicket.model.Ticket;
import Se2.MovieTicket.model.User;
import Se2.MovieTicket.service.OrderService;
import Se2.MovieTicket.service.TicketService;
import Se2.MovieTicket.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/")
public class TicketController {
    @Autowired
    private TicketService ticketService;

    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    @GetMapping("/view-ticket/{orderId}")
    public String viewTicket(@PathVariable Long orderId, Model model, HttpServletRequest request) {
        // Add user to model (similar to the manageRooms method)
        addUserToModel(model, request);

        // Fetch the order with associated tickets, film, showtime, etc.
        Order order = orderService.findById(orderId);

        if (order == null) {
            return "redirect:/error";
        }

        // Add all needed information to the model
        model.addAttribute("order", order);
        model.addAttribute("tickets", order.getTickets());
        model.addAttribute("film", order.getShowtime().getFilm());
        model.addAttribute("cinema", order.getShowtime().getCinema());
        model.addAttribute("showtime", order.getShowtime());

        return "view-ticket"; // This will use the view-ticket.html template
    }

    /**
     * This is an alternative server-side approach for ticket download
     * You can use this if client-side download isn't suitable
     */
    @GetMapping("/ticket/download/{orderId}/{ticketIndex}")
    public void downloadTicket(
            @PathVariable Long orderId,
            @PathVariable(required = false) Integer ticketIndex,
            HttpServletResponse response,
            HttpServletRequest request,
            Model model) throws IOException {

        // Add user to model (if needed for any processing)
        // Note: For downloads, this might not be displayed but could be used for access control
        addUserToModel(model, request);

        // Default to first ticket if index not provided
        int index = (ticketIndex != null) ? ticketIndex : 0;

        // Get the order and tickets
        Order order = orderService.findById(orderId);
        if (order == null || order.getTickets() == null || order.getTickets().isEmpty()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        List<Ticket> tickets = new ArrayList<>(order.getTickets());

        // Make sure the index is valid
        if (index < 0 || index >= tickets.size()) {
            index = 0;
        }


        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=ticket-" + orderId + "-" + index + ".pdf");


        // For this demo, we'll just write a placeholder text
        PrintWriter writer = response.getWriter();
        writer.println("This is a placeholder for ticket " + index + " of order " + orderId);
        writer.close();
    }

    // Add this method to your controller
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