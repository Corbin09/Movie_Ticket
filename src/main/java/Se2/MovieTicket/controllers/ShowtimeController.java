package Se2.MovieTicket.controllers;

import Se2.MovieTicket.dto.ShowtimeDTO;
import Se2.MovieTicket.model.Showtime;
import Se2.MovieTicket.service.ShowtimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/showtimes")
public class ShowtimeController {
    @Autowired
    private ShowtimeService showtimeService;

    @GetMapping
    public ResponseEntity<List<Showtime>> getAllShowtimes() {
        List<Showtime> showtimes = showtimeService.getAllShowtimes();
        return showtimes.isEmpty() ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(showtimes, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Showtime> getShowtimeById(@PathVariable("id") Long id) {
        return showtimeService.getShowtimeById(id)
                .map(showtime -> new ResponseEntity<>(showtime, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<Showtime> createShowtime(@RequestBody ShowtimeDTO showtimeDTO) {
        Showtime newShowtime = showtimeService.createShowtime(showtimeDTO);
        return new ResponseEntity<>(newShowtime, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Showtime> updateShowtime(@PathVariable("id") Long id, @RequestBody ShowtimeDTO showtimeDTO) {
        Showtime updatedShowtime = showtimeService.updateShowtime(id, showtimeDTO);
        return updatedShowtime != null ? new ResponseEntity<>(updatedShowtime, HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteShowtime(@PathVariable("id") Long id) {
        showtimeService.deleteShowtime(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}