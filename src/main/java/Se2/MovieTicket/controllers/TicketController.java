package Se2.MovieTicket.controllers;

import Se2.MovieTicket.dto.TicketDTO;
import Se2.MovieTicket.model.Ticket;
import Se2.MovieTicket.service.TicketService;
import Se2.MovieTicket.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {
    @Autowired
    private TicketService ticketService;

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<Ticket>> getAllTickets() {
        if (!userService.hasRole("Admin") && !userService.hasRole("User  ")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        List<Ticket> tickets = ticketService.getAllTickets();
        return tickets.isEmpty() ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(tickets, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ticket> getTicketById(@PathVariable("id") Long id) {
        if (!userService.hasRole("Admin") && !userService.hasRole("User  ")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return ticketService.getTicketById(id)
                .map(ticket -> new ResponseEntity<>(ticket, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<Ticket> createTicket(@RequestBody TicketDTO ticketDTO) {
        if (!userService.hasRole("Admin")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        Ticket newTicket = ticketService.createTicket(ticketDTO);
        return new ResponseEntity<>(newTicket, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Ticket> updateTicket(@PathVariable("id") Long id, @RequestBody TicketDTO ticketDTO) {
        if (!userService.hasRole("Admin")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        Ticket updatedTicket = ticketService.updateTicket(id, ticketDTO);
        return updatedTicket != null ? new ResponseEntity<>(updatedTicket, HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteTicket(@PathVariable("id") Long id) {
        if (!userService.hasRole("Admin")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        ticketService.deleteTicket(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}