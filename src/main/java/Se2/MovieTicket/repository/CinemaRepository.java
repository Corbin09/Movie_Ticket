package Se2.MovieTicket.repository;

import Se2.MovieTicket.model.Cinema;
import Se2.MovieTicket.model.CinemaCluster;
import Se2.MovieTicket.model.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CinemaRepository extends JpaRepository<Cinema, Long> {
    List<Cinema> findByCinemaCluster(CinemaCluster cinemaCluster);
    List<Cinema> findByRegion(Region region);
    List<Cinema> findByCinemaNameContaining(String name);
    List<Cinema> findByAddressContaining(String address);
}