package Se2.MovieTicket.controllers;

import Se2.MovieTicket.dto.CinemaDTO;
import Se2.MovieTicket.model.Cinema;
import Se2.MovieTicket.service.CinemaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cinemas")
public class CinemaController {
    @Autowired
    private CinemaService cinemaService;

    @GetMapping
    public ResponseEntity<List<Cinema>> getAllCinemas() {
        List<Cinema> cinemas = cinemaService.getAllCinemas();
        return cinemas.isEmpty() ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(cinemas, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cinema> getCinemaById(@PathVariable("id") Long id) {
        return cinemaService.getCinemaById(id)
                .map(cinema -> new ResponseEntity<>(cinema, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<Cinema> createCinema(@RequestBody CinemaDTO cinemaDTO) {
        Cinema newCinema = cinemaService.createCinema(cinemaDTO);
        return new ResponseEntity<>(newCinema, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cinema> updateCinema(@PathVariable("id") Long id, @RequestBody CinemaDTO cinemaDTO) {
        Cinema updatedCinema = cinemaService.updateCinema(id, cinemaDTO);
        return updatedCinema != null ? new ResponseEntity<>(updatedCinema, HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteCinema(@PathVariable("id") Long id) {
        cinemaService.deleteCinema(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}