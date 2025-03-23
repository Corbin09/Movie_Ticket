package Se2.MovieTicket.dto;

import java.util.List;

public class CinemaWithShowtimesDTO {
    private Long cinemaId;
    private String cinemaName;
    private String address;
    private List<ShowtimeDTO> showtimes;

    // Constructors
    public CinemaWithShowtimesDTO() {
    }

    public CinemaWithShowtimesDTO(Long cinemaId, String cinemaName, String address, List<ShowtimeDTO> showtimes) {
        this.cinemaId = cinemaId;
        this.cinemaName = cinemaName;
        this.address = address;
        this.showtimes = showtimes;
    }

    // Getters and Setters
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<ShowtimeDTO> getShowtimes() {
        return showtimes;
    }

    public void setShowtimes(List<ShowtimeDTO> showtimes) {
        this.showtimes = showtimes;
    }
}