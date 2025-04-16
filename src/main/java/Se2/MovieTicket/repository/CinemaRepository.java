package Se2.MovieTicket.repository;

import Se2.MovieTicket.model.Cinema;
import Se2.MovieTicket.model.CinemaCluster;
import Se2.MovieTicket.model.Region;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Repository
public interface CinemaRepository extends JpaRepository<Cinema, Long> {
    List<Cinema> findByCinemaCluster(CinemaCluster cinemaCluster);
    List<Cinema> findByRegion(Region region);
    List<Cinema> findByCinemaNameContaining(String name);
    List<Cinema> findByAddressContaining(String address);

    @Query("SELECT DISTINCT c FROM Cinema c " +
            "LEFT JOIN FETCH c.showtimes s " +
            "WHERE s.film.filmId = :filmId " +
            "AND (CAST(s.showDate AS date) = CAST(:date AS date) OR :date IS NULL)")
    List<Cinema> findCinemasWithShowtimesForFilm(
            @Param("filmId") Long filmId,
            @Param("date") String date
    );

    @Query("SELECT c, s FROM Cinema c JOIN c.showtimes s WHERE s.film.filmId = :filmId AND s.showDate = :showDate")
    List<Object[]> findCinemasWithShowtimesByFilmAndDate(@Param("filmId") Long filmId, @Param("showDate") LocalDate showDate);

    @Query("SELECT c FROM Cinema c WHERE c.region.regionId = :regionId")
    List<Cinema> findByRegionId(@Param("regionId") Long regionId);

    /**
     * Get only necessary cinema fields for dropdowns
     */
    @Query("SELECT new Cinema(c.cinemaId, c.cinemaName) FROM Cinema c ORDER BY c.cinemaName")
    List<Cinema> findAllBasicInfo();

    Page<Cinema> findByCinemaNameContainingIgnoreCaseOrAddressContainingIgnoreCase(String name, String address, Pageable pageable);


    Page<Cinema> findByCinemaCluster_ClusterNameContainingIgnoreCase(String clusterName, Pageable pageable);


}