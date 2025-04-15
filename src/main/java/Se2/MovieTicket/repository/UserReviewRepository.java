package Se2.MovieTicket.repository;

import Se2.MovieTicket.model.UserReview;
import Se2.MovieTicket.model.UserReviewId;
import Se2.MovieTicket.model.Film;
import Se2.MovieTicket.model.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Repository
public interface UserReviewRepository extends JpaRepository<UserReview, UserReviewId> {
    @Query("SELECT ur FROM UserReview ur WHERE ur.id.userId = :userId")
    List<UserReview> findByUserId(@Param("userId") Long userId);

    @Query("SELECT ur FROM UserReview ur WHERE ur.id.filmId = :filmId")
    List<UserReview> findByFilmId(@Param("filmId") Long filmId);

    @Query("SELECT ur FROM UserReview ur WHERE ur.film = :film")
    List<UserReview> findByFilm(@Param("film") Film film);

    @Query("SELECT ur FROM UserReview ur WHERE ur.user = :user")
    List<UserReview> findByUser(@Param("user") User user);

    @Query("SELECT ur FROM UserReview ur WHERE ur.film = :film AND ur.user = :user")
    List<UserReview> findByFilmAndUser(@Param("film") Film film, @Param("user") User user);

    @Query("SELECT ur FROM UserReview ur WHERE ur.star >= :minStar")
    List<UserReview> findByStarGreaterThanEqual(@Param("minStar") Integer minStar);

    List<UserReview> findByFilmFilmId(Long filmId);

    @Query("SELECT COUNT(ur) > 0 FROM UserReview ur WHERE ur.id.userId = :userId AND ur.id.filmId = :filmId")
    boolean existsByUserIdAndFilmId(@Param("userId") Long userId, @Param("filmId") Long filmId);

    @Query("SELECT ur FROM UserReview ur WHERE ur.id.userId = :userId AND ur.id.filmId = :filmId")
    UserReview findByUserIdAndFilmId(@Param("userId") Long userId, @Param("filmId") Long filmId);

    @Modifying
    @Transactional
    @Query("UPDATE UserReview ur SET ur.star = :star, ur.comments = :comment, ur.datePosted = :date WHERE ur.id.userId = :userId AND ur.id.filmId = :filmId")
    void updateReview(@Param("userId") Long userId, @Param("filmId") Long filmId,
                      @Param("star") Integer star, @Param("comment") String comment,
                      @Param("date") Date date);
}
