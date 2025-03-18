package Se2.MovieTicket.service;

import Se2.MovieTicket.dto.TicketDTO;
import Se2.MovieTicket.model.Ticket;
import Se2.MovieTicket.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TicketService {
    @Autowired
    private TicketRepository ticketRepository;

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
        ticket.setShowtimeId(ticketDTO.getShowtimeId());
        ticket.setTicketPrice(ticketDTO.getTicketPrice());
        return ticketRepository.save(ticket);
    }

    public Ticket updateTicket(Long id, TicketDTO ticketDTO) {
        Optional<Ticket> ticketData = ticketRepository.findById(id);
        if (ticketData.isPresent()) {
            Ticket ticket = ticketData.get();
            ticket.setOrderId(ticketDTO.getOrderId());
            ticket.setSeatId(ticketDTO.getSeatId());
            ticket.setShowtimeId(ticketDTO.getShowtimeId());
            ticket.setTicketPrice(ticketDTO.getTicketPrice());
            return ticketRepository.save(ticket);
        }
        return null;
    }

    public void deleteTicket(Long id) {
        ticketRepository.deleteById(id);
    }
}