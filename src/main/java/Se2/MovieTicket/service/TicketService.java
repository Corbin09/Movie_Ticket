package Se2.MovieTicket.service;

import Se2.MovieTicket.dto.TicketDTO;
import Se2.MovieTicket.model.Ticket;
import Se2.MovieTicket.model.User;
import Se2.MovieTicket.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TicketService {
    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private OrderService orderService;




    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    public Optional<Ticket> getTicketById(Long id) {
        return ticketRepository.findById(id);
    }

    public Ticket createTicket(TicketDTO ticketDTO) {
        Ticket ticket = new Ticket();
        ticket.setOrderId(ticketDTO.getOrderId());
        ticket.setSeatId(ticketDTO.getSeatId());
//        ticket.setShowtimeId(ticketDTO.getShowtimeId());
        ticket.setTicketPrice(ticketDTO.getTicketPrice());
        return ticketRepository.save(ticket);
    }

    public Ticket updateTicket(Long id, TicketDTO ticketDTO) {
        Optional<Ticket> ticketData = ticketRepository.findById(id);
        if (ticketData.isPresent()) {
            Ticket ticket = ticketData.get();
            ticket.setOrderId(ticketDTO.getOrderId());
            ticket.setSeatId(ticketDTO.getSeatId());
//            ticket.setShowtimeId(ticketDTO.getShowtimeId());
            ticket.setTicketPrice(ticketDTO.getTicketPrice());
            return ticketRepository.save(ticket);
        }
        return null;
    }
    /**
     * Get all tickets purchased by a specific user
     *
     * @param userId the ID of the user
     * @return list of tickets associated with the user
     */
    public List<Ticket> getTicketsByUser(Long userId) {
        // Get all orders for the user
        return orderService.getOrdersByUserId(userId).stream()
                .flatMap(order -> order.getTickets().stream())
                .collect(Collectors.toList());
    }
    // In TicketService class
    public List<Ticket> getTicketsByUserDirectly(Long userId) {
        // Direct query for tickets based on user ID
        return ticketRepository.findTicketsByUserId(userId);
    }
    /**
     * Get all tickets purchased by a specific user
     *
     * @param user the user entity
     * @return list of tickets associated with the user
     */
    public List<Ticket> getTicketsByUser(User user) {
        return getTicketsByUser(user.getUserId());
    }
    public void deleteTicket(Long id) {
        ticketRepository.deleteById(id);
    }
}