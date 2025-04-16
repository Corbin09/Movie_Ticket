package Se2.MovieTicket.repository;

import Se2.MovieTicket.model.SeatStatus;
import Se2.MovieTicket.model.Seat;
import Se2.MovieTicket.model.Showtime;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface SeatStatusRepository extends JpaRepository<SeatStatus, Long> {
    @Query("SELECT ss FROM SeatStatus ss WHERE ss.seat = :seat")
    List<SeatStatus> findBySeat(@Param("seat") Seat seat);

    @Query("SELECT ss FROM SeatStatus ss WHERE ss.showtime = :showtime")
    List<SeatStatus> findByShowtime(@Param("showtime") Showtime showtime);

    // Consider using a more targeted query that only returns necessary fields
    @Query("SELECT ss FROM SeatStatus ss WHERE ss.seat.id = :seatId AND ss.showtime.id = :showtimeId")
    SeatStatus findBySeatIdAndShowtimeId(@Param("seatId") Long seatId, @Param("showtimeId") Long showtimeId);

    List<SeatStatus> findByShowtimeShowtimeId(Long showtimeId);

    // In SeatStatusRepository
    @Modifying
    @Query("UPDATE SeatStatus ss SET ss.seatStatus = :status WHERE ss.seat.seatId IN :seatIds AND ss.showtime.showtimeId = :showtimeId")
    int updateStatusInBatch(@Param("seatIds") Set<Long> seatIds, @Param("showtimeId") Long showtimeId, @Param("status") String status);

}