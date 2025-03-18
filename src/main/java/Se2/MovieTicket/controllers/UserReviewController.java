package Se2.MovieTicket.controllers;

import Se2.MovieTicket.dto.UserReviewDTO;
import Se2.MovieTicket.model.UserReview;
import Se2.MovieTicket.service.UserReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user-reviews")
public class UserReviewController {
    @Autowired
    private UserReviewService userReviewService;

    @GetMapping
    public ResponseEntity<List<UserReview>> getAllUserReviews() {
        List<UserReview> userReviews = userReviewService.getAllUserReviews();
        return userReviews.isEmpty() ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(userReviews, HttpStatus.OK);
    }

    @GetMapping("/{userId}/{filmId}")
    public ResponseEntity<UserReview> getUserReviewById(@PathVariable("userId") Long userId, @PathVariable("filmId") Long filmId) {
        return userReviewService.getUserReviewById(userId, filmId)
                .map(userReview -> new ResponseEntity<>(userReview, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<UserReview> createUserReview(@RequestBody UserReviewDTO userReviewDTO) {
        UserReview newUserReview = userReviewService.createUserReview(userReviewDTO);
        return new ResponseEntity<>(newUserReview, HttpStatus.CREATED);
    }

    @PutMapping("/{userId}/{filmId}")
    public ResponseEntity<UserReview> updateUserReview(@PathVariable("userId") Long userId, @PathVariable("filmId") Long filmId, @RequestBody UserReviewDTO userReviewDTO) {
        UserReview updatedUserReview = userReviewService.updateUserReview(userId, filmId, userReviewDTO);
        return updatedUserReview != null ? new ResponseEntity<>(updatedUserReview, HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{userId}/{filmId}")
    public ResponseEntity<HttpStatus> deleteUserReview(@PathVariable("userId") Long userId, @PathVariable("filmId") Long filmId) {
        userReviewService.deleteUserReview(userId, filmId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}