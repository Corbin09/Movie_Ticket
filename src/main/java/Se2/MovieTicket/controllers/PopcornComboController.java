package Se2.MovieTicket.controllers;

import Se2.MovieTicket.dto.PopcornComboDTO;
import Se2.MovieTicket.model.PopcornCombo;
import Se2.MovieTicket.service.PopcornComboService;
import Se2.MovieTicket.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/popcorn-combos")
public class PopcornComboController {
    @Autowired
    private PopcornComboService popcornComboService;

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<PopcornCombo>> getAllPopcornCombos() {
        if (!userService.hasRole("Admin") && !userService.hasRole("User  ")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        List<PopcornCombo> popcornCombos = popcornComboService.getAllPopcornCombos();
        return popcornCombos.isEmpty() ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(popcornCombos, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PopcornCombo> getPopcornComboById(@PathVariable("id") Long id) {
        if (!userService.hasRole("Admin") && !userService.hasRole("User  ")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return popcornComboService.getPopcornComboById(id)
                .map(popcornCombo -> new ResponseEntity<>(popcornCombo, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<PopcornCombo> createPopcornCombo(@RequestBody PopcornComboDTO popcornComboDTO) {
        if (!userService.hasRole("Admin")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        PopcornCombo newPopcornCombo = popcornComboService.createPopcornCombo(popcornComboDTO);
        return new ResponseEntity<>(newPopcornCombo, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PopcornCombo> updatePopcornCombo(@PathVariable("id") Long id, @RequestBody PopcornComboDTO popcornComboDTO) {
        if (!userService.hasRole("Admin")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        PopcornCombo updatedPopcornCombo = popcornComboService.updatePopcornCombo(id, popcornComboDTO);
        return updatedPopcornCombo != null ? new ResponseEntity<>(updatedPopcornCombo, HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deletePopcornCombo(@PathVariable("id") Long id) {
        if (!userService.hasRole("Admin")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        popcornComboService.deletePopcornCombo(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}