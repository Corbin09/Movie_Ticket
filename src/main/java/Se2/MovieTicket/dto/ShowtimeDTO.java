//package Se2.MovieTicket.dto;
//
//import lombok.Data;
//
//import java.util.Date;
//
//@Data
//public class ShowtimeDTO {
//    private Long showtimeId;
//    private Long filmId;
//    private Long roomId;
//    private Long cinemaId;
//    private Date showDate;
//    private String showTime;
//
//    public Long getShowtimeId() {
//        return showtimeId;
//    }
//
//    public void setShowtimeId(Long showtimeId) {
//        this.showtimeId = showtimeId;
//    }
//
//    public Long getFilmId() {
//        return filmId;
//    }
//
//    public void setFilmId(Long filmId) {
//        this.filmId = filmId;
//    }
//
//    public Long getRoomId() {
//        return roomId;
//    }
//
//    public void setRoomId(Long roomId) {
//        this.roomId = roomId;
//    }
//
//    public Long getCinemaId() {
//        return cinemaId;
//    }
//
//    public void setCinemaId(Long cinemaId) {
//        this.cinemaId = cinemaId;
//    }
//
//    public Date getShowDate() {
//        return showDate;
//    }
//
//    public void setShowDate(Date showDate) {
//        this.showDate = showDate;
//    }
//
//    public String getShowTime() {
//        return showTime;
//    }
//
//    public void setShowTime(String showTime) {
//        this.showTime = showTime;
//    }
//}

package Se2.MovieTicket.dto;

import java.util.Date;

public class ShowtimeDTO {
    private Long showtimeId;
    private Date showDate;
    private String showTime;
    private Long filmId;
    private String filmName;
    private Long roomId;
    private String roomName;
    private Long cinemaId;
    private String cinemaName;
    private Double price;

    public Long getShowtimeId() {
        return showtimeId;
    }

    public void setShowtimeId(Long showtimeId) {
        this.showtimeId = showtimeId;
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

    public Long getFilmId() {
        return filmId;
    }

    public void setFilmId(Long filmId) {
        this.filmId = filmId;
    }

    public String getFilmName() {
        return filmName;
    }

    public void setFilmName(String filmName) {
        this.filmName = filmName;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public Long getCinemaId() {
        return cinemaId;
    }

    public void setCinemaId(Long cinemaId) {
        this.cinemaId = cinemaId;
    }

    public String getCinemaName() {
        return cinemaName;
    }

    public void setCinemaName(String cinemaName) {
        this.cinemaName = cinemaName;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}