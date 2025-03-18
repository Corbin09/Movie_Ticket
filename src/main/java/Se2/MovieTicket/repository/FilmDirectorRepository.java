package Se2.MovieTicket.repository;

import Se2.MovieTicket.model.FilmDirector;
import Se2.MovieTicket.model.FilmDirectorId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FilmDirectorRepository extends JpaRepository<FilmDirector, FilmDirectorId> {
    @Query("SELECT fd FROM FilmDirector fd WHERE fd.id.filmId = :filmId")
    List<FilmDirector> findByFilmId(@Param("filmId") Long filmId);

    @Query("SELECT fd FROM FilmDirector fd WHERE fd.id.directorId = :directorId")
    List<FilmDirector> findByDirectorId(@Param("directorId") Long directorId);

    @Query("SELECT fd FROM FilmDirector fd WHERE fd.id.filmId = :filmId AND fd.id.directorId = :directorId")
    Optional<FilmDirector> findByFilmIdAndDirectorId(@Param("filmId") Long filmId, @Param("directorId") Long directorId);
}