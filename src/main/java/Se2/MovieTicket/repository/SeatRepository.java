package Se2.MovieTicket.repository;

import Se2.MovieTicket.model.Seat;
import Se2.MovieTicket.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
    @Query("SELECT s FROM Seat s WHERE s.room = :room")
    List<Seat> findByRoom(@Param("room") Room room);

    @Query("SELECT s FROM Seat s WHERE s.room = :room AND s.seatType = :seatType")
    List<Seat> findByRoomAndSeatType(@Param("room") Room room, @Param("seatType") String seatType);

    @Query("SELECT s FROM Seat s WHERE s.room = :room AND s.seatRow = :row AND s.seatNumber = :number")
    Optional<Seat> findByRoomAndSeatRowAndSeatNumber(@Param("room") Room room, @Param("row") String row, @Param("number") Integer number);
}