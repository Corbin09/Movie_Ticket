package Se2.MovieTicket.dto;

import lombok.Data;

import java.util.Set;

@Data
public class CinemaDTO {
    private Long cinemaId;
    private String cinemaName;
    private String address;
    private Long clusterId;
    private String complexName;
    private String complexColor;
    private Set<ShowtimeDTO> showtimes;

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

    public Long getClusterId() {
        return clusterId;
    }

    public void setClusterId(Long clusterId) {
        this.clusterId = clusterId;
    }

    public String getComplexName() {
        return complexName;
    }

    public void setComplexName(String complexName) {
        this.complexName = complexName;
    }

    public String getComplexColor() {
        return complexColor;
    }

    public void setComplexColor(String complexColor) {
        this.complexColor = complexColor;
    }

    public Set<ShowtimeDTO> getShowtimes() {
        return showtimes;
    }

    public void setShowtimes(Set<ShowtimeDTO> showtimes) {
        this.showtimes = showtimes;
    }
}