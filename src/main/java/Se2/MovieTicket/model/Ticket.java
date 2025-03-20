package Se2.MovieTicket.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tickets")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ticket_id")
    private Long ticketId;

    @ManyToOne
    @JoinColumn(name = "order_id")
    @JsonIgnore
    private Order order;

    @ManyToOne
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;

//    @ManyToOne
//    @JoinColumn(name = "showtime_id", nullable = false)
//    private Showtime showtime;

    @Column(name = "ticket_price")
    private Double ticketPrice;

    public Long getTicketId() {
        return ticketId;
    }

    public void setTicketId(Long ticketId) {
        this.ticketId = ticketId;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Seat getSeat() {
        return seat;
    }

    public void setSeat(Seat seat) {
        this.seat = seat;
    }
//
//    public Showtime getShowtime() {
//        return showtime;
//    }
//
//    public void setShowtime(Showtime showtime) {
//        this.showtime = showtime;
//    }

    public Double getTicketPrice() {
        return ticketPrice;
    }

    public void setTicketPrice(Double ticketPrice) {
        this.ticketPrice = ticketPrice;
    }

    public void setOrderId(Long orderId) {
        if (this.order == null) {
            this.order = new Order();
        }
        this.order.setOrderId(orderId);
    }

    public void setSeatId(Long seatId) {
        if (this.seat == null) {
            this.seat = new Seat();
        }
        this.seat.setSeatId(seatId);
    }

//    public void setShowtimeId(Long showtimeId) {
//        if (this.showtime == null) {
//            this.showtime = new Showtime();
//        }
//        this.showtime.setShowtimeId(showtimeId);
//    }
}