package Se2.MovieTicket.controllers;

import Se2.MovieTicket.model.UserLikeFilm;
import Se2.MovieTicket.model.UserLikeFilmId;
import Se2.MovieTicket.repository.UserLikeFilmRepository;
import Se2.MovieTicket.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/user-likes")
public class UserLikeFilmController {
    @Autowired
    private UserLikeFilmRepository userLikeFilmRepository;

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<UserLikeFilm>> getAllUserLikes() {
        if (!userService.hasRole("Admin") && !userService.hasRole("User  ")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        List<UserLikeFilm> userLikes = userLikeFilmRepository.findAll();
        return new ResponseEntity<>(userLikes, HttpStatus.OK);
    }

    @GetMapping("/{userId}/{filmId}")
    public ResponseEntity<UserLikeFilm> getUserLikeById(@PathVariable("userId") Long userId, @PathVariable("filmId") Long filmId) {
        if (!userService.hasRole("Admin") && !userService.hasRole("User  ")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        UserLikeFilmId userLikeFilmId = new UserLikeFilmId();
        userLikeFilmId.setUserId(userId);
        userLikeFilmId.setFilmId(filmId);
        Optional<UserLikeFilm> userLikeData = userLikeFilmRepository.findById(userLikeFilmId);
        return userLikeData.map(userLikeFilm -> new ResponseEntity<>(userLikeFilm, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserLikeFilm>> getUserLikesByUserId(@PathVariable("userId") Long userId) {
        if (!userService.hasRole("Admin") && !userService.hasRole("User  ")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        List<UserLikeFilm> userLikes = userLikeFilmRepository.findByUserId(userId);
        return new ResponseEntity<>(userLikes, HttpStatus.OK);
    }

    @GetMapping("/film/{filmId}")
    public ResponseEntity<List<UserLikeFilm>> getUserLikesByFilmId(@PathVariable("filmId") Long filmId) {
        if (!userService.hasRole("Admin") && !userService.hasRole("User  ")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        List<UserLikeFilm> userLikes = userLikeFilmRepository.findByFilmId(filmId);
        return new ResponseEntity<>(userLikes, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<UserLikeFilm> createUserLike(@RequestBody UserLikeFilm userLikeFilm) {
        if (!userService.hasRole("Admin") && !userService.hasRole("User  ")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        try {
            UserLikeFilm _userLikeFilm = userLikeFilmRepository.save(userLikeFilm);
            return new ResponseEntity<>(_userLikeFilm, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{userId}/{filmId}")
    public ResponseEntity<HttpStatus> deleteUserLike(@PathVariable("userId") Long userId, @PathVariable("filmId") Long filmId) {
        if (!userService.hasRole("Admin") && !userService.hasRole("User  ")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        try {
            UserLikeFilmId userLikeFilmId = new UserLikeFilmId();
            userLikeFilmId.setUserId(userId);
            userLikeFilmId.setFilmId(filmId);
            userLikeFilmRepository.deleteById(userLikeFilmId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/user/{userId}/film/{filmId}")
    public ResponseEntity<HttpStatus> deleteUserLikeByUserIdAndFilmId(@PathVariable("userId") Long userId, @PathVariable("filmId") Long filmId) {
        if (!userService.hasRole("Admin") && !userService.hasRole("User  ")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        try {
            Optional<UserLikeFilm> userLike = userLikeFilmRepository.findByUserIdAndFilmId(userId, filmId);
            if (userLike.isPresent()) {
                userLikeFilmRepository.delete(userLike.get());
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}