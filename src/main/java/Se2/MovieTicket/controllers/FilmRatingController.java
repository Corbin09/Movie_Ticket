package Se2.MovieTicket.controllers;

import Se2.MovieTicket.model.FilmRating;
import Se2.MovieTicket.repository.FilmRatingRepository;
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

    @GetMapping
    public ResponseEntity<List<FilmRating>> getAllFilmRatings() {
        List<FilmRating> filmRatings = filmRatingRepository.findAll();
        return new ResponseEntity<>(filmRatings, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FilmRating> getFilmRatingById(@PathVariable("id") Long id) {
        Optional<FilmRating> filmRatingData = filmRatingRepository.findById(id);
        if (filmRatingData.isPresent()) {
            return new ResponseEntity<>(filmRatingData.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/film/{filmId}")
    public ResponseEntity<FilmRating> getFilmRatingByFilmId(@PathVariable("filmId") Long filmId) {
        Optional<FilmRating> filmRating = filmRatingRepository.findByFilmId(filmId);
        if (filmRating.isPresent()) {
            return new ResponseEntity<>(filmRating.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    public ResponseEntity<FilmRating> createFilmRating(@RequestBody FilmRating filmRating) {
        try {
            FilmRating _filmRating = filmRatingRepository.save(filmRating);
            return new ResponseEntity<>(_filmRating, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<FilmRating> updateFilmRating(@PathVariable("id") Long id, @RequestBody FilmRating filmRating) {
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
        try {
            filmRatingRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}