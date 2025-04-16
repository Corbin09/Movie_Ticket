package Se2.MovieTicket.repository;

import Se2.MovieTicket.dto.FilmDTO;
import Se2.MovieTicket.model.Film;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface FilmRepository extends JpaRepository<Film, Long> {
    @Query("SELECT f FROM Film f WHERE LOWER(f.filmName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Film> searchByFilmName(@Param("name") String name);
    // Method query để tìm kiếm theo tên phim, bỏ qua chữ hoa chữ thường
    Page<Film> findByFilmNameContainingIgnoreCase(String name, Pageable pageable);
    @Query("SELECT f FROM Film f WHERE f.releaseDate > :date")
    List<Film> findByReleaseDateAfter(@Param("date") Date date);

    @Query("SELECT f FROM Film f WHERE f.releaseDate < :date")
    List<Film> findByReleaseDateBefore(@Param("date") Date date);

    @Query("SELECT f FROM Film f WHERE LOWER(f.country) = LOWER(:country)")
    List<Film> findByCountry(@Param("country") String country);

    @Query("SELECT f FROM Film f WHERE LOWER(f.filmType) = LOWER(:type)")
    List<Film> findByFilmType(@Param("type") String type);

    @Query("SELECT f FROM Film f WHERE LOWER(f.filmType) = LOWER(:type)")
    Page<Film> findByFilmType(@Param("type") String type, Pageable pageable);

    @Query("SELECT f FROM Film f WHERE f.ageLimit <= :age")
    List<Film> findByAgeLimit(@Param("age") Integer age);

    @Query("SELECT f FROM Film f JOIN f.filmCategories fc WHERE fc.category.categoryId = :categoryId")
    List<Film> findByCategoryId(@Param("categoryId") Long categoryId);

    @Query("SELECT f FROM Film f JOIN f.filmDirectors fd WHERE fd.director.directorId = :directorId")
    List<Film> findByDirectorId(@Param("directorId") Long directorId);

    @Query("SELECT f FROM Film f JOIN f.filmActors fa WHERE fa.actor.actorId = :actorId")
    List<Film> findByActorId(@Param("actorId") Long actorId);

    // Existing methods
    List<Film> findByReleaseDateBeforeOrderByReleaseDateDesc(Date currentDate);

    List<Film> findByReleaseDateAfterOrderByReleaseDateAsc(Date currentDate);

    Page<Film> findByReleaseDateBeforeOrderByReleaseDateDesc(Date currentDate, Pageable pageable);

    Page<Film> findByReleaseDateAfterOrderByReleaseDateAsc(Date currentDate, Pageable pageable);

    @Query("SELECT DISTINCT f FROM Film f " +
            "LEFT JOIN f.filmActors fa " +
            "LEFT JOIN fa.actor a " +
            "LEFT JOIN f.filmDirectors fd " +
            "LEFT JOIN fd.director d " +
            "LEFT JOIN f.showtimes s " +
            "LEFT JOIN s.cinema c " +
            "LEFT JOIN c.cinemaCluster cc " +
            "WHERE LOWER(f.filmName) LIKE CONCAT('%', :searchTerm, '%') " +
            "OR LOWER(f.filmType) LIKE CONCAT('%', :searchTerm, '%') " +
            "OR LOWER(a.actorName) LIKE CONCAT('%', :searchTerm, '%') " +
            "OR LOWER(d.directorName) LIKE CONCAT('%', :searchTerm, '%') " +
            "OR LOWER(c.cinemaName) LIKE CONCAT('%', :searchTerm, '%') " +
            "OR LOWER(cc.clusterName) LIKE CONCAT('%', :searchTerm, '%')")
    Page<Film> findBySearchTerm(@Param("searchTerm") String searchTerm, Pageable pageable);
    @Query("SELECT DISTINCT f FROM Film f " +
            "JOIN f.showtimes s " +
            "JOIN s.room r " +
            "JOIN r.cinema c " +
            "WHERE s.showDate = :date")
    Page<Film> findFilmsWithShowtimesByDate(@Param("date") Date date, Pageable pageable);

    @Query("SELECT DISTINCT f FROM Film f " +
            "JOIN f.showtimes s " +
            "JOIN s.room r " +
            "JOIN r.cinema c " +
            "WHERE c.region.regionId = :regionId " +
            "AND s.showDate = :date")
    Page<Film> findFilmsByRegionAndDate(@Param("regionId") Long regionId, @Param("date") Date date, Pageable pageable);

    @Query("SELECT DISTINCT f FROM Film f " +
            "JOIN f.showtimes s " +
            "JOIN s.room r " +
            "WHERE r.cinema.cinemaId = :cinemaId " +
            "AND s.showDate = :date")
    Page<Film> findFilmsByCinemaAndDate(@Param("cinemaId") Long cinemaId, @Param("date") Date date, Pageable pageable);

    // Repository methods needed
// These should be implemented in FilmRepository interface

    @Query("SELECT DISTINCT f FROM Film f JOIN f.showtimes s JOIN s.cinema c WHERE c.region.regionId = :regionId")
    Page<Film> findFilmsByRegionId(@Param("regionId") Long regionId, Pageable pageable);


    @Query("SELECT DISTINCT f FROM Film f JOIN f.showtimes s JOIN s.cinema c WHERE c.cinemaId = :cinemaId")
    Page findFilmsByCinemaId(@Param("cinemaId") Long cinemaId, Pageable pageable);


    @Query("SELECT DISTINCT f FROM Film f JOIN f.showtimes s JOIN s.room r WHERE r.cinema.cinemaId = :cinemaId AND DATE(s.showDate) = :date")
    Page<Film> findFilmsByCinemaAndDate(@Param("cinemaId") Long cinemaId, @Param("date") LocalDate date, Pageable pageable);

    @Query("SELECT DISTINCT f FROM Film f " +
            "JOIN f.showtimes s " +
            "JOIN s.cinema c " +
            "WHERE s.showTime = :showTime " +
            "AND c.cinemaId = :cinemaId " +
            "AND c.region.regionId = :regionId")
    Page<FilmDTO> findFilmsByShowTime(@Param("showTime") String showTime,
                                      @Param("cinemaId") Long cinemaId,
                                      @Param("regionId") Long regionId,
                                      Pageable pageable);

    @Query("SELECT f FROM Film f WHERE f.country = :vietnamese")
    Page<Film> findByFilmOrigin(@Param("vietnamese") String vietnamese, Pageable pageable);
    @Query("SELECT f FROM Film f WHERE f.country <> :vietnamese")
    Page<Film> findByFilmOriginNot(@Param("vietnamese") String vietnamese, Pageable pageable);

    // OR use a JPQL query
    @Query("SELECT f FROM Film f WHERE f.filmId = :id")
    Optional<Film> findFilmById(@Param("id") Long id);

    // In FilmRepository
    @Query("SELECT f FROM Film f JOIN UserLikeFilm ulf ON f.id = ulf.film.id WHERE ulf.user.userId = :userId")
    List<Film> findLikedFilmsByUserId(@Param("userId") Long userId);


    // Add this to your FilmRepository interface
    @Query("SELECT DISTINCT f FROM Film f JOIN f.filmActors fa WHERE fa.actor.actorId = :actorId")
    Page<Film> findFilmsByActorId(@Param("actorId") Long actorId, Pageable pageable);

    @Query("SELECT DISTINCT f FROM Film f JOIN f.filmDirectors fd WHERE fd.director.directorId = :directorId")
    Page<Film> findByDirectorIdPage(@Param("directorId") Long directorId, Pageable pageable);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM actor_film WHERE film_id = :filmId", nativeQuery = true)
    void deleteAllActorsByFilmId(@Param("filmId") Long filmId);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO actor_film (film_id, actor_id) VALUES (:filmId, :actorId)", nativeQuery = true)
    void addActorToFilm(@Param("filmId") Long filmId, @Param("actorId") Long actorId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM category_film WHERE film_id = :filmId", nativeQuery = true)
    void deleteAllCategoriesByFilmId(@Param("filmId") Long filmId);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO category_film (film_id, category_id) VALUES (:filmId, :categoryId)", nativeQuery = true)
    void addCategoryToFilm(@Param("filmId") Long filmId, @Param("categoryId") Long categoryId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM director_film WHERE film_id = :filmId", nativeQuery = true)
    void deleteAllDirectorsByFilmId(@Param("filmId") Long filmId);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO director_film (film_id, director_id) VALUES (:filmId, :directorId)", nativeQuery = true)
    void addDirectorToFilm(@Param("filmId") Long filmId, @Param("directorId") Long directorId);
}