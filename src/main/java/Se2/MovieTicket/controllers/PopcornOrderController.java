package Se2.MovieTicket.controllers;

import Se2.MovieTicket.dto.PopcornOrderDTO;
import Se2.MovieTicket.model.PopcornOrder;
import Se2.MovieTicket.service.PopcornOrderService;
import Se2.MovieTicket.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/popcorn-orders")
public class PopcornOrderController {
    @Autowired
    private PopcornOrderService popcornOrderService;

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<PopcornOrder>> getAllPopcornOrders() {
        if (!userService.hasRole("Admin") && !userService.hasRole("User  ")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        List<PopcornOrder> popcornOrders = popcornOrderService.getAllPopcornOrders();
        return popcornOrders.isEmpty() ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(popcornOrders, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PopcornOrder> getPopcornOrderById(@PathVariable("id") Long id) {
        if (!userService.hasRole("Admin") && !userService.hasRole("User  ")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return popcornOrderService.getPopcornOrderById(id)
                .map(popcornOrder -> new ResponseEntity<>(popcornOrder, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<PopcornOrder> createPopcornOrder(@RequestBody PopcornOrderDTO popcornOrderDTO) {
        if (!userService.hasRole("Admin")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        PopcornOrder newPopcornOrder = popcornOrderService.createPopcornOrder(popcornOrderDTO);
        return new ResponseEntity<>(newPopcornOrder, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PopcornOrder> updatePopcornOrder(@PathVariable("id") Long id, @RequestBody PopcornOrderDTO popcornOrderDTO) {
        if (!userService.hasRole("Admin")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        PopcornOrder updatedPopcornOrder = popcornOrderService.updatePopcornOrder(id, popcornOrderDTO);
        return updatedPopcornOrder != null ? new ResponseEntity<>(updatedPopcornOrder, HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deletePopcornOrder(@PathVariable("id") Long id) {
        if (!userService.hasRole("Admin")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        popcornOrderService.deletePopcornOrder(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}