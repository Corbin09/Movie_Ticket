package Se2.MovieTicket.repository;

import Se2.MovieTicket.model.UserLikeFilm;
import Se2.MovieTicket.model.UserLikeFilmId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserLikeFilmRepository extends JpaRepository<UserLikeFilm, UserLikeFilmId> {
    @Query("SELECT u FROM UserLikeFilm u WHERE u.id.userId = :userId")
    List<UserLikeFilm> findByUserId(@Param("userId") Long userId);

    @Query("SELECT u FROM UserLikeFilm u WHERE u.id.filmId = :filmId")
    List<UserLikeFilm> findByFilmId(@Param("filmId") Long filmId);

    @Query("SELECT u FROM UserLikeFilm u WHERE u.id.userId = :userId AND u.id.filmId = :filmId")
    Optional<UserLikeFilm> findByUserIdAndFilmId(@Param("userId") Long userId, @Param("filmId") Long filmId);
}