package Se2.MovieTicket.controllers;

import Se2.MovieTicket.model.FilmRating;
import Se2.MovieTicket.repository.FilmRatingRepository;
import Se2.MovieTicket.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/film-ratings")
public class FilmRatingController {
    @Autowired
    private FilmRatingRepository filmRatingRepository;

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<FilmRating>> getAllFilmRatings() {
        if (!userService.hasRole("Admin") && !userService.hasRole("User ")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        List<FilmRating> filmRatings = filmRatingRepository.findAll();
        return new ResponseEntity<>(filmRatings, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FilmRating> getFilmRatingById(@PathVariable("id") Long id) {
        if (!userService.hasRole("Admin") && !userService.hasRole("User ")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        Optional<FilmRating> filmRatingData = filmRatingRepository.findById(id);
        if (filmRatingData.isPresent()) {
            return new ResponseEntity<>(filmRatingData.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/film/{filmId}")
    public ResponseEntity<FilmRating> getFilmRatingByFilmId(@PathVariable("filmId") Long filmId) {
        if (!userService.hasRole("Admin") && !userService.hasRole("User ")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        Optional<FilmRating> filmRating = filmRatingRepository.findByFilmId(filmId);
        if (filmRating.isPresent()) {
            return new ResponseEntity<>(filmRating.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    public ResponseEntity<FilmRating> createFilmRating(@RequestBody FilmRating filmRating) {
        if (!userService.hasRole("Admin")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        try {
            FilmRating _filmRating = filmRatingRepository.save(filmRating);
            return new ResponseEntity<>(_filmRating, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<FilmRating> updateFilmRating(@PathVariable("id") Long id, @RequestBody FilmRating filmRating) {
        if (!userService.hasRole("Admin")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        Optional<FilmRating> filmRatingData = filmRatingRepository.findById(id);
        if (filmRatingData.isPresent()) {
            FilmRating _filmRating = filmRatingData.get();
            _filmRating.setFilmId(filmRating.getFilmId());
            _filmRating.setFilmRate(filmRating.getFilmRate());
            _filmRating.setSumRate(filmRating.getSumRate());
            _filmRating.setSumStar(filmRating.getSumStar());
            return new ResponseEntity<>(filmRatingRepository.save(_filmRating), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteFilmRating(@PathVariable("id") Long id) {
        if (!userService.hasRole("Admin")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        try {
            filmRatingRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}