package Se2.MovieTicket.repository;

import Se2.MovieTicket.model.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegionRepository extends JpaRepository<Region, Long> {
    List<Region> findByRegionNameContaining(String name);
}