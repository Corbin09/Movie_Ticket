package Se2.MovieTicket.controllers;


import Se2.MovieTicket.model.FilmDirector;
import Se2.MovieTicket.model.FilmDirectorId;
import Se2.MovieTicket.repository.FilmDirectorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/film-directors")
public class FilmDirectorController {

    @Autowired
    private FilmDirectorRepository filmDirectorRepository;

    @GetMapping
    public ResponseEntity<List<FilmDirector>> getAllFilmDirectors() {
        try {
            List<FilmDirector> filmDirectors = new ArrayList<>(filmDirectorRepository.findAll());

            if (filmDirectors.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(filmDirectors, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{filmId}/{directorId}")
    public ResponseEntity<FilmDirector> getFilmDirectorById(@PathVariable("filmId") Long filmId, @PathVariable("directorId") Long directorId) {
        Optional<FilmDirector> filmDirectorData = filmDirectorRepository.findByFilmIdAndDirectorId(filmId, directorId);

        return filmDirectorData.map(filmDirector -> new ResponseEntity<>(filmDirector, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/film/{filmId}")
    public ResponseEntity<List<FilmDirector>> getDirectorsByFilmId(@PathVariable("filmId") Long filmId) {
        try {
            List<FilmDirector> filmDirectors = filmDirectorRepository.findByFilmId(filmId);

            if (filmDirectors.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(filmDirectors, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/director/{directorId}")
    public ResponseEntity<List<FilmDirector>> getFilmsByDirectorId(@PathVariable("directorId") Long directorId) {
        try {
            List<FilmDirector> filmDirectors = filmDirectorRepository.findByDirectorId(directorId);

            if (filmDirectors.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(filmDirectors, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping
    public ResponseEntity<FilmDirector> createFilmDirector(@RequestBody FilmDirector filmDirector) {
        try {
            FilmDirector newFilmDirector = filmDirectorRepository.save(filmDirector);
            return new ResponseEntity<>(newFilmDirector, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{filmId}/{directorId}")
    public ResponseEntity<HttpStatus> deleteFilmDirector(
            @PathVariable("filmId") Long filmId,
            @PathVariable("directorId") Long directorId) {
        try {
            FilmDirectorId filmDirectorId = new FilmDirectorId(filmId, directorId);
            Optional<FilmDirector> filmDirector = filmDirectorRepository.findById(filmDirectorId);

            if (filmDirector.isPresent()) {
                filmDirectorRepository.delete(filmDirector.get());
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
