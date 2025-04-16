package Se2.MovieTicket.controllers;

import Se2.MovieTicket.dto.FilmDTO;
import Se2.MovieTicket.dto.SeatDTO;
import Se2.MovieTicket.impl.UserDetailsImpl;
import Se2.MovieTicket.model.*;
import Se2.MovieTicket.service.*;
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
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/")
public class SeatController {
    @Autowired
    private SeatService seatService;

    @Autowired
    private UserService userService;

    @Autowired
    private ShowtimeService showtimeService;

    @Autowired
    private CinemaService cinemaService;
    @Autowired
    private FilmService filmService;


    @GetMapping("/pick-seat")
    public String pickSeat(
            @RequestParam("filmId") Long filmId,
            @RequestParam("cinemaId") Long cinemaId,
            @RequestParam("showtimeId") Long showtimeId,
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

        // Get room details from showtime
        Room room = showtime.getRoom();
        model.addAttribute("room", room);

        // Get all seats in the room
        List<SeatDTO> seats = seatService.getSeatsByRoomId(room.getRoomId());
        model.addAttribute("seats", seats);

        // Nhóm ghế theo hàng và sắp xếp số ghế trong mỗi hàng
        Map<String, List<SeatDTO>> seatsByRow = seats.stream()
                .collect(Collectors.groupingBy(
                        SeatDTO::getSeatRow,
                        TreeMap::new,  // Dùng TreeMap để đảm bảo thứ tự hàng A-Z
                        Collectors.collectingAndThen(Collectors.toList(), list -> {
                            list.sort(Comparator.comparing(SeatDTO::getSeatNumber)); // Sắp xếp theo số ghế
                            return list;
                        })
                ));

        model.addAttribute("seatsByRow", seatsByRow);

        // Get seat status for this showtime
        Map<String, String> seatStatusMap = seatService.getSeatStatusMap(showtimeId);
        model.addAttribute("seatStatusMap", seatStatusMap);

        // Parse selected date
        LocalDate parsedDate = LocalDate.parse(selectedDate);
        model.addAttribute("selectedDate", parsedDate);
        model.addAttribute("formattedDate", parsedDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        // Thêm vào đây: Truyền selectedSeatsJson vào model nếu có
        if (selectedSeatsJson != null && !selectedSeatsJson.isEmpty()) {
            model.addAttribute("selectedSeatsJson", selectedSeatsJson);
        }

        return "pick-seat";
    }
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