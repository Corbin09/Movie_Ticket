package Se2.MovieTicket.repository;

import Se2.MovieTicket.model.Showtime;
import Se2.MovieTicket.model.Film;
import Se2.MovieTicket.model.Room;
import Se2.MovieTicket.model.Cinema;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ShowtimeRepository extends JpaRepository<Showtime, Long> {
    @Query("SELECT s FROM Showtime s WHERE s.film = :film")
    List<Showtime> findByFilm(@Param("film") Film film);

    @Query("SELECT s FROM Showtime s WHERE s.room = :room")
    List<Showtime> findByRoom(@Param("room") Room room);

    @Query("SELECT s FROM Showtime s WHERE s.cinema = :cinema")
    List<Showtime> findByCinema(@Param("cinema") Cinema cinema);

    @Query("SELECT s FROM Showtime s WHERE s.showDate = :showDate")
    List<Showtime> findByShowDate(@Param("showDate") Date showDate);
}