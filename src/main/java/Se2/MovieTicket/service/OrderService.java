package Se2.MovieTicket.service;

import Se2.MovieTicket.dto.OrderDTO;
import Se2.MovieTicket.model.Order;
import Se2.MovieTicket.model.User;
import Se2.MovieTicket.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    public List<Order> getAllOrders() {
        return orderRepository.findAllWithDetails();
    }

    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    public Order createOrder(OrderDTO orderDTO) {
        Order order = new Order();
        order.setUserId(orderDTO.getUserId());
        order.setShowtimeId(orderDTO.getShowtimeId());
        order.setOrderDate(orderDTO.getOrderDate());
        order.setTotalPrice(orderDTO.getTotalPrice());
        return orderRepository.save(order);
    }

    public Order updateOrder(Long id, OrderDTO orderDTO) {
        Optional<Order> orderData = orderRepository.findById(id);
        if (orderData.isPresent()) {
            Order order = orderData.get();
            order.setUserId(orderDTO.getUserId());
            order.setShowtimeId(orderDTO.getShowtimeId());
            order.setOrderDate(orderDTO.getOrderDate());
            order.setTotalPrice(orderDTO.getTotalPrice());
            return orderRepository.save(order);
        }
        return null;
    }

    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }


    public List<Order> searchOrdersByCriteria(String criteria, String query) {
        List<Order> allOrders = orderRepository.findAllWithDetails();

        // If query is empty, return all orders when a criteria is selected
        if (query == null || query.isEmpty()) {
            return allOrders;
        }

        List<Order> filteredOrders = new ArrayList<>();

        switch (criteria.toLowerCase()) {
            case "user":
                // Filter by username
                for (Order order : allOrders) {
                    if (order.getUser() != null &&
                            order.getUser().getUsername() != null &&
                            order.getUser().getUsername().toLowerCase().contains(query.toLowerCase())) {
                        filteredOrders.add(order);
                    }
                }
                break;

            case "movie":
                // Filter by movie name
                for (Order order : allOrders) {
                    if (order.getShowtime() != null &&
                            order.getShowtime().getFilm() != null &&
                            order.getShowtime().getFilm().getFilmName() != null &&
                            order.getShowtime().getFilm().getFilmName().toLowerCase().contains(query.toLowerCase())) {
                        filteredOrders.add(order);
                    }
                }
                break;

            case "theater":
                // Filter by theater name
                for (Order order : allOrders) {
                    if (order.getShowtime() != null &&
                            order.getShowtime().getCinema() != null &&
                            order.getShowtime().getCinema().getCinemaName() != null &&
                            order.getShowtime().getCinema().getCinemaName().toLowerCase().contains(query.toLowerCase())) {
                        filteredOrders.add(order);
                    }
                }
                break;

            case "room":
                // Filter by room number
                for (Order order : allOrders) {
                    if (order.getShowtime() != null &&
                            order.getShowtime().getRoom() != null &&
                            order.getShowtime().getRoom().getRoomName() != null &&
                            order.getShowtime().getRoom().getRoomName().toLowerCase().contains(query.toLowerCase())) {
                        filteredOrders.add(order);
                    }
                }
                break;

            case "showdate":
                // Filter by show date (needs date validation)
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    Date searchDate = dateFormat.parse(query);

                    for (Order order : allOrders) {
                        if (order.getShowtime() != null &&
                                order.getShowtime().getShowDate() != null) {

                            Date showDate = order.getShowtime().getShowDate();
                            Calendar cal1 = Calendar.getInstance();
                            Calendar cal2 = Calendar.getInstance();
                            cal1.setTime(searchDate);
                            cal2.setTime(showDate);

                            if (cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR) &&
                                    cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)) {
                                filteredOrders.add(order);
                            }
                        }
                    }
                } catch (ParseException e) {
                    // If date parsing fails, return an empty list
                    return new ArrayList<>();
                }
                break;

            case "ordervalue":
                // Filter by order value (needs numeric validation)
                try {
                    Double searchValue = Double.parseDouble(query);

                    for (Order order : allOrders) {
                        if (order.getTotalPrice() != null &&
                                Math.abs(order.getTotalPrice() - searchValue) < 0.01) {
                            filteredOrders.add(order);
                        }
                    }
                } catch (NumberFormatException e) {
                    // If number parsing fails, return an empty list
                    return new ArrayList<>();
                }
                break;

            case "orderdate":
                // Filter by order date (needs date validation)
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    Date searchDate = dateFormat.parse(query);

                    for (Order order : allOrders) {
                        if (order.getOrderDate() != null) {
                            Date orderDate = order.getOrderDate();
                            Calendar cal1 = Calendar.getInstance();
                            Calendar cal2 = Calendar.getInstance();
                            cal1.setTime(searchDate);
                            cal2.setTime(orderDate);

                            if (cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR) &&
                                    cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)) {
                                filteredOrders.add(order);
                            }
                        }
                    }
                } catch (ParseException e) {
                    // If date parsing fails, return an empty list
                    return new ArrayList<>();
                }
                break;

            case "ordercode":
                // Filter by order ID
                try {
                    Long searchId = Long.parseLong(query);

                    for (Order order : allOrders) {
                        if (order.getOrderId() != null &&
                                order.getOrderId().equals(searchId)) {
                            filteredOrders.add(order);
                        }
                    }
                } catch (NumberFormatException e) {
                    // If we can't parse as a number, try searching order ID as a string
                    for (Order order : allOrders) {
                        if (order.getOrderId() != null &&
                                order.getOrderId().toString().contains(query)) {
                            filteredOrders.add(order);
                        }
                    }
                }
                break;

            default:
                // If no valid criteria is provided, return all orders
                return allOrders;
        }

        return filteredOrders;
    }

    /**
     * Get all orders for a specific user by user ID
     *
     * @param userId the ID of the user
     * @return list of orders associated with the user
     */
    public List<Order> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserUserId(userId);
    }

    /**
     * Get all orders for a specific user
     *
     * @param user the user entity
     * @return list of orders associated with the user
     */
    public List<Order> getOrdersByUser(User user) {
        return orderRepository.findByUser(user);
    }


    /**
     * Save an order to the database
     *
     * @param order the order to save
     * @return the saved order with updated ID
     */
    public Order saveOrder(Order order) {
        return orderRepository.save(order);
    }


}