package Se2.MovieTicket.controllers;

import Se2.MovieTicket.dto.FilmDTO;
import Se2.MovieTicket.dto.PopcornOrderDTO;
import Se2.MovieTicket.dto.SeatDTO;
import Se2.MovieTicket.impl.UserDetailsImpl;
import Se2.MovieTicket.model.*;
import Se2.MovieTicket.service.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/")
public class PopcornOrderController {
    @Autowired
    private PopcornOrderService popcornOrderService;

    @Autowired
    private UserService userService;

    @Autowired
    private FilmService filmService;

    @Autowired
    private CinemaService cinemaService;

    @Autowired
    private ShowtimeService showtimeService;

    @Autowired
    private PopcornComboService popcornComboService;

    @GetMapping("/pick-popcorn")
    public String pickPopcorn(
            @RequestParam("filmId") Long filmId,
            @RequestParam("cinemaId") Long cinemaId,
            @RequestParam("showtimeId") Long showtimeId,
            @RequestParam("ticketSubtotal") double ticketSubtotal,
            @RequestParam("selectedDate") String selectedDate,
            @RequestParam(value = "selectedSeatsJson", required = false) String selectedSeatsJson,
            Model model, HttpServletRequest request) {


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
        // Đảm bảo selectedSeatsJson được truyền vào model
        if (selectedSeatsJson != null && !selectedSeatsJson.isEmpty()) {
            model.addAttribute("selectedSeatsJson", selectedSeatsJson);
        }
        List<Long> selectedSeatIds = new ArrayList<>();
        List<SeatDTO> selectedSeats = new ArrayList<>();

        // If selectedSeatsJson is provided, parse it
        if (selectedSeatsJson != null && !selectedSeatsJson.isEmpty()) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                List<Map<String, Object>> seatsList = objectMapper.readValue(selectedSeatsJson,
                        new TypeReference<List<Map<String, Object>>>() {});

                // Extract seat IDs and create SeatDTO objects
                for (Map<String, Object> seatData : seatsList) {
                    Long seatId = Long.parseLong(seatData.get("id").toString());
                    String seatLabel = (String) seatData.get("label");
                    String seatType = (String) seatData.get("type");

                    selectedSeatIds.add(seatId);

                    // Create a simplified SeatDTO with just the info we need
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

        // Add ticket subtotal to model (using the value passed as parameter)
        model.addAttribute("ticketSubtotal", ticketSubtotal);

        // Get film details
        Optional<Film> filmOptional = filmService.getFilmById(filmId);
        if (!filmOptional.isPresent()) {
            return "redirect:/films";
        }
        model.addAttribute("film", convertToFilmDTO(filmOptional.get()));

        // Get cinema details
        Optional<Cinema> cinemaOptional = cinemaService.getCinemaById(cinemaId);
        if (!cinemaOptional.isPresent()) {
            return "redirect:/films";
        }
        model.addAttribute("cinema", cinemaOptional.get());

        // Get showtime details
        Optional<Showtime> showtimeOptional = showtimeService.getShowtimeById(showtimeId);
        if (!showtimeOptional.isPresent()) {
            return "redirect:/films";
        }
        Showtime showtime = showtimeOptional.get();
        model.addAttribute("showtime", showtime);

        // Parse selected date
        LocalDate parsedDate = LocalDate.parse(selectedDate);
        model.addAttribute("selectedDate", parsedDate);
        model.addAttribute("formattedDate", parsedDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        // Get all popcorn combos
        List<PopcornCombo> popcornCombos = popcornComboService.getAllPopcornCombos();
        model.addAttribute("popcornCombos", popcornCombos);

        // Store selected seat IDs and seat objects
        model.addAttribute("selectedSeatIds", selectedSeatIds);
        model.addAttribute("selectedSeats", selectedSeats);

        return "pick-popcorn";
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
}