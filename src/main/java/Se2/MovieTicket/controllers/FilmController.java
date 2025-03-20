package Se2.MovieTicket.controllers;

import Se2.MovieTicket.dto.FilmDTO;
import Se2.MovieTicket.model.Film;
import Se2.MovieTicket.service.FilmService;
import Se2.MovieTicket.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/films")
public class FilmController {
    @Autowired
    private FilmService filmService;

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<Film>> getAllFilms() {
        if (!userService.hasRole("Admin") && !userService.hasRole("User ")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        List<Film> films = filmService.getAllFilms();
        return films.isEmpty() ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(films, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Film> getFilmById(@PathVariable("id") Long id) {
        if (!userService.hasRole("Admin") && !userService.hasRole("User ")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return filmService.getFilmById(id)
                .map(film -> new ResponseEntity<>(film, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<Film> createFilm(@RequestBody FilmDTO filmDTO) {
        if (!userService.hasRole("Admin")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        Film newFilm = filmService.createFilm(filmDTO);
        return new ResponseEntity<>(newFilm, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Film> updateFilm(@PathVariable("id") Long id, @RequestBody FilmDTO filmDTO) {
        if (!userService.hasRole("Admin")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        Film updatedFilm = filmService.updateFilm(id, filmDTO);
        return updatedFilm != null ? new ResponseEntity<>(updatedFilm, HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteFilm(@PathVariable("id") Long id) {
        if (!userService.hasRole("Admin")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        filmService.deleteFilm(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Film>> searchFilms(@RequestParam(required = false) String name)  {
        List<Film> films = filmService.searchFilms(name);
        return films.isEmpty() ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(films, HttpStatus.OK);
    }

    @GetMapping("/filter")
    public ResponseEntity<List<Film>> filterFilms(@RequestParam(required = false) String name,
                                                  @RequestParam(required = false) Date releaseDate,
                                                  @RequestParam(required = false) String country,
                                                  @RequestParam(required = false) String type,
                                                  @RequestParam(required = false) Integer age) {
        List<Film> films = filmService.filterFilms(name, releaseDate, country, type, age);
        return new ResponseEntity<>(films, HttpStatus.OK);
    }
}