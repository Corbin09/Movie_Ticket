package Se2.MovieTicket.repository;

import Se2.MovieTicket.model.Room;
import Se2.MovieTicket.model.Cinema;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    @Query("SELECT r FROM Room r WHERE r.cinema = :cinema")
    List<Room> findByCinema(@Param("cinema") Cinema cinema);
}