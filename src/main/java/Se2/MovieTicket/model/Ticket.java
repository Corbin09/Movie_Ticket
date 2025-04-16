package Se2.MovieTicket.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tickets")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"order"})
@EqualsAndHashCode(exclude = {"order"})
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ticket_id")
    private Long ticketId;

    @ManyToOne
    @JoinColumn(name = "order_id")
    @JsonBackReference
    private Order order;

    @ManyToOne
    @JoinColumn(name = "seat_id", nullable = false)
    @JsonBackReference
    private Seat seat;

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


}