package Se2.MovieTicket.controllers;

import Se2.MovieTicket.dto.OrderDTO;
import Se2.MovieTicket.model.Order;
import Se2.MovieTicket.repository.OrderRepository;
import Se2.MovieTicket.service.OrderService;
import Se2.MovieTicket.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import Se2.MovieTicket.dto.*;
import Se2.MovieTicket.model.*;
import Se2.MovieTicket.service.*;
import Se2.MovieTicket.impl.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.List;


@Controller
@RequestMapping("/")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;


    @Autowired
    private ShowtimeService showtimeService;

    @Autowired
    private CinemaService cinemaService;

    @Autowired
    private FilmService filmService;



    @GetMapping("/pick-payment-method")
    public String pickPaymentMethod(
            @RequestParam("filmId") Long filmId,
            @RequestParam("cinemaId") Long cinemaId,
            @RequestParam("showtimeId") Long showtimeId,
            @RequestParam("ticketSubtotal") double ticketSubtotal,
            @RequestParam("selectedDate") String selectedDate,
            @RequestParam(value = "selectedSeatsJson", required = false) String selectedSeatsJson,
            @RequestParam(value = "selectedCombosJson", required = false) String selectedCombosJson,
            @RequestParam(value = "comboSubtotal", defaultValue = "0.0") double comboSubtotal,
            Model model, HttpServletRequest request) {


        // Get user from session or SecurityContext (same as in pickPopcorn)
        HttpSession session = request.getSession(false);
        User sessionUser = (session != null) ? (User) session.getAttribute("user") : null;

        if (sessionUser == null) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl) {
                UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
                sessionUser = userService.getUserById(userDetails.getId()).orElse(null);

                if (sessionUser != null && session != null) {
                    session.setAttribute("user", sessionUser);
                }
            }
        }

        if (sessionUser != null) {
            model.addAttribute("user", sessionUser);
        }

        // Pass all parameters to the model for persistence
        model.addAttribute("filmId", filmId);
        model.addAttribute("cinemaId", cinemaId);
        model.addAttribute("showtimeId", showtimeId);
        model.addAttribute("ticketSubtotal", ticketSubtotal);
        model.addAttribute("selectedDate", selectedDate);
        model.addAttribute("comboSubtotal", comboSubtotal);

        // Calculate total price
        double totalPrice = ticketSubtotal + comboSubtotal;
        model.addAttribute("totalPrice", totalPrice);

        // Ensure selectedSeatsJson is passed to the model
        if (selectedSeatsJson != null && !selectedSeatsJson.isEmpty()) {
            model.addAttribute("selectedSeatsJson", selectedSeatsJson);
        }

        // Ensure selectedCombosJson is passed to the model
        if (selectedCombosJson != null && !selectedCombosJson.isEmpty()) {
            model.addAttribute("selectedCombosJson", selectedCombosJson);
        }

        List<SeatDTO> selectedSeats = new ArrayList<>();
        // Parse selectedSeatsJson to get seat information
        if (selectedSeatsJson != null && !selectedSeatsJson.isEmpty()) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                List<Map<String, Object>> seatsList = objectMapper.readValue(selectedSeatsJson,
                        new TypeReference<List<Map<String, Object>>>() {});

                // Create SeatDTO objects
                for (Map<String, Object> seatData : seatsList) {
                    Long seatId = Long.parseLong(seatData.get("id").toString());
                    String seatLabel = (String) seatData.get("label");
                    String seatType = (String) seatData.get("type");

                    // Create a simplified SeatDTO
                    SeatDTO seat = new SeatDTO();
                    seat.setSeatId(seatId);
                    seat.setSeatRow(seatLabel.substring(0, 1));
                    seat.setSeatNumber(Integer.valueOf(seatLabel.substring(1)));
                    seat.setSeatType(seatType);

                    selectedSeats.add(seat);
                }
            } catch (JsonProcessingException e) {
            }
        }
        model.addAttribute("selectedSeats", selectedSeats);

        List<PopcornComboDTO> selectedCombos = new ArrayList<>();
        // Parse selectedCombosJson to get combo information
        if (selectedCombosJson != null && !selectedCombosJson.isEmpty()) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                selectedCombos = objectMapper.readValue(selectedCombosJson,
                        new TypeReference<List<PopcornComboDTO>>() {});

            } catch (JsonProcessingException e) {
                return "redirect:/home";
            }
        }
        model.addAttribute("selectedCombos", selectedCombos);

        // Get film details
        Optional<Film> filmOptional = filmService.getFilmById(filmId);
        if (!filmOptional.isPresent()) {
            return "redirect:/films";
        }
        Film film = filmOptional.get();
        model.addAttribute("film", convertToFilmDTO(film));

        // Get cinema details
        Optional<Cinema> cinemaOptional = cinemaService.getCinemaById(cinemaId);
        if (!cinemaOptional.isPresent()) {
            return "redirect:/home";
        }
        model.addAttribute("cinema", cinemaOptional.get());

        // Get showtime details
        Optional<Showtime> showtimeOptional = showtimeService.getShowtimeById(showtimeId);
        if (!showtimeOptional.isPresent()) {
            return "redirect:/home";
        }
        Showtime showtime = showtimeOptional.get();
        model.addAttribute("showtime", showtime);

        // Parse selected date
        LocalDate parsedDate = LocalDate.parse(selectedDate);
        model.addAttribute("selectedDate", parsedDate);
        model.addAttribute("formattedDate", parsedDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        return "pick-payment-method";
    }
    // Helper method to convert Film to FilmDTO
    private FilmDTO convertToFilmDTO(Film film) {
        FilmDTO filmDTO = new FilmDTO();
        filmDTO.setFilmId(film.getFilmId());
        filmDTO.setFilmName(film.getFilmName());
        filmDTO.setFilmImg(film.getFilmImg());
        filmDTO.setFilmTrailer(film.getFilmTrailer());
        filmDTO.setFilmDescription(film.getFilmDescription());
        filmDTO.setReleaseDate(film.getReleaseDate());
        filmDTO.setDuration(film.getDuration());
        filmDTO.setFilmType(film.getFilmType());
        filmDTO.setCountry(film.getCountry());
        filmDTO.setAgeLimit(film.getAgeLimit());

        // Extract related data
        filmDTO.setDirectorNames(film.getFilmDirectors().stream()
                .map(fd -> fd.getDirector().getDirectorName())
                .collect(Collectors.toList()));

        filmDTO.setActorNames(film.getFilmActors().stream()
                .map(fa -> fa.getActor().getActorName())
                .collect(Collectors.toList()));

        filmDTO.setCategoryNames(film.getFilmCategories().stream()
                .map(fc -> fc.getCategory().getCategoryName())
                .collect(Collectors.toList()));

        return filmDTO;
    }



    @Autowired
    private TicketService ticketService;

    @Autowired
    private PopcornOrderService popcornOrderService;

    @Autowired
    private SeatStatusService seatStatusService;


    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PopcornComboService popcornComboService;


    @Autowired
    private SeatService seatService;

    @PostMapping("/create-order")
    @ResponseBody
    @Transactional
    public Map<String, Object> createOrder(
            @RequestParam("showtimeId") Long showtimeId,
            @RequestParam("selectedSeatsJson") String selectedSeatsJson,
            @RequestParam(value = "selectedCombosJson", required = false, defaultValue = "[]") String selectedCombosJson,
            @RequestParam("paymentMethod") String paymentMethod,
            @RequestParam("totalPrice") Double totalPrice,
            HttpSession session) {

        long startTime = System.currentTimeMillis();
        System.out.println("=== Starting optimized createOrder method ===");
        Map<String, Object> response = new HashMap<>();

        try {
            // Log raw parameters
            System.out.println("Raw selectedSeatsJson: " + selectedSeatsJson);
            System.out.println("Raw selectedCombosJson: " + selectedCombosJson);

            // Ensure proper URL decoding
            try {
                selectedSeatsJson = URLDecoder.decode(selectedSeatsJson, StandardCharsets.UTF_8.toString());
                selectedCombosJson = URLDecoder.decode(selectedCombosJson, StandardCharsets.UTF_8.toString());
                System.out.println("Decoded selectedSeatsJson: " + selectedSeatsJson);
                System.out.println("Decoded selectedCombosJson: " + selectedCombosJson);
            } catch (Exception e) {
                System.err.println("Error decoding JSON parameters: " + e.getMessage());
                e.printStackTrace();
            }

            // Get current user - single query
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User user = userService.findByUsername(username);
            if (user == null) {
                response.put("success", false);
                response.put("message", "User not found");
                return response;
            }

            // Get showtime - single query
            Optional<Showtime> showtimeOpt = showtimeService.getShowtimeById(showtimeId);
            if (showtimeOpt.isEmpty()) {
                response.put("success", false);
                response.put("message", "Showtime not found");
                return response;
            }
            Showtime showtime = showtimeOpt.get();

            // Create order object
            Order order = new Order();
            order.setUser(user);
            order.setShowtime(showtime);
            order.setOrderDate(new Date());
            order.setTotalPrice(totalPrice);

            // Process seats in memory
            List<SeatDTO> selectedSeats = parseSeatsFromJson(selectedSeatsJson);
            System.out.println("Parsed " + selectedSeats.size() + " seats from JSON");
            Set<Long> seatIdsToUpdate = new HashSet<>();

            if (!selectedSeats.isEmpty()) {
                // Get all seats in ONE query
                List<Long> seatIds = selectedSeats.stream()
                        .map(SeatDTO::getSeatId)
                        .collect(Collectors.toList());
                System.out.println("Fetching " + seatIds.size() + " seats by IDs: " + seatIds);

                Map<Long, Seat> seatsMap = seatService.getSeatsByIds(seatIds).stream()
                        .collect(Collectors.toMap(Seat::getSeatId, seat -> seat));
                System.out.println("Found " + seatsMap.size() + " seats in database");

                for (SeatDTO seatDTO : selectedSeats) {
                    Seat seat = seatsMap.get(seatDTO.getSeatId());
                    if (seat == null) {
                        System.err.println("Seat not found in database: " + seatDTO.getSeatId());
                        continue;
                    }

                    // Create and add ticket
                    Ticket ticket = new Ticket();
                    ticket.setSeat(seat);
                    ticket.setTicketPrice((double) ("Vip".equalsIgnoreCase(seatDTO.getSeatType()) ? 150000 : 100000));
//                    ticket.setTicketPrice((double) seat.getPrice()); // Make sure to set the price

                    // Debug print
                    System.out.println("Creating ticket for seat: " + seat.getSeatId() + " label: " +
                            (seat.getSeatRow() + seat.getSeatNumber()) +
                            " type: " + seat.getSeatType());

                    // Add to order using the helper method
                    order.addTicket(ticket);

                    // Debug verification
                    System.out.println("Current ticket count in order: " + order.getTickets().size());

                    // Add seat ID to update list
                    seatIdsToUpdate.add(seat.getSeatId());
                }
                System.out.println("Created " + order.getTickets().size() + " tickets for " + selectedSeats.size() + " selected seats");
            }

            // Process combos in memory
            if (selectedCombosJson != null && !selectedCombosJson.isEmpty() &&
                    !selectedCombosJson.equals("null") && !selectedCombosJson.equals("[]")) {

                List<PopcornComboDTO> selectedCombos = parseCombosFromJson(selectedCombosJson);
                System.out.println("Parsed " + (selectedCombos != null ? selectedCombos.size() : 0) + " combos from JSON");

                if (selectedCombos != null && !selectedCombos.isEmpty()) {
                    // Get all combos in ONE query
                    List<Long> comboIds = selectedCombos.stream()
                            .map(PopcornComboDTO::getComboId)
                            .collect(Collectors.toList());
                    System.out.println("Fetching " + comboIds.size() + " combos by IDs: " + comboIds);

                    Map<Long, PopcornCombo> combosMap = popcornComboService.getCombosByIds(comboIds).stream()
                            .collect(Collectors.toMap(PopcornCombo::getComboId, combo -> combo));
                    System.out.println("Found " + combosMap.size() + " combos in database");

                    // Create popcorn orders
                    for (PopcornComboDTO comboDTO : selectedCombos) {
                        PopcornCombo combo = combosMap.get(comboDTO.getComboId());
                        if (combo == null) {
                            System.err.println("Combo not found in database: " + comboDTO.getComboId());
                            continue;
                        }

                        PopcornOrder popcornOrder = new PopcornOrder();
                        popcornOrder.setPopcornCombo(combo);
                        popcornOrder.setOrder(order);
                        popcornOrder.setComboQuantity(comboDTO.getQuantity());
                        order.getPopcornOrders().add(popcornOrder);
                    }

                    System.out.println("Created " + order.getPopcornOrders().size() + " popcorn orders for " + selectedCombos.size() + " selected combos");
                }
            }

            // Save order with a single operation
            System.out.println("Saving order with " + order.getTickets().size() + " tickets and " + order.getPopcornOrders().size() + " popcorn orders");
            Order savedOrder = orderService.saveOrder(order);
            System.out.println("Order saved with ID: " + savedOrder.getOrderId());

            // Update seat statuses in batch
            if (!seatIdsToUpdate.isEmpty()) {
                System.out.println("Updating status for " + seatIdsToUpdate.size() + " seats to BOOKED");
                seatStatusService.updateSeatStatusesInBatch(seatIdsToUpdate, showtimeId, "BOOKED");
            }

            response.put("success", true);
            response.put("orderId", savedOrder.getOrderId());
            response.put("totalPrice", savedOrder.getTotalPrice());
            response.put("paymentMethod", paymentMethod);
            response.put("ticketCount", savedOrder.getTickets().size());
            response.put("comboCount", savedOrder.getPopcornOrders().size());

            long endTime = System.currentTimeMillis();
            System.out.println("Order creation completed in " + (endTime - startTime) + "ms");

        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Order creation failed: " + e.getMessage());
        }

        return response;
    }

    private List<SeatDTO> parseSeatsFromJson(String selectedSeatsJson) {
        List<SeatDTO> seats = new ArrayList<>();
        System.out.println("Starting to parse seats from JSON: " + selectedSeatsJson);

        try {
            if (selectedSeatsJson != null && !selectedSeatsJson.isEmpty()) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode seatNodes = mapper.readTree(selectedSeatsJson);
                System.out.println("Found " + seatNodes.size() + " seat nodes in JSON");

                for (JsonNode seat : seatNodes) {
                    try {
                        System.out.println("Processing seat: " + seat.toString());
                        SeatDTO seatDTO = new SeatDTO();
                        seatDTO.setSeatId(Long.valueOf(seat.get("id").asText()));
                        String label = seat.get("label").asText();
                        seatDTO.setSeatRow(label.substring(0, 1));
                        seatDTO.setSeatNumber(Integer.parseInt(label.substring(1)));
                        seatDTO.setSeatType(seat.get("type").asText());
                        seats.add(seatDTO);
                        System.out.println("Successfully parsed seat: " + label);
                    } catch (Exception e) {
                        System.err.println("Error parsing individual seat: " + seat + " - " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error parsing seat JSON: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("Finished parsing, found " + seats.size() + " valid seats");
        return seats;
    }


    private List<PopcornComboDTO> parseCombosFromJson(String selectedCombosJson) {
        List<PopcornComboDTO> combos = new ArrayList<>();

        try {
            if (selectedCombosJson != null && !selectedCombosJson.isEmpty() && !selectedCombosJson.equals("null")) {
                System.out.println("Parsing combo JSON: " + selectedCombosJson);

                ObjectMapper mapper = new ObjectMapper();
                JsonNode comboNodes;

                try {
                    comboNodes = mapper.readTree(selectedCombosJson);
                } catch (Exception e) {
                    System.err.println("Error parsing combo JSON as array: " + e.getMessage());
                    return combos;
                }

                if (comboNodes.isArray()) {
                    for (JsonNode combo : comboNodes) {
                        try {
                            PopcornComboDTO comboDTO = new PopcornComboDTO();

                            // Handle case-specific accessors
                            if (combo.has("comboId")) {
                                comboDTO.setComboId(combo.get("comboId").asLong());
                            } else if (combo.has("id")) {
                                comboDTO.setComboId(combo.get("id").asLong());
                            }

                            // Set name
                            if (combo.has("comboName")) {
                                comboDTO.setComboName(combo.get("comboName").asText());
                            } else if (combo.has("name")) {
                                comboDTO.setComboName(combo.get("name").asText());
                            }

                            // Set price
                            if (combo.has("comboPrice")) {
                                comboDTO.setComboPrice(combo.get("comboPrice").asDouble());
                            } else if (combo.has("price")) {
                                comboDTO.setComboPrice(combo.get("price").asDouble());
                            }

                            // Set quantity
                            if (combo.has("quantity")) {
                                comboDTO.setQuantity(combo.get("quantity").asInt());
                            } else {
                                comboDTO.setQuantity(1); // Default quantity
                            }

                            System.out.println("Parsed combo: " + comboDTO.getComboName() +
                                    " (ID: " + comboDTO.getComboId() + ", Qty: " + comboDTO.getQuantity() + ")");
                            combos.add(comboDTO);
                        } catch (Exception e) {
                            System.err.println("Error parsing individual combo: " + e.getMessage());
                        }
                    }
                } else {
                    System.err.println("combosJson is not an array: " + selectedCombosJson);
                }
            }
        } catch (Exception e) {
            System.err.println("Error parsing combo JSON: " + e.getMessage());
            e.printStackTrace();
        }

        return combos;
    }








//--------------------------ADMIN----------------------------------------------------------
    @GetMapping("/manage-orders")
    public String manageOrders(
            @RequestParam(required = false) String searchCriteria,
            @RequestParam(required = false) String searchQuery,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int pageSize,
            Model model,
            HttpServletRequest request) {

        // Get user from session or SecurityContext
        HttpSession session = request.getSession(false);
        User sessionUser = (session != null) ? (User) session.getAttribute("user") : null;

        if (sessionUser == null) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl) {
                UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
                sessionUser = userService.getUserById(userDetails.getId()).orElse(null);

                if (sessionUser != null && session != null) {
                    session.setAttribute("user", sessionUser);
                }
            }
        }

        if (sessionUser != null) {
            model.addAttribute("user", sessionUser);
        }

        // Check if user has admin role
//        if (!userService.hasRole("ROLE_ADMIN")) {
//            return "redirect:/access-denied";
//        }

        // Create pageable object for database pagination
        Pageable pageable = PageRequest.of(page - 1, pageSize);

        // Get orders based on search criteria and query with pagination
        Page<?> ordersPage;

        try {
            // Only use search criteria if both searchCriteria and searchQuery are provided
            if (searchCriteria != null && !searchCriteria.isEmpty() && searchQuery != null && !searchQuery.isEmpty()) {
                // Search based on selected criteria and query with pagination
                ordersPage = orderService.searchOrdersByCriteriaPaginated(searchCriteria, searchQuery, pageable);
            } else {
                // Get all orders with pagination if no search criteria provided
                ordersPage = orderService.getAllOrdersPaginated(pageable);
            }

            // Add orders to model
            model.addAttribute("orders", ordersPage.getContent());

            // Add pagination parameters
            model.addAttribute("currentPage", page);
            model.addAttribute("pageSize", pageSize);
            model.addAttribute("totalItems", ordersPage.getTotalElements());
            model.addAttribute("totalPages", ordersPage.getTotalPages());

        } catch (Exception e) {
            model.addAttribute("orders", new ArrayList<>());
            model.addAttribute("errorMessage", "Error fetching orders: " + e.getMessage());
            model.addAttribute("currentPage", 1);
            model.addAttribute("pageSize", pageSize);
            model.addAttribute("totalItems", 0);
            model.addAttribute("totalPages", 0);
        }

        // Pass the selected search options to the view
        model.addAttribute("searchCriteria", searchCriteria);
        model.addAttribute("searchQuery", searchQuery);

        // Add currPage attribute for menu active state
        model.addAttribute("currPage", "manage-orders");

        return "manage-orders";
    }


}