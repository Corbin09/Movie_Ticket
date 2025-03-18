package Se2.MovieTicket.repository;

import Se2.MovieTicket.model.FilmCategory;
import Se2.MovieTicket.model.FilmCategoryId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FilmCategoryRepository extends JpaRepository<FilmCategory, FilmCategoryId> {
    @Query("SELECT fc FROM FilmCategory fc WHERE fc.id.filmId = :filmId")
    List<FilmCategory> findByFilmId(@Param("filmId") Long filmId);

    @Query("SELECT fc FROM FilmCategory fc WHERE fc.id.categoryId = :categoryId")
    List<FilmCategory> findByCategoryId(@Param("categoryId") Long categoryId);

    @Query("SELECT fc FROM FilmCategory fc WHERE fc.id.filmId = :filmId AND fc.id.categoryId = :categoryId")
    Optional<FilmCategory> findByFilmIdAndCategoryId(@Param("filmId") Long filmId, @Param("categoryId") Long categoryId);
}