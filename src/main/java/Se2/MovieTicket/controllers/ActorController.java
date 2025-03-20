package Se2.MovieTicket.controllers;

import Se2.MovieTicket.dto.ActorDTO;
import Se2.MovieTicket.model.Actor;
import Se2.MovieTicket.service.ActorService;
import Se2.MovieTicket.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/actors")
public class ActorController {
    @Autowired
    private ActorService actorService;

    @Autowired
    private UserService userService; // Giả sử có UserService để kiểm tra quyền

    @GetMapping
    public ResponseEntity<List<Actor>> getAllActors() {
        if (!userService.hasRole("Admin") && !userService.hasRole("User ")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        List<Actor> actors = actorService.getAllActors();
        return actors.isEmpty() ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(actors, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Actor> getActorById(@PathVariable("id") Long id) {
        if (!userService.hasRole("Admin") && !userService.hasRole("User ")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return actorService.getActorById(id)
                .map(actor -> new ResponseEntity<>(actor, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<Actor> createActor(@RequestBody ActorDTO actorDTO) {
        if (!userService.hasRole("Admin")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        Actor newActor = actorService.createActor(actorDTO);
        return new ResponseEntity<>(newActor, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Actor> updateActor(@PathVariable("id") Long id, @RequestBody ActorDTO actorDTO) {
        if (!userService.hasRole("Admin")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        Actor updatedActor = actorService.updateActor(id, actorDTO);
        return updatedActor != null ? new ResponseEntity<>(updatedActor, HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteActor(@PathVariable("id") Long id) {
        if (!userService.hasRole("Admin")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        actorService.deleteActor(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Actor>> searchActors(@RequestParam(required = false) String name, @RequestParam(required = false) Long id) {
        List<Actor> actors = actorService.searchActors(name, id);
        return actors.isEmpty() ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(actors, HttpStatus.OK);
    }

    @GetMapping("/filter")
    public ResponseEntity<List<Actor>> filterActors(@RequestParam(required = false) String name) {
        List<Actor> actors = actorService.filterActors(name);
        return new ResponseEntity<>(actors, HttpStatus.OK);
    }


}