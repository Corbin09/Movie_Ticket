package Se2.MovieTicket.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Entity
@Table(name = "cinemas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Cinema {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cinema_id")
    private Long cinemaId;

    @Column(name = "cinema_name", nullable = false)
    private String cinemaName;

    @Column(name = "address")
    private String address;

    @ManyToOne
    @JoinColumn(name = "cluster_id")
    @JsonIgnore
    private CinemaCluster cinemaCluster;

    @OneToMany(mappedBy = "cinema", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Room> rooms;

    @OneToMany(mappedBy = "cinema", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Showtime> showtimes;

    @ManyToOne
    @JoinColumn(name = "region_id") // Thêm ánh xạ đến Region
    @JsonIgnore
    private Region region;

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

    public CinemaCluster getCinemaCluster() {
        return cinemaCluster;
    }

    public void setCinemaCluster(CinemaCluster cinemaCluster) {
        this.cinemaCluster = cinemaCluster;
    }

    public Set<Room> getRooms() {
        return rooms;
    }

    public Set<Showtime> getShowtimes() {
        return showtimes;
    }

    public void setShowtimes(Set<Showtime> showtimes) {
        this.showtimes = showtimes;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public void setRooms(Set<Room> rooms) {
        this.rooms = rooms;
    }
}