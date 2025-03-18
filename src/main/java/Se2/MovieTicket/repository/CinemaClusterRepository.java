package Se2.MovieTicket.repository;

import Se2.MovieTicket.model.CinemaCluster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CinemaClusterRepository extends JpaRepository<CinemaCluster, Long> {
    List<CinemaCluster> findByClusterNameContaining(String name);
}