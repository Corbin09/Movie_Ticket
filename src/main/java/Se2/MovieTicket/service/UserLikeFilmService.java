package Se2.MovieTicket.service;

import Se2.MovieTicket.model.Film;
import Se2.MovieTicket.model.User;
import Se2.MovieTicket.model.UserLikeFilm;
import Se2.MovieTicket.model.UserLikeFilmId;
import Se2.MovieTicket.repository.FilmRepository;
import Se2.MovieTicket.repository.UserLikeFilmRepository;
import Se2.MovieTicket.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserLikeFilmService {

    @Autowired
    private UserLikeFilmRepository userLikeFilmRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FilmRepository filmRepository;

    /**
     * Get all films liked by a specific user
     *
     * @param userId the ID of the user
     * @return list of films liked by the user
     */
    public List<Film> getFilmsLikedByUser(Long userId) {
        List<UserLikeFilm> userLikes = userLikeFilmRepository.findByUserUserId(userId);
        return userLikes.stream()
                .map(UserLikeFilm::getFilm)
                .collect(Collectors.toList());
    }

    /**
     * Get all users who like a specific film
     *
     * @param filmId the ID of the film
     * @return list of users who like the film
     */
    public List<User> getUsersWhoLikeFilm(Long filmId) {
        List<UserLikeFilm> filmLikes = userLikeFilmRepository.findByFilmFilmId(filmId);
        return filmLikes.stream()
                .map(UserLikeFilm::getUser)
                .collect(Collectors.toList());
    }

    /**
     * Get the count of likes for a specific film
     *
     * @param filmId the ID of the film
     * @return the number of users who like the film
     */
    public long getFilmLikeCount(Long filmId) {
        return userLikeFilmRepository.countByFilmFilmId(filmId);
    }

    /**
     * Check if a user likes a specific film
     *
     * @param userId the ID of the user
     * @param filmId the ID of the film
     * @return true if the user likes the film, false otherwise
     */
    public boolean doesUserLikeFilm(Long userId, Long filmId) {
        return userLikeFilmRepository.existsByUserUserIdAndFilmFilmId(userId, filmId);
    }

    /**
     * Add a like relationship between a user and a film
     *
     * @param userId the ID of the user
     * @param filmId the ID of the film
     * @return the created UserLikeFilm object
     * @throws RuntimeException if user or film not found
     */
    @Transactional
    public UserLikeFilm addUserLikeFilm(Long userId, Long filmId) {
        // Check if already exists
        if (doesUserLikeFilm(userId, filmId)) {
            throw new RuntimeException("User already likes this film");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        Film film = filmRepository.findById(filmId)
                .orElseThrow(() -> new RuntimeException("Film not found with ID: " + filmId));

        UserLikeFilm userLikeFilm = new UserLikeFilm();
        UserLikeFilmId id = new UserLikeFilmId();
        id.setUserId(userId);
        id.setFilmId(filmId);

        userLikeFilm.setId(id);
        userLikeFilm.setUser(user);
        userLikeFilm.setFilm(film);

        return userLikeFilmRepository.save(userLikeFilm);
    }

    /**
     * Remove a like relationship between a user and a film
     *
     * @param userId the ID of the user
     * @param filmId the ID of the film
     * @return true if successfully removed, false if relationship didn't exist
     */
    @Transactional
    public boolean removeUserLikeFilm(Long userId, Long filmId) {
        Optional<UserLikeFilm> userLikeFilm = userLikeFilmRepository.findByUserUserIdAndFilmFilmId(userId, filmId);

        if (userLikeFilm.isPresent()) {
            userLikeFilmRepository.delete(userLikeFilm.get());
            return true;
        }

        return false;
    }



    public boolean hasUserLikedFilm(Long userId, Long filmId) {
        UserLikeFilmId id = new UserLikeFilmId(userId, filmId);
        return userLikeFilmRepository.existsById(id);
    }

    @Transactional
    public boolean toggleUserLikeFilm(Long userId, Long filmId) {

        UserLikeFilmId id = new UserLikeFilmId(userId, filmId);
        boolean currentlyLiked = userLikeFilmRepository.existsById(id);

        if (currentlyLiked) {
            // Unlike the film
            userLikeFilmRepository.deleteById(id);
            return false;
        } else {
            // Like the film
            UserLikeFilm userLikeFilm = new UserLikeFilm();
            userLikeFilm.setId(id);

            // Set user
            Optional<User> userOptional = userRepository.findById(userId);
            if (userOptional.isPresent()) {
                userLikeFilm.setUser(userOptional.get());
            } else {
                throw new IllegalArgumentException("User not found");
            }

            // Set film
            Optional<Film> filmOptional = filmRepository.findById(filmId);
            if (filmOptional.isPresent()) {
                userLikeFilm.setFilm(filmOptional.get());
            } else {
                throw new IllegalArgumentException("Film not found");
            }

            userLikeFilmRepository.save(userLikeFilm);
            return true;
        }
    }

    public int countLikesByFilmId(Long filmId) {
        return userLikeFilmRepository.countByFilmFilmId(filmId);
    }


public boolean checkUserLikedFilm(Long userId, Long filmId) {
    UserLikeFilmId id = new UserLikeFilmId(userId, filmId);
    return userLikeFilmRepository.existsById(id);
}

}
