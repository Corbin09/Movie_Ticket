package Se2.MovieTicket.repository;

import Se2.MovieTicket.model.Film;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface FilmRepository extends JpaRepository<Film, Long> {
    @Query("SELECT f FROM Film f WHERE LOWER(f.filmName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Film> searchByFilmName(@Param("name") String name);

    @Query("SELECT f FROM Film f WHERE f.releaseDate > :date")
    List<Film> findByReleaseDateAfter(@Param("date") Date date);

    @Query("SELECT f FROM Film f WHERE f.releaseDate < :date")
    List<Film> findByReleaseDateBefore(@Param("date") Date date);

    @Query("SELECT f FROM Film f WHERE LOWER(f.country) = LOWER(:country)")
    List<Film> findByCountry(@Param("country") String country);

    @Query("SELECT f FROM Film f WHERE LOWER(f.filmType) = LOWER(:type)")
    List<Film> findByFilmType(@Param("type") String type);

    @Query("SELECT f FROM Film f WHERE f.ageLimit <= :age")
    List<Film> findByAgeLimit(@Param("age") Integer age);

    @Query("SELECT f FROM Film f JOIN f.filmCategories fc WHERE fc.category.categoryId = :categoryId")
    List<Film> findByCategoryId(@Param("categoryId") Long categoryId);

    @Query("SELECT f FROM Film f JOIN f.filmDirectors fd WHERE fd.director.directorId = :directorId")
    List<Film> findByDirectorId(@Param("directorId") Long directorId);

    @Query("SELECT f FROM Film f JOIN f.filmActors fa WHERE fa.actor.actorId = :actorId")
    List<Film> findByActorId(@Param("actorId") Long actorId);
}