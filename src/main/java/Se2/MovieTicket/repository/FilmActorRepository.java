package Se2.MovieTicket.repository;

import Se2.MovieTicket.model.Actor;
import Se2.MovieTicket.model.Film;
import Se2.MovieTicket.model.FilmActor;
import Se2.MovieTicket.model.FilmActorId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FilmActorRepository extends JpaRepository<FilmActor, FilmActorId> {
    @Query("SELECT fa FROM FilmActor fa WHERE fa.id.filmId = :filmId")
    List<FilmActor> findByFilmId(@Param("filmId") Long filmId);

    @Query("SELECT fa FROM FilmActor fa WHERE fa.id.actorId = :actorId")
    List<FilmActor> findByActorId(@Param("actorId") Long actorId);

    @Query("SELECT fa FROM FilmActor fa WHERE fa.id.filmId = :filmId AND fa.id.actorId = :actorId")
    Optional<FilmActor> findByFilmIdAndActorId(@Param("filmId") Long filmId, @Param("actorId") Long actorId);

    List<FilmActor> findByFilm(Film film);

    List<FilmActor> findByActor(Actor actor);

    Optional<FilmActor> findByFilmAndActor(Film film, Actor actor);
}