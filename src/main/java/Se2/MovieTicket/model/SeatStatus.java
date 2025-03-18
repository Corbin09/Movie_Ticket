package Se2.MovieTicket.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "seat_status")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeatStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seat_status_id")
    private Long seatStatusId;

    @ManyToOne
    @JoinColumn(name = "seat_id", nullable = false)
    @JsonIgnore
    private Seat seat;

    @ManyToOne
    @JoinColumn(name = "showtime_id", nullable = false)
    @JsonIgnore
    private Showtime showtime;

    @Column(name = "seat_status", nullable = false)
    private String seatStatus;

    @Column(name = "reserved_until")
    @Temporal(TemporalType.TIMESTAMP)
    private Date reservedUntil;

    public Long getSeatStatusId() {
        return seatStatusId;
    }

    public void setSeatStatusId(Long seatStatusId) {
        this.seatStatusId = seatStatusId;
    }

    public Seat getSeat() {
        return seat;
    }

    public void setSeat(Seat seat) {
        this.seat = seat;
    }

    public Showtime getShowtime() {
        return showtime;
    }

    public void setShowtime(Showtime showtime) {
        this.showtime = showtime;
    }

    public String getSeatStatus() {
        return seatStatus;
    }

    public void setSeatStatus(String seatStatus) {
        this.seatStatus = seatStatus;
    }

    public Date getReservedUntil() {
        return reservedUntil;
    }

    public void setReservedUntil(Date reservedUntil) {
        this.reservedUntil = reservedUntil;
    }

    public void setSeatId(Long seatId) {
        if (this.seat == null) {
            this.seat = new Seat();
        }
        this.seat.setSeatId(seatId);
    }

    public void setShowtimeId(Long showtimeId) {
        if (this.showtime == null) {
            this.showtime = new Showtime();
        }
        this.showtime.setShowtimeId(showtimeId);
    }
}