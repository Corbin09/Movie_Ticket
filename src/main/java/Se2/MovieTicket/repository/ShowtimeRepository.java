package Se2.MovieTicket.repository;

import Se2.MovieTicket.model.Showtime;
import Se2.MovieTicket.model.Film;
import Se2.MovieTicket.model.Room;
import Se2.MovieTicket.model.Cinema;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
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

    @Query("SELECT s FROM Showtime s WHERE s.film.filmId = :filmId AND s.showDate = :showDate AND s.cinema.cinemaId = :cinemaId")
    List<Showtime> findByFilmIdAndShowDateAndCinemaId(@Param("filmId") Long filmId,
                                                      @Param("showDate") Date showDate,
                                                      @Param("cinemaId") Long cinemaId);

    @Query("SELECT COUNT(s) FROM Showtime s WHERE s.film.filmId = :filmId AND CAST(s.showDate AS LocalDate) = :showDate")
    Integer countByFilmIdAndShowDate(@Param("filmId") Long filmId, @Param("showDate") LocalDate showDate);

    @Query("SELECT s FROM Showtime s " +
            "JOIN s.film f " +
            "WHERE f.filmId = :filmId " +
            "AND s.showDate = :date " +
            "ORDER BY s.showDate, s.showTime")
    List<Showtime> findByFilmAndDate(@Param("filmId") Long filmId, @Param("date") LocalDate date);

    @Query("SELECT s FROM Showtime s " +
            "JOIN s.film f " +
            "JOIN s.room r " +
            "WHERE f.filmId = :filmId " +
            "AND r.cinema.cinemaId = :cinemaId " +
            "AND s.showDate = :date " +
            "ORDER BY s.showDate, s.showTime")
    List<Showtime> findByFilmAndCinemaAndDate(
            @Param("filmId") Long filmId,
            @Param("cinemaId") Long cinemaId,
            @Param("date") LocalDate date);

    @Query("SELECT s FROM Showtime s " +
            "JOIN s.film f " +
            "JOIN s.room r " +
            "JOIN r.cinema c " +
            "WHERE f.filmId = :filmId " +
            "AND c.region.regionId = :regionId " +
            "AND s.showDate = :date " +
            "ORDER BY s.showDate, s.showTime")
    List<Showtime> findByFilmAndRegionAndDate(
            @Param("filmId") Long filmId,
            @Param("regionId") Long regionId,
            @Param("date") LocalDate date);}