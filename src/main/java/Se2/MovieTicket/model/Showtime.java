package Se2.MovieTicket.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "showtimes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Showtime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "showtime_id")
    private Long showtimeId;

    @ManyToOne
    @JoinColumn(name = "film_id", nullable = false)
    @JsonIgnore
    private Film film;

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    @JsonIgnore
    private Room room;

    @ManyToOne
    @JoinColumn(name = "cinema_id", nullable = false)
    @JsonIgnore
    private Cinema cinema;

    @Column(name = "show_date")
    @Temporal(TemporalType.DATE)
    private Date showDate;

    @Column(name = "show_time")
    private String showTime;

    @OneToMany(mappedBy = "showtime", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Ticket> tickets;

    @OneToMany(mappedBy = "showtime", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<SeatStatus> seatStatuses;

    @OneToMany(mappedBy = "showtime", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Order> orders;

    public Long getShowtimeId() {
        return showtimeId;
    }

    public void setShowtimeId(Long showtimeId) {
        this.showtimeId = showtimeId;
    }

    public Film getFilm() {
        return film;
    }

    public void setFilm(Film film) {
        this.film = film;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public Cinema getCinema() {
        return cinema;
    }

    public void setCinema(Cinema cinema) {
        this.cinema = cinema;
    }

    public Date getShowDate() {
        return showDate;
    }

    public void setShowDate(Date showDate) {
        this.showDate = showDate;
    }

    public String getShowTime() {
        return showTime;
    }

    public void setShowTime(String showTime) {
        this.showTime = showTime;
    }

    public Set<Ticket> getTickets() {
        return tickets;
    }

    public void setTickets(Set<Ticket> tickets) {
        this.tickets = tickets;
    }

    public Set<SeatStatus> getSeatStatuses() {
        return seatStatuses;
    }

    public void setSeatStatuses(Set<SeatStatus> seatStatuses) {
        this.seatStatuses = seatStatuses;
    }

    public Set<Order> getOrders() {
        return orders;
    }

    public void setOrders(Set<Order> orders) {
        this.orders = orders;
    }

    public void setFilmId(Long filmId) {
        if (this.film == null) {
            this.film = new Film();
        }
        this.film.setFilmId(filmId);
    }

    public void setRoomId(Long roomId) {
        if (this.room == null) {
            this.room = new Room();
        }
        this.room.setRoomId(roomId);
    }

    public void setCinemaId(Long cinemaId) {
        if (this.cinema == null) {
            this.cinema = new Cinema();
        }
        this.cinema.setCinemaId(cinemaId);
    }
}