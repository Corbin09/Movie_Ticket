package Se2.MovieTicket.repository;

import Se2.MovieTicket.model.UserReview;
import Se2.MovieTicket.model.UserReviewId;
import Se2.MovieTicket.model.Film;
import Se2.MovieTicket.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserReviewRepository extends JpaRepository<UserReview, UserReviewId> {

    @Query("SELECT ur FROM UserReview ur WHERE ur.id.userId = :userId")
    List<UserReview> findByUserId(@Param("userId") Long userId);

    @Query("SELECT ur FROM UserReview ur WHERE ur.id.filmId = :filmId")
    List<UserReview> findByFilmId(@Param("filmId") Long filmId);

    @Query("SELECT ur FROM UserReview ur WHERE ur.id.userId = :userId AND ur.id.filmId = :filmId")
    Optional<UserReview> findByUserIdAndFilmId(@Param("userId") Long userId, @Param("filmId") Long filmId);

    @Query("SELECT ur FROM UserReview ur WHERE ur.film = :film")
    List<UserReview> findByFilm(@Param("film") Film film);

    @Query("SELECT ur FROM UserReview ur WHERE ur.user = :user")
    List<UserReview> findByUser(@Param("user") User user);

    @Query("SELECT ur FROM UserReview ur WHERE ur.film = :film AND ur.user = :user")
    List<UserReview> findByFilmAndUser(@Param("film") Film film, @Param("user") User user);

    @Query("SELECT ur FROM UserReview ur WHERE ur.star >= :minStar")
    List<UserReview> findByStarGreaterThanEqual(@Param("minStar") Integer minStar);
}
