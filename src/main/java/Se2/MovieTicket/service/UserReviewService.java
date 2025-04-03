package Se2.MovieTicket.service;

import Se2.MovieTicket.dto.UserReviewDTO;
import Se2.MovieTicket.model.Film;
import Se2.MovieTicket.model.User;
import Se2.MovieTicket.model.UserReview;
import Se2.MovieTicket.model.UserReviewId;
import Se2.MovieTicket.repository.FilmRepository;
import Se2.MovieTicket.repository.UserRepository;
import Se2.MovieTicket.repository.UserReviewRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class UserReviewService {
    @Autowired
    private UserReviewRepository userReviewRepository;
@Autowired
private UserRepository userRepository;

@Autowired
private FilmRepository filmRepository;



    public List<UserReview> getAllUserReviews() {
        return userReviewRepository.findAll();
    }

    public Optional<UserReview> getUserReviewById(Long userId, Long filmId) {
        return userReviewRepository.findById(new UserReviewId(userId, filmId));
    }

    public UserReview createUserReview(UserReviewDTO userReviewDTO) {
        UserReview userReview = new UserReview();
        userReview.setUserId(userReviewDTO.getUserId());
        userReview.setFilmId(userReviewDTO.getFilmId());
        userReview.setComments(userReviewDTO.getComments());
        userReview.setStar(userReviewDTO.getStar());
        userReview.setDatePosted(userReviewDTO.getDatePosted());
        return userReviewRepository.save(userReview);
    }

    public UserReview updateUserReview(Long userId, Long filmId, UserReviewDTO userReviewDTO) {
        Optional<UserReview> userReviewData = userReviewRepository.findById(new UserReviewId(userId, filmId));
        if (userReviewData.isPresent()) {
            UserReview userReview = userReviewData.get();
            userReview.setComments(userReviewDTO.getComments());
            userReview.setStar(userReviewDTO.getStar());
            userReview.setDatePosted(userReviewDTO.getDatePosted());
            return userReviewRepository.save(userReview);
        }
        return null;
    }

    public void deleteUserReview(Long userId, Long filmId) {
        userReviewRepository.deleteById(new UserReviewId(userId, filmId));
    }


    public List<UserReview> findReviewsByFilmId(Long filmId) {
        return userReviewRepository.findByFilmFilmId(filmId);
    }


    @Transactional
    public UserReview saveOrUpdateUserReview(Long userId, Long filmId, Integer star, String comment) {

        // Validate star rating
        if (star < 1 || star > 5) {
            throw new IllegalArgumentException("Star rating must be between 1 and 5");
        }

        // Create or update review
        UserReviewId id = new UserReviewId(userId, filmId);
        UserReview review;
        Optional<UserReview> existingReview = userReviewRepository.findById(id);

        if (existingReview.isPresent()) {
            review = existingReview.get();
            review.setComments(comment);
            review.setStar(star);
            review.setDatePosted(new Date());
        } else {
            review = new UserReview();
            review.setId(id);

            // Set user
            Optional<User> userOptional = userRepository.findById(userId);
            if (userOptional.isPresent()) {
                review.setUser(userOptional.get());
            } else {
                throw new IllegalArgumentException("User not found");
            }

            // Set film
            Optional<Film> filmOptional = filmRepository.findById(filmId);
            if (filmOptional.isPresent()) {
                review.setFilm(filmOptional.get());
            } else {
                throw new IllegalArgumentException("Film not found");
            }

            review.setComments(comment);
            review.setStar(star);
            review.setDatePosted(new Date());
        }

        return userReviewRepository.save(review);
    }
//
//    @Transactional
//    public void deleteUserReview(Long userId, Long filmId) {
//        UserReviewId id = new UserReviewId(userId, filmId);
//        userReviewRepository.deleteById(id);
//    }



    public List<UserReview> getReviewsByFilmId(Long filmId) {
        return userReviewRepository.findByFilmFilmId(filmId);
    }

    public UserReview getUserReviewByUserIdAndFilmId(Long userId, Long filmId) {
        UserReviewId id = new UserReviewId(userId, filmId);
        return userReviewRepository.findById(id).orElse(null);
    }

    @Transactional
    public UserReview saveOrUpdateReview(Long userId, Long filmId, Integer star, String comment) {
        UserReviewId id = new UserReviewId(userId, filmId);

        // Check if review exists
        UserReview review = userReviewRepository.findById(id).orElse(null);

        if (review == null) {
            // Create new review
            review = new UserReview();
            review.setId(id);
            review.setUser(userRepository.findById(userId).orElse(null));
            review.setFilm(filmRepository.findById(filmId).orElse(null));
        }

        review.setStar(star);
        review.setComments(comment);
        review.setDatePosted(new Date());

        return userReviewRepository.save(review);
    }

    public UserReview findUserReviewByUserAndFilm(Long userId, Long filmId) {
        UserReviewId id = new UserReviewId(userId, filmId);
        return userReviewRepository.findById(id).orElse(null);
    }
}