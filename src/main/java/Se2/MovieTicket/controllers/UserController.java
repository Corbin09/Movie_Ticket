package Se2.MovieTicket.controllers;

import Se2.MovieTicket.dto.UserDTO;
import Se2.MovieTicket.model.User;
import Se2.MovieTicket.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return users.isEmpty() ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable("id") Long id) {
        return userService.getUserById(id)
                .map(user -> new ResponseEntity<>(user, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<User> createUser (@RequestBody UserDTO userDTO) {
        User newUser  = userService.createUser (userDTO);
        return new ResponseEntity<>(newUser , HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser (@PathVariable("id") Long id, @RequestBody UserDTO userDTO) {
        User updatedUser  = userService.updateUser (id, userDTO);
        return updatedUser  != null ? new ResponseEntity<>(updatedUser , HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteUser (@PathVariable("id") Long id) {
        userService.deleteUser (id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}