package Se2.MovieTicket.controllers;

import Se2.MovieTicket.model.Film;
import Se2.MovieTicket.model.FilmActor;
import Se2.MovieTicket.model.Actor;
import Se2.MovieTicket.repository.FilmActorRepository;
import Se2.MovieTicket.repository.FilmRepository;
import Se2.MovieTicket.repository.ActorRepository;
import Se2.MovieTicket.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/film-actors")
public class FilmActorController {
    @Autowired
    private FilmActorRepository filmActorRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private FilmRepository filmRepository;

    @Autowired
    private ActorRepository actorRepository;

    @GetMapping
    public ResponseEntity<List<FilmActor>> getAllFilmActors() {
        if (!userService.hasRole("Admin") && !userService.hasRole("User ")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        List<FilmActor> filmActors = filmActorRepository.findAll();
        return filmActors.isEmpty() ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(filmActors, HttpStatus.OK);
    }

    @GetMapping("/film/{filmId}")
    public ResponseEntity<List<FilmActor>> getActorsByFilm(@PathVariable("filmId") Long filmId) {
        if (!userService.hasRole("Admin") && !userService.hasRole("User ")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        Optional<Film> film = filmRepository.findById(filmId);
        if (film.isPresent()) {
            List<FilmActor> filmActors = filmActorRepository.findByFilm(film.get());
            return filmActors.isEmpty() ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(filmActors, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/actor/{actorId}")
    public ResponseEntity<List<FilmActor>> getFilmsByActor(@PathVariable("actorId") Long actorId) {
        if (!userService.hasRole("Admin") && !userService.hasRole("User ")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        Optional<Actor> actor = actorRepository.findById(actorId);
        if (actor.isPresent()) {
            List<FilmActor> filmActors = filmActorRepository.findByActor(actor.get());
            return filmActors.isEmpty() ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(filmActors, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping
    public ResponseEntity<FilmActor> createFilmActor(@RequestBody FilmActor filmActor) {
        if (!userService.hasRole("Admin")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        try {
            FilmActor newFilmActor = filmActorRepository.save(filmActor);
            return new ResponseEntity<>(newFilmActor, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{filmId}/{actorId}")
    public ResponseEntity<HttpStatus> deleteFilmActor(@PathVariable("filmId") Long filmId, @PathVariable("actorId") Long actorId) {
        if (!userService.hasRole("Admin")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        Optional<Film> film = filmRepository.findById(filmId);
        Optional<Actor> actor = actorRepository.findById(actorId);
        if (film.isPresent() && actor.isPresent()) {
            Optional<FilmActor> filmActor = filmActorRepository.findByFilmAndActor(film.get(), actor.get());
            filmActor.ifPresent(filmActorRepository::delete);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}