package Se2.MovieTicket.controllers;

import Se2.MovieTicket.dto.CinemaDTO;
import Se2.MovieTicket.model.Cinema;
import Se2.MovieTicket.service.CinemaService;
import Se2.MovieTicket.service.UserService;
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

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<Cinema>> getAllCinemas() {
        if (!userService.hasRole("Admin") && !userService.hasRole("User ")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        List<Cinema> cinemas = cinemaService.getAllCinemas();
        return cinemas.isEmpty() ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(cinemas, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cinema> getCinemaById(@PathVariable("id") Long id) {
        if (!userService.hasRole("Admin") && !userService.hasRole("User ")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return cinemaService.getCinemaById(id)
                .map(cinema -> new ResponseEntity<>(cinema, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<Cinema> createCinema(@RequestBody CinemaDTO cinemaDTO) {
        if (!userService.hasRole("Admin")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        Cinema newCinema = cinemaService.createCinema(cinemaDTO);
        return new ResponseEntity<>(newCinema, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cinema> updateCinema(@PathVariable("id") Long id, @RequestBody CinemaDTO cinemaDTO) {
        if (!userService.hasRole("Admin")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        Cinema updatedCinema = cinemaService.updateCinema(id, cinemaDTO);
        return updatedCinema != null ? new ResponseEntity<>(updatedCinema, HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteCinema(@PathVariable("id") Long id) {
        if (!userService.hasRole("Admin")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        cinemaService.deleteCinema(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Cinema>> searchCinemas(@RequestParam(required = false) String name, @RequestParam(required = false) String address) {
        List<Cinema> cinemas = cinemaService.searchCinemas(name, address);
        return cinemas.isEmpty() ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(cinemas, HttpStatus.OK);
    }

    @GetMapping("/filter")
    public ResponseEntity<List<Cinema>> filterCinemas(@RequestParam(required = false) String name, @RequestParam(required = false) String address) {
        List<Cinema> cinemas = cinemaService.filterCinemas(name, address);
        return new ResponseEntity<>(cinemas, HttpStatus.OK);
    }
}