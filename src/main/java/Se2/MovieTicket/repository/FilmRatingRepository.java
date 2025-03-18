package Se2.MovieTicket.repository;

import Se2.MovieTicket.model.FilmRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FilmRatingRepository extends JpaRepository<FilmRating, Long> {
    @Query("SELECT fr FROM FilmRating fr WHERE fr.film.id = :filmId")
    Optional<FilmRating> findByFilmId(@Param("filmId") Long filmId);
}