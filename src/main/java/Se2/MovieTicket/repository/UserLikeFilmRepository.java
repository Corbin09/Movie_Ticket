package Se2.MovieTicket.repository;

import Se2.MovieTicket.model.Film;
import Se2.MovieTicket.model.User;
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

    /**
     * Find all UserLikeFilm entries for a given user
     *
     * @param userId the ID of the user
     * @return list of UserLikeFilm objects associated with the user
     */
    List<UserLikeFilm> findByUserUserId(Long userId);

    /**
     * Find all UserLikeFilm entries for a given user
     *
     * @param user the user entity
     * @return list of UserLikeFilm objects associated with the user
     */
    List<UserLikeFilm> findByUser(User user);

    /**
     * Find all UserLikeFilm entries for a given film
     *
     * @param filmId the ID of the film
     * @return list of UserLikeFilm objects associated with the film
     */
    List<UserLikeFilm> findByFilmFilmId(Long filmId);

    /**
     * Find all UserLikeFilm entries for a given film
     *
     * @param film the film entity
     * @return list of UserLikeFilm objects associated with the film
     */
    List<UserLikeFilm> findByFilm(Film film);

    /**
     * Find a specific UserLikeFilm entry by user and film
     *
     * @param userId the ID of the user
     * @param filmId the ID of the film
     * @return the UserLikeFilm object if it exists
     */
    Optional<UserLikeFilm> findByUserUserIdAndFilmFilmId(Long userId, Long filmId);

    /**
     * Check if a user likes a specific film
     *
     * @param userId the ID of the user
     * @param filmId the ID of the film
     * @return true if the user likes the film, false otherwise
     */
    boolean existsByUserUserIdAndFilmFilmId(Long userId, Long filmId);

    /**
     * Count the number of users who like a specific film
     *
     * @param filmId the ID of the film
     * @return the count of users who like the film
     */
    int countByFilmFilmId(Long filmId);

    /**
     * Find a specific UserLikeFilm entry by user and film entities
     *
     * @param user the user entity
     * @param film the film entity
     * @return the UserLikeFilm object if it exists
     */
    Optional<UserLikeFilm> findByUserAndFilm(User user, Film film);

    /**
     * Find all UserLikeFilm entries for a given user ID using JPQL
     *
     * @param userId the ID of the user
     * @return list of UserLikeFilm objects associated with the user
     */
    @Query("SELECT ulf FROM UserLikeFilm ulf WHERE ulf.user.userId = :userId")
    List<UserLikeFilm> findByUserId(@Param("userId") Long userId);

    /**
     * Find all UserLikeFilm entries for a given film ID using JPQL
     *
     * @param filmId the ID of the film
     * @return list of UserLikeFilm objects associated with the film
     */
    @Query("SELECT ulf FROM UserLikeFilm ulf WHERE ulf.film.filmId = :filmId")
    List<UserLikeFilm> findByFilmId(@Param("filmId") Long filmId);

    /**
     * Find a specific UserLikeFilm entry by user ID and film ID using JPQL
     *
     * @param userId the ID of the user
     * @param filmId the ID of the film
     * @return the UserLikeFilm object if it exists
     */
    @Query("SELECT ulf FROM UserLikeFilm ulf WHERE ulf.user.userId = :userId AND ulf.film.filmId = :filmId")
    Optional<UserLikeFilm> findByUserIdAndFilmId(@Param("userId") Long userId, @Param("filmId") Long filmId);
}