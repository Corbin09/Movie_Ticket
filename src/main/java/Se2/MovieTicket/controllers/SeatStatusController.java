package Se2.MovieTicket.controllers;

import Se2.MovieTicket.dto.SeatStatusDTO;
import Se2.MovieTicket.model.SeatStatus;
import Se2.MovieTicket.service.SeatStatusService;
import Se2.MovieTicket.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seat-statuses")
public class SeatStatusController {
    @Autowired
    private SeatStatusService seatStatusService;

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<SeatStatus>> getAllSeatStatuses() {
        if (!userService.hasRole("Admin") && !userService.hasRole("User  ")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        List<SeatStatus> seatStatuses = seatStatusService.getAllSeatStatuses();
        return seatStatuses.isEmpty() ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(seatStatuses, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SeatStatus> getSeatStatusById(@PathVariable("id") Long id) {
        if (!userService.hasRole("Admin") && !userService.hasRole("User  ")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return seatStatusService.getSeatStatusById(id)
                .map(seatStatus -> new ResponseEntity<>(seatStatus, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<SeatStatus> createSeatStatus(@RequestBody SeatStatusDTO seatStatusDTO) {
        if (!userService.hasRole("Admin")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        SeatStatus newSeatStatus = seatStatusService.createSeatStatus(seatStatusDTO);
        return new ResponseEntity<>(newSeatStatus, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SeatStatus> updateSeatStatus(@PathVariable("id") Long id, @RequestBody SeatStatusDTO seatStatusDTO) {
        if (!userService.hasRole("Admin")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        SeatStatus updatedSeatStatus = seatStatusService.updateSeatStatus(id, seatStatusDTO);
        return updatedSeatStatus != null ? new ResponseEntity<>(updatedSeatStatus, HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteSeatStatus(@PathVariable("id") Long id) {
        if (!userService.hasRole("Admin")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        seatStatusService.deleteSeatStatus(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}