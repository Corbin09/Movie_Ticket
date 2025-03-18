package Se2.MovieTicket.controllers;

import Se2.MovieTicket.dto.DirectorDTO;
import Se2.MovieTicket.model.Director;
import Se2.MovieTicket.service.DirectorService;
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

    @GetMapping
    public ResponseEntity<List<Director>> getAllDirectors() {
        List<Director> directors = directorService.getAllDirectors();
        return directors.isEmpty() ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(directors, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Director> getDirectorById(@PathVariable("id") Long id) {
        return directorService.getDirectorById(id)
                .map(director -> new ResponseEntity<>(director, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<Director> createDirector(@RequestBody DirectorDTO directorDTO) {
        Director newDirector = directorService.createDirector(directorDTO);
        return new ResponseEntity<>(newDirector, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Director> updateDirector(@PathVariable("id") Long id, @RequestBody DirectorDTO directorDTO) {
        Director updatedDirector = directorService.updateDirector(id, directorDTO);
        return updatedDirector != null ? new ResponseEntity<>(updatedDirector, HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteDirector(@PathVariable("id") Long id) {
        directorService.deleteDirector(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}