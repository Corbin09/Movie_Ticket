package Se2.MovieTicket.controllers;

import Se2.MovieTicket.dto.CinemaWithShowtimesDTO;
import Se2.MovieTicket.dto.DayDTO;
import Se2.MovieTicket.dto.WeekDTO;
import Se2.MovieTicket.service.CinemaService;
import Se2.MovieTicket.service.FilmService;
import Se2.MovieTicket.service.ShowtimeService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.*;
import java.time.format.DateTimeParseException;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

@RestController
@RequestMapping("/api")
public class MovieTicketApiController {

    private final FilmService filmService;
    private final CinemaService cinemaService;
    private final ShowtimeService showtimeService;
    private static final Logger logger = LoggerFactory.getLogger(MovieTicketApiController.class);

    @Autowired
    public MovieTicketApiController(FilmService filmService, CinemaService cinemaService, ShowtimeService showtimeService) {
        this.filmService = Objects.requireNonNull(filmService, "FilmService cannot be null");
        this.cinemaService = Objects.requireNonNull(cinemaService, "CinemaService cannot be null");
        this.showtimeService = Objects.requireNonNull(showtimeService, "ShowtimeService cannot be null");
    }

    @GetMapping("/calendar")
    @ResponseBody
    public Map<String, Object> getCalendarData(
            @RequestParam("filmId") Long filmId,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year) {

        if (filmId == null || filmId <= 0) {
            throw new IllegalArgumentException("Invalid filmId. It must be a positive number.");
        }

        logger.info("API call for calendar data - filmId: {}, month: {}, year: {}", filmId, month, year);
        Map<String, Object> response = new HashMap<>();

        LocalDate today = LocalDate.now();
        int currentMonth = (month != null) ? month : today.getMonthValue();
        int currentYear = (year != null) ? year : today.getYear();

        YearMonth yearMonth;
        try {
            yearMonth = YearMonth.of(currentYear, currentMonth);
        } catch (DateTimeException e) {
            logger.warn("Invalid month/year input. Using today's date.");
            yearMonth = YearMonth.of(today.getYear(), today.getMonthValue());
        }

        response.put("currentMonth", yearMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault()));
        response.put("currentYear", yearMonth.getYear());
        response.put("currentMonthNum", yearMonth.getMonthValue());
        response.put("weekDays", getWeekDays());
        response.put("weeks", getCalendarWeeks(yearMonth, today, filmId));

        return response;
    }

    @GetMapping("/showtimes-movie-ticket")
    @ResponseBody
    public List<CinemaWithShowtimesDTO> getShowtimes(
            @RequestParam("filmId") Long filmId,
            @RequestParam("date") String dateStr) {

        if (filmId == null || filmId <= 0) {
            throw new IllegalArgumentException("Invalid filmId. It must be a positive number.");
        }

        logger.info("API call for showtimes - filmId: {}, date: {}", filmId, dateStr);
        LocalDate selectedDate;
        try {
            selectedDate = LocalDate.parse(dateStr);
        } catch (DateTimeParseException e) {
            logger.error("Invalid date format: {}. Expected format: yyyy-MM-dd", dateStr);
            throw new IllegalArgumentException("Invalid date format. Please use yyyy-MM-dd.");
        }

        return cinemaService.getCinemasWithShowtimesForFilmAndDate(filmId, selectedDate);
    }

    private List<DayDTO> getWeekDays() {
        List<DayDTO> days = new ArrayList<>();
        for (DayOfWeek day : DayOfWeek.values()) {
            boolean isWeekend = (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY);
            String shortName = day.getDisplayName(TextStyle.SHORT, Locale.getDefault());
            days.add(new DayDTO(shortName, isWeekend));
        }
        return days;
    }

    private List<WeekDTO> getCalendarWeeks(YearMonth yearMonth, LocalDate today, Long filmId) {
        List<WeekDTO> weeks = new ArrayList<>();
        LocalDate firstDayOfMonth = yearMonth.atDay(1);
        LocalDate lastDayOfMonth = yearMonth.atEndOfMonth();
        LocalDate firstCalendarDate = firstDayOfMonth.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        LocalDate date = firstCalendarDate;
        while (!date.isAfter(lastDayOfMonth)) {
            List<DayDTO> days = new ArrayList<>();
            for (int i = 0; i < 7; i++) {
                boolean isToday = date.equals(today);
                boolean isPast = date.isBefore(today);
                boolean isCurrentMonth = date.getMonth() == yearMonth.getMonth();
                boolean hasShowtimes = showtimeService.hasShowtimesForFilmAndDate(filmId, date);

                String formattedDate = date.toString();
                String shortName = date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.getDefault());

                days.add(new DayDTO(
                        String.valueOf(date.getDayOfMonth()),
                        shortName,
                        isToday,
                        isPast,
                        hasShowtimes,
                        formattedDate,
                        date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY,
                        false,
                        isCurrentMonth
                ));
                date = date.plusDays(1);
            }
            weeks.add(new WeekDTO(days));
        }
        return weeks;
    }

    @GetMapping("/booking")
    public String bookTicket(@RequestParam Long filmId,
                             @RequestParam Long cinemaId,
                             @RequestParam Long showtimeId,
                             @RequestParam String selectedDate) {

        logger.info("Booking request received:");
        logger.info("Film ID: {}", filmId);
        logger.info("Cinema ID: {}", cinemaId);
        logger.info("Showtime ID: {}", showtimeId);
        logger.info("Selected Date: {}", selectedDate);

        return "booking-page";
    }
}
