package Se2.MovieTicket.controllers;

import Se2.MovieTicket.dto.OrderDTO;
import Se2.MovieTicket.model.Order;
import Se2.MovieTicket.service.OrderService;
import Se2.MovieTicket.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        if (!userService.hasRole("Admin") && !userService.hasRole("User ")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        List<Order> orders = orderService.getAllOrders();
        return orders.isEmpty() ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(orders, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable("id") Long id) {
        if (!userService.hasRole("Admin") && !userService.hasRole("User  ")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return orderService.getOrderById(id)
                .map(order -> new ResponseEntity<>(order, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody OrderDTO orderDTO) {
        if (!userService.hasRole("Admin")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        Order newOrder = orderService.createOrder(orderDTO);
        return new ResponseEntity<>(newOrder, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Order> updateOrder(@PathVariable("id") Long id, @RequestBody OrderDTO orderDTO) {
        if (!userService.hasRole("Admin")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        Order updatedOrder = orderService.updateOrder(id, orderDTO);
        return updatedOrder != null ? new ResponseEntity<>(updatedOrder, HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteOrder(@PathVariable("id") Long id) {
        if (!userService.hasRole("Admin")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        orderService.deleteOrder(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}