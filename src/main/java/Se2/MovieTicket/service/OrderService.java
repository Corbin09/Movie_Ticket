package Se2.MovieTicket.service;

import Se2.MovieTicket.dto.OrderDTO;
import Se2.MovieTicket.model.*;
import Se2.MovieTicket.repository.OrderRepository;
import Se2.MovieTicket.repository.SeatStatusRepository;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.hibernate.FlushMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.hibernate.Session;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private SeatStatusRepository seatStatusRepository;

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

    public Page<Order> getAllOrdersPaginated(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }

    public Page<Order> searchOrdersByCriteriaPaginated(String criteria, String query, Pageable pageable) {
        if (query == null || query.trim().isEmpty()) {
            return getAllOrdersPaginated(pageable);
        }

        query = query.trim();

        switch (criteria) {
            case "ordercode":
                try {
                    Long orderId = Long.parseLong(query);
                    return orderRepository.findByOrderId(orderId, pageable);
                } catch (NumberFormatException e) {
                    return Page.empty(pageable);
                }
            case "user":
                return orderRepository.findByUserUsernameContainingIgnoreCase(query, pageable);
            case "movie":
                return orderRepository.findByShowtimeFilmFilmNameContainingIgnoreCase(query, pageable);
            case "theater":
                return orderRepository.findByShowtimeCinemaCinemaNameContainingIgnoreCase(query, pageable);
            case "room":
                return orderRepository.findByShowtimeRoomRoomNameContainingIgnoreCase(query, pageable);
            case "showdate":
                try {
                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                    Date date = formatter.parse(query);
                    return orderRepository.findByShowtimeShowDate(date, pageable);
                } catch (ParseException e) {
                    return Page.empty(pageable);
                }
            case "ordervalue":
                try {
                    double value = Double.parseDouble(query);
                    return orderRepository.findByTotalPrice(value, pageable);
                } catch (NumberFormatException e) {
                    return Page.empty(pageable);
                }
            case "orderdate":
                try {
                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                    Date date = formatter.parse(query);
                    return orderRepository.findByOrderDate(date, pageable);
                } catch (ParseException e) {
                    return Page.empty(pageable);
                }
            default:
                return getAllOrdersPaginated(pageable);
        }
    }

    public List<Order> searchOrdersByCriteria(String criteria, String query) {
        List<Order> allOrders = orderRepository.findAllWithDetails();

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
                            order.getShowtime().getCinema().getCinemaName().toLowerCase()
                                    .contains(query.toLowerCase())) {
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
                // Filter by show date
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
                    return new ArrayList<>();
                }
                break;

            case "ordervalue":
                // Filter by order value
                try {
                    Double searchValue = Double.parseDouble(query);

                    for (Order order : allOrders) {
                        if (order.getTotalPrice() != null &&
                                Math.abs(order.getTotalPrice() - searchValue) < 0.01) {
                            filteredOrders.add(order);
                        }
                    }
                } catch (NumberFormatException e) {
                    return new ArrayList<>();
                }
                break;

            case "orderdate":
                // Filter by order date
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
                    for (Order order : allOrders) {
                        if (order.getOrderId() != null &&
                                order.getOrderId().toString().contains(query)) {
                            filteredOrders.add(order);
                        }
                    }
                }
                break;

            default:
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

    @Transactional
    public Order saveOrder(Order order) {
        if (order.getTotalPrice() == null || order.getTotalPrice() == 0) {
            order.calculateTotal();
        }

        Session session = entityManager.unwrap(Session.class);
        FlushMode originalFlushMode = session.getHibernateFlushMode();
        session.setHibernateFlushMode(FlushMode.MANUAL);

        try {
            if (order.getTickets() != null) {
                for (Ticket ticket : order.getTickets()) {
                    ticket.setOrder(order);
                }
            }

            if (order.getPopcornOrders() != null) {
                for (PopcornOrder popcornOrder : order.getPopcornOrders()) {
                    popcornOrder.setOrder(order);
                }
            }

            Order savedOrder = orderRepository.save(order);

            session.flush();

            return savedOrder;
        } finally {
            session.setHibernateFlushMode(originalFlushMode);
        }
    }

    public Order findById(Long orderId) {
        return orderRepository.findById(orderId).orElse(null);
    }
}