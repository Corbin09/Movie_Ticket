package Se2.MovieTicket.repository;

import Se2.MovieTicket.model.Film;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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

}