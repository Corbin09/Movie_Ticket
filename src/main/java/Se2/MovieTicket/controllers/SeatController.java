package Se2.MovieTicket.controllers;

import Se2.MovieTicket.dto.SeatDTO;
import Se2.MovieTicket.model.Seat;
import Se2.MovieTicket.service.SeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seats")
public class SeatController {
    @Autowired
    private SeatService seatService;

    @GetMapping
    public ResponseEntity<List<Seat>> getAllSeats() {
        List<Seat> seats = seatService.getAllSeats();
        return seats.isEmpty() ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(seats, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Seat> getSeatById(@PathVariable("id") Long id) {
        return seatService.getSeatById(id)
                .map(seat -> new ResponseEntity<>(seat, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<Seat> createSeat(@RequestBody SeatDTO seatDTO) {
        Seat newSeat = seatService.createSeat(seatDTO);
        return new ResponseEntity<>(newSeat, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Seat> updateSeat(@PathVariable("id") Long id, @RequestBody SeatDTO seatDTO) {
        Seat updatedSeat = seatService.updateSeat(id, seatDTO);
        return updatedSeat != null ? new ResponseEntity<>(updatedSeat, HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteSeat(@PathVariable("id") Long id) {
        seatService.deleteSeat(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}