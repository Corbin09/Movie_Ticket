package Se2.MovieTicket.dto;

import lombok.Data;

import java.util.Date;

@Data
public class SeatStatusDTO {
    private Long seatStatusId;
    private Long seatId;
    private Long showtimeId;
    private String seatStatus;
    private Date reservedUntil;

    public Long getSeatStatusId() {
        return seatStatusId;
    }

    public void setSeatStatusId(Long seatStatusId) {
        this.seatStatusId = seatStatusId;
    }

    public Long getSeatId() {
        return seatId;
    }

    public void setSeatId(Long seatId) {
        this.seatId = seatId;
    }

    public Long getShowtimeId() {
        return showtimeId;
    }

    public void setShowtimeId(Long showtimeId) {
        this.showtimeId = showtimeId;
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
}