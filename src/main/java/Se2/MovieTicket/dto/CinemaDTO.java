package Se2.MovieTicket.dto;

import Se2.MovieTicket.model.Showtime;
import lombok.Data;

import java.util.Set;

@Data
public class CinemaDTO {
    private Long cinemaId;
    private String cinemaName;
    private String address;
    private Long clusterId;
    private Set<Showtime> showtimes;

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
}