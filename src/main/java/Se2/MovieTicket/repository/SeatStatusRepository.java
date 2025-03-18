package Se2.MovieTicket.repository;

import Se2.MovieTicket.model.SeatStatus;
import Se2.MovieTicket.model.Seat;
import Se2.MovieTicket.model.Showtime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeatStatusRepository extends JpaRepository<SeatStatus, Long> {
    @Query("SELECT ss FROM SeatStatus ss WHERE ss.seat = :seat")
    List<SeatStatus> findBySeat(@Param("seat") Seat seat);

    @Query("SELECT ss FROM SeatStatus ss WHERE ss.showtime = :showtime")
    List<SeatStatus> findByShowtime(@Param("showtime") Showtime showtime);

    @Query("SELECT ss FROM SeatStatus ss WHERE ss.seat = :seat AND ss.showtime = :showtime")
    Optional<SeatStatus> findBySeatAndShowtime(@Param("seat") Seat seat, @Param("showtime") Showtime showtime);
}