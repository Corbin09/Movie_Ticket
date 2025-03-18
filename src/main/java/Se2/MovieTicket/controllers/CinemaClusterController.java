package Se2.MovieTicket.controllers;

import Se2.MovieTicket.dto.CinemaClusterDTO;
import Se2.MovieTicket.model.CinemaCluster;
import Se2.MovieTicket.service.CinemaClusterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cinema-clusters")
public class CinemaClusterController {
    @Autowired
    private CinemaClusterService cinemaClusterService;

    @GetMapping
    public ResponseEntity<List<CinemaCluster>> getAllCinemaClusters() {
        List<CinemaCluster> cinemaClusters = cinemaClusterService.getAllCinemaClusters();
        return cinemaClusters.isEmpty() ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(cinemaClusters, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CinemaCluster> getCinemaClusterById(@PathVariable("id") Long id) {
        return cinemaClusterService.getCinemaClusterById(id)
                .map(cinemaCluster -> new ResponseEntity<>(cinemaCluster, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<CinemaCluster> createCinemaCluster(@RequestBody CinemaClusterDTO cinemaClusterDTO) {
        CinemaCluster newCinemaCluster = cinemaClusterService.createCinemaCluster(cinemaClusterDTO);
        return new ResponseEntity<>(newCinemaCluster, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CinemaCluster> updateCinemaCluster(@PathVariable("id") Long id, @RequestBody CinemaClusterDTO cinemaClusterDTO) {
        CinemaCluster updatedCinemaCluster = cinemaClusterService.updateCinemaCluster(id, cinemaClusterDTO);
        return updatedCinemaCluster != null ? new ResponseEntity<>(updatedCinemaCluster, HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteCinemaCluster(@PathVariable("id") Long id) {
        cinemaClusterService.deleteCinemaCluster(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}