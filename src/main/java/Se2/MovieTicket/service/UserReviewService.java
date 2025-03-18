package Se2.MovieTicket.service;

import Se2.MovieTicket.dto.UserReviewDTO;
import Se2.MovieTicket.model.UserReview;
import Se2.MovieTicket.model.UserReviewId;
import Se2.MovieTicket.repository.UserReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserReviewService {
    @Autowired
    private UserReviewRepository userReviewRepository;

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
}