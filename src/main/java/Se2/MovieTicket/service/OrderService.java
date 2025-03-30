package Se2.MovieTicket.service;

import Se2.MovieTicket.dto.OrderDTO;
import Se2.MovieTicket.model.Order;
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

    /**
     * Universal search method that searches across all relevant fields without requiring a specific searchBy parameter
     * @param search The search term
     * @param filterBy Optional filter that will be applied along with the search
     * @return List of Orders matching the search criteria
     */
    public List<Order> searchOrders(String search, String filterBy) {
        if (search == null || search.trim().isEmpty()) {
            return filterBy != null && !filterBy.trim().isEmpty()
                    ? filterOrders(filterBy)
                    : orderRepository.findAllWithDetails();
        }

        // Get base list of orders (either all or filtered)
        List<Order> baseOrders = filterBy != null && !filterBy.trim().isEmpty()
                ? filterOrders(filterBy)
                : orderRepository.findAllWithDetails();

        // Try to search by orderId if search is a number
        try {
            Long orderId = Long.parseLong(search);
            Optional<Order> orderOpt = orderRepository.findById(orderId);
            if (orderOpt.isPresent() && baseOrders.contains(orderOpt.get())) {
                return List.of(orderOpt.get());
            }
        } catch (NumberFormatException e) {
            // Not a number, continue with other search methods
        }

        // Try to parse search as date
        Date searchDate = null;
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            searchDate = formatter.parse(search);
        } catch (ParseException e) {
            // Not a valid date format, continue with other search methods
        }

        final Date finalSearchDate = searchDate;
        String searchLower = search.toLowerCase();

        // Search across all relevant fields
        return baseOrders.stream()
                .filter(order -> {
                    // Check order ID as string
                    if (order.getOrderId() != null && order.getOrderId().toString().contains(search)) {
                        return true;
                    }

                    // Check username
                    if (order.getUser() != null && order.getUser().getUsername() != null
                            && order.getUser().getUsername().toLowerCase().contains(searchLower)) {
                        return true;
                    }

                    // Check film name
                    if (order.getShowtime() != null && order.getShowtime().getFilm() != null
                            && order.getShowtime().getFilm().getFilmName() != null
                            && order.getShowtime().getFilm().getFilmName().toLowerCase().contains(searchLower)) {
                        return true;
                    }

                    // Check cinema name
                    if (order.getShowtime() != null && order.getShowtime().getCinema() != null
                            && order.getShowtime().getCinema().getCinemaName() != null
                            && order.getShowtime().getCinema().getCinemaName().toLowerCase().contains(searchLower)) {
                        return true;
                    }

                    // Check room name
                    if (order.getShowtime() != null && order.getShowtime().getRoom() != null
                            && order.getShowtime().getRoom().getRoomName() != null
                            && order.getShowtime().getRoom().getRoomName().toLowerCase().contains(searchLower)) {
                        return true;
                    }

                    // Check showtime
                    if (order.getShowtime() != null && order.getShowtime().getShowTime() != null
                            && order.getShowtime().getShowTime().toString().toLowerCase().contains(searchLower)) {
                        return true;
                    }

                    // Check order date
                    if (finalSearchDate != null && order.getOrderDate() != null) {
                        Calendar cal1 = Calendar.getInstance();
                        Calendar cal2 = Calendar.getInstance();
                        cal1.setTime(finalSearchDate);
                        cal2.setTime(order.getOrderDate());

                        return cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH)
                                && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH)
                                && cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);
                    }

                    return false;
                })
                .collect(Collectors.toList());
    }

    public List<Order> filterOrders(String filterBy) {
        if (filterBy == null || filterBy.trim().isEmpty() || filterBy.equalsIgnoreCase("all")) {
            return orderRepository.findAllWithDetails();
        }

        switch (filterBy.toLowerCase()) {
            case "today":
                // Filter orders for today
                Date today = new Date();
                Calendar cal = Calendar.getInstance();
                cal.setTime(today);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                Date startOfDay = cal.getTime();

                cal.set(Calendar.HOUR_OF_DAY, 23);
                cal.set(Calendar.MINUTE, 59);
                cal.set(Calendar.SECOND, 59);
                Date endOfDay = cal.getTime();

                return orderRepository.findByOrderDateBetween(startOfDay, endOfDay);

            case "yesterday":
                // Filter orders from yesterday
                Calendar calYest = Calendar.getInstance();
                calYest.add(Calendar.DATE, -1);
                calYest.set(Calendar.HOUR_OF_DAY, 0);
                calYest.set(Calendar.MINUTE, 0);
                calYest.set(Calendar.SECOND, 0);
                Date startOfYesterday = calYest.getTime();

                calYest.set(Calendar.HOUR_OF_DAY, 23);
                calYest.set(Calendar.MINUTE, 59);
                calYest.set(Calendar.SECOND, 59);
                Date endOfYesterday = calYest.getTime();

                return orderRepository.findByOrderDateBetween(startOfYesterday, endOfYesterday);

            case "this_week":
                // Filter orders for this week
                Calendar calWeek = Calendar.getInstance();
                calWeek.setFirstDayOfWeek(Calendar.MONDAY);
                calWeek.set(Calendar.DAY_OF_WEEK, calWeek.getFirstDayOfWeek());
                calWeek.set(Calendar.HOUR_OF_DAY, 0);
                calWeek.set(Calendar.MINUTE, 0);
                calWeek.set(Calendar.SECOND, 0);
                Date startOfWeek = calWeek.getTime();

                calWeek.add(Calendar.DAY_OF_WEEK, 6);
                calWeek.set(Calendar.HOUR_OF_DAY, 23);
                calWeek.set(Calendar.MINUTE, 59);
                calWeek.set(Calendar.SECOND, 59);
                Date endOfWeek = calWeek.getTime();

                return orderRepository.findByOrderDateBetween(startOfWeek, endOfWeek);

            case "this_month":
                // Filter orders for this month
                Calendar calMonth = Calendar.getInstance();
                calMonth.set(Calendar.DAY_OF_MONTH, 1);
                calMonth.set(Calendar.HOUR_OF_DAY, 0);
                calMonth.set(Calendar.MINUTE, 0);
                calMonth.set(Calendar.SECOND, 0);
                Date startOfMonth = calMonth.getTime();

                calMonth.add(Calendar.MONTH, 1);
                calMonth.add(Calendar.DAY_OF_MONTH, -1);
                calMonth.set(Calendar.HOUR_OF_DAY, 23);
                calMonth.set(Calendar.MINUTE, 59);
                calMonth.set(Calendar.SECOND, 59);
                Date endOfMonth = calMonth.getTime();

                return orderRepository.findByOrderDateBetween(startOfMonth, endOfMonth);

            case "this_year":
                // Filter orders for this year
                Calendar calYear = Calendar.getInstance();
                calYear.set(Calendar.DAY_OF_YEAR, 1);
                calYear.set(Calendar.HOUR_OF_DAY, 0);
                calYear.set(Calendar.MINUTE, 0);
                calYear.set(Calendar.SECOND, 0);
                Date startOfYear = calYear.getTime();

                calYear.add(Calendar.YEAR, 1);
                calYear.add(Calendar.DAY_OF_YEAR, -1);
                calYear.set(Calendar.HOUR_OF_DAY, 23);
                calYear.set(Calendar.MINUTE, 59);
                calYear.set(Calendar.SECOND, 59);
                Date endOfYear = calYear.getTime();

                return orderRepository.findByOrderDateBetween(startOfYear, endOfYear);

            case "price_asc":
                // Sort orders by price ascending
                return orderRepository.findAllWithDetails().stream()
                        .sorted(Comparator.comparing(Order::getTotalPrice))
                        .collect(Collectors.toList());

            case "price_desc":
                // Sort orders by price descending
                return orderRepository.findAllWithDetails().stream()
                        .sorted(Comparator.comparing(Order::getTotalPrice).reversed())
                        .collect(Collectors.toList());

            case "date_asc":
                // Sort orders by date ascending
                return orderRepository.findAllWithDetails().stream()
                        .sorted(Comparator.comparing(Order::getOrderDate))
                        .collect(Collectors.toList());

            case "date_desc":
                // Sort orders by date descending
                return orderRepository.findAllWithDetails().stream()
                        .sorted(Comparator.comparing(Order::getOrderDate).reversed())
                        .collect(Collectors.toList());

            default:
                return orderRepository.findAllWithDetails();
        }
    }
}