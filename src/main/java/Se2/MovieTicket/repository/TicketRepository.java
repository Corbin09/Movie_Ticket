package Se2.MovieTicket.repository;

import Se2.MovieTicket.model.Ticket;
import Se2.MovieTicket.model.Seat;
import Se2.MovieTicket.model.Showtime;
import Se2.MovieTicket.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    @Query("SELECT t FROM Ticket t WHERE t.order = :order")
    List<Ticket> findByOrder(@Param("order") Order order);

    @Query("SELECT t FROM Ticket t WHERE t.showtime = :showtime")
    List<Ticket> findByShowtime(@Param("showtime") Showtime showtime);

    @Query("SELECT t FROM Ticket t WHERE t.seat = :seat")
    List<Ticket> findBySeat(@Param("seat") Seat seat);
}