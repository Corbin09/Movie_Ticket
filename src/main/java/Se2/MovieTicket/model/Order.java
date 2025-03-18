package Se2.MovieTicket.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long orderId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @ManyToOne
    @JoinColumn(name = "showtime_id")
    @JsonIgnore
    private Showtime showtime;

    @Column(name = "order_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date orderDate;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Ticket> tickets;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<PopcornOrder> popcornOrders;

    @Column(name = "total_price", nullable = false)
    private Double totalPrice = 0.0;

    public void calculateTotal() {
        double total = 0.0;
        if (tickets != null) {
            for (Ticket ticket : tickets) {
                if (ticket.getTicketPrice() != null) {
                    total += ticket.getTicketPrice();
                }
            }
        }
        if (popcornOrders != null) {
            for (PopcornOrder popcornOrder : popcornOrders) {
                if (popcornOrder.getPopcornCombo() != null && popcornOrder.getPopcornCombo().getComboPrice() != null) {
                    double comboPrice = popcornOrder.getPopcornCombo().getComboPrice();
                    int quantity = popcornOrder.getComboQuantity() != null ? popcornOrder.getComboQuantity() : 1;
                    total += comboPrice * quantity;
                }
            }
        }
        this.totalPrice = total;


    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Showtime getShowtime() {
        return showtime;
    }

    public void setShowtime(Showtime showtime) {
        this.showtime = showtime;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public Set<Ticket> getTickets() {
        return tickets;
    }

    public void setTickets(Set<Ticket> tickets) {
        this.tickets = tickets;
    }

    public Set<PopcornOrder> getPopcornOrders() {
        return popcornOrders;
    }

    public void setPopcornOrders(Set<PopcornOrder> popcornOrders) {
        this.popcornOrders = popcornOrders;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void setUserId(Long userId) {
        if (this.user == null) {
            this.user = new User();
        }
        this.user.setUserId(userId);
    }

    public void setShowtimeId(Long showtimeId) {
        if (this.showtime == null) {
            this.showtime = new Showtime();
        }
        this.showtime.setShowtimeId(showtimeId);
    }
}