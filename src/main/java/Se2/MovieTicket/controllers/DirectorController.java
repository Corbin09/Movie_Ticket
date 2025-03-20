package Se2.MovieTicket.controllers;

import Se2.MovieTicket.dto.DirectorDTO;
import Se2.MovieTicket.model.Director;
import Se2.MovieTicket.service.DirectorService;
import Se2.MovieTicket.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/directors")
public class DirectorController {
    @Autowired
    private DirectorService directorService;

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<Director>> getAllDirectors() {
        if (!userService.hasRole("Admin") && !userService.hasRole("User ")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        List<Director> directors = directorService.getAllDirectors();
        return directors.isEmpty() ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(directors, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Director> getDirectorById(@PathVariable("id") Long id) {
        if (!userService.hasRole("Admin") && !userService.hasRole("User ")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return directorService.getDirectorById(id)
                .map(director -> new ResponseEntity<>(director, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<Director> createDirector(@RequestBody DirectorDTO directorDTO) {
        if (!userService.hasRole("Admin")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        Director newDirector = directorService.createDirector(directorDTO);
        return new ResponseEntity<>(newDirector, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Director> updateDirector(@PathVariable("id") Long id, @RequestBody DirectorDTO directorDTO) {
        if (!userService.hasRole("Admin")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        Director updatedDirector = directorService.updateDirector(id, directorDTO);
        return updatedDirector != null ? new ResponseEntity<>(updatedDirector, HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteDirector(@PathVariable("id") Long id) {
        if (!userService.hasRole("Admin")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        directorService.deleteDirector(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Director>> searchDirectors(@RequestParam(required = false) String name) {
        List<Director> directors = directorService.searchDirectors(name);
        return directors.isEmpty() ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(directors, HttpStatus.OK);
    }

    @GetMapping("/filter")
    public ResponseEntity<List<Director>> filterDirectors(@RequestParam(required = false) String name) {
        List<Director> directors = directorService.filterDirectors(name);
        return new ResponseEntity<>(directors, HttpStatus.OK);
    }
}