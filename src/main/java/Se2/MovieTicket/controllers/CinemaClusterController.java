package Se2.MovieTicket.controllers;

import Se2.MovieTicket.dto.CinemaClusterDTO;
import Se2.MovieTicket.model.CinemaCluster;
import Se2.MovieTicket.service.CinemaClusterService;
import Se2.MovieTicket.service.UserService;
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

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<CinemaCluster>> getAllCinemaClusters() {
        if (!userService.hasRole("Admin") && !userService.hasRole("User ")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        List<CinemaCluster> cinemaClusters = cinemaClusterService.getAllCinemaClusters();
        return cinemaClusters.isEmpty() ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(cinemaClusters, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CinemaCluster> getCinemaClusterById(@PathVariable("id") Long id) {
        if (!userService.hasRole("Admin") && !userService.hasRole("User ")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return cinemaClusterService.getCinemaClusterById(id)
                .map(cinemaCluster -> new ResponseEntity<>(cinemaCluster, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<CinemaCluster> createCinemaCluster(@RequestBody CinemaClusterDTO cinemaClusterDTO) {
        if (!userService.hasRole("Admin")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        CinemaCluster newCinemaCluster = cinemaClusterService.createCinemaCluster(cinemaClusterDTO);
        return new ResponseEntity<>(newCinemaCluster, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CinemaCluster> updateCinemaCluster(@PathVariable("id") Long id, @RequestBody CinemaClusterDTO cinemaClusterDTO) {
        if (!userService.hasRole("Admin")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        CinemaCluster updatedCinemaCluster = cinemaClusterService.updateCinemaCluster(id, cinemaClusterDTO);
        return updatedCinemaCluster != null ? new ResponseEntity<>(updatedCinemaCluster, HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteCinemaCluster(@PathVariable("id") Long id) {
        if (!userService.hasRole("Admin")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        cinemaClusterService.deleteCinemaCluster(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/filter")
    public ResponseEntity<List<CinemaCluster>> filterCinemaClusters(@RequestParam(required = false) String name) {
        List<CinemaCluster> clusters = cinemaClusterService.filterCinemaClusters(name);
        return new ResponseEntity<>(clusters, HttpStatus.OK);
    }
}