package Se2.MovieTicket.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"user", "showtime"})
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long orderId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;

    @ManyToOne
    @JoinColumn(name = "showtime_id")
    @JsonBackReference
    private Showtime showtime;

    @Column(name = "order_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date orderDate;


    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    @BatchSize(size = 50)
    @Fetch(FetchMode.SUBSELECT)  // This is key for batch fetching
    private Set<Ticket> tickets = new HashSet<>();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    @BatchSize(size = 20)
    @Fetch(FetchMode.SUBSELECT)
    private Set<PopcornOrder> popcornOrders = new HashSet<>();

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

    // Getters and setters
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

    // Helper methods to maintain bidirectional relationship
    public void addTicket(Ticket ticket) {
        System.out.println("Adding ticket to order");
        if (tickets == null) {
            System.out.println("Tickets collection was null, initializing it");
            tickets = new HashSet<>();
        }
        System.out.println("Current tickets size before adding: " + tickets.size());
        tickets.add(ticket);
        ticket.setOrder(this);
        System.out.println("Current tickets size after adding: " + tickets.size());
    }

    public void removeTicket(Ticket ticket) {
        System.out.println("Removing ticket from order");
        if (tickets != null) {
            System.out.println("Current tickets size before removing: " + tickets.size());
            tickets.remove(ticket);
            ticket.setOrder(null);
            System.out.println("Current tickets size after removing: " + tickets.size());
        } else {
            System.out.println("Cannot remove ticket: tickets collection is null");
        }
    }

    public void addPopcornOrder(PopcornOrder popcornOrder) {
        System.out.println("Adding popcorn order to order");
        if (popcornOrders == null) {
            System.out.println("PopcornOrders collection was null, initializing it");
            popcornOrders = new HashSet<>();
        }
        System.out.println("Current popcorn orders size before adding: " + popcornOrders.size());
        popcornOrders.add(popcornOrder);
        popcornOrder.setOrder(this);
        System.out.println("Current popcorn orders size after adding: " + popcornOrders.size());
    }

    public void removePopcornOrder(PopcornOrder popcornOrder) {
        System.out.println("Removing popcorn order from order");
        if (popcornOrders != null) {
            System.out.println("Current popcorn orders size before removing: " + popcornOrders.size());
            popcornOrders.remove(popcornOrder);
            popcornOrder.setOrder(null);
            System.out.println("Current popcorn orders size after removing: " + popcornOrders.size());
        } else {
            System.out.println("Cannot remove popcorn order: popcornOrders collection is null");
        }
}}