
package Se2.MovieTicket.controllers;

import Se2.MovieTicket.dto.CinemaDTO;
import Se2.MovieTicket.dto.FilmDTO;
import Se2.MovieTicket.dto.ShowtimeDTO;
import Se2.MovieTicket.impl.UserDetailsImpl;
import Se2.MovieTicket.model.*;
import Se2.MovieTicket.service.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/")
public class ShowtimeController {
    @Autowired
    private ShowtimeService showtimeService;

    @Autowired
    private UserService userService;


    @Autowired
    private FilmService filmService;

    @Autowired
    private CinemaService cinemaService;

    @Autowired
    private RegionService regionService;

    @Autowired
    private RoomService roomService;


    @GetMapping("/showtime")
    public String getShowtimes(
            @RequestParam(required = false) Long regionId,
            @RequestParam(required = false) Long cinemaId,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "12") int size,
            @RequestParam(name = "showTime", required = false) String showTime,
            Model model, HttpServletRequest request) {
        model.addAttribute("currPage", "showtime");


        // Handle user session and security
        handleUserSession(model, request);

        // Get all regions for dropdown
        List<Region> regions = regionService.getAllRegions();
        model.addAttribute("regions", regions);

        // Create pageable for pagination
        Pageable pageable = PageRequest.of(page - 1, size);

        // Initialize variables
        Region selectedRegion = null;
        Cinema selectedCinema = null;
        List<CinemaDTO> cinemas = new ArrayList<>();
        Page<FilmDTO> filmsPage;

        // Handle region selection
        if (regionId != null) {
            selectedRegion = regionService.getRegionById(regionId).orElse(null);
            model.addAttribute("selectedRegion", selectedRegion);

            // Get cinemas for selected region
            if (selectedRegion != null) {
                cinemas = cinemaService.getCinemasByRegionId(regionId);
                model.addAttribute("cinemas", cinemas);
            }
        }

        // Handle cinema selection
        if (cinemaId != null && selectedRegion != null) {
            selectedCinema = cinemaService.getCinemaById(cinemaId).orElse(null);
            model.addAttribute("selectedCinema", selectedCinema);
        }

        // Apply filters progressively and get the appropriate films
        if (regionId == null) {
            // No filters - show all films with pagination
            filmsPage = filmService.getAllFilms(pageable);
        } else if (cinemaId == null) {
            // Only region filter - show films available in that region based on showtimes
            filmsPage = filmService.getFilmsByRegionThroughShowtimes(regionId, pageable);
        } else {
            // Region and cinema filters - show all films for the cinema based on showtimes
            filmsPage = filmService.getFilmsByCinemaThroughShowtimes(cinemaId, pageable);
        }

        // Add films to model
        List<FilmDTO> films = filmsPage.getContent();
        List<ShowtimeDTO> uniqueShowtimes = new ArrayList<>();

        // Populate uniqueShowtimes based on different contexts
        if (cinemaId != null) {
            // If cinema is selected, collect unique showtimes for that cinema
            for (FilmDTO film : films) {
                List<ShowtimeDTO> showtimes = showtimeService.getAllShowtimesByCinemaAndFilm(
                        cinemaId, film.getFilmId());

                for (ShowtimeDTO showtime : showtimes) {
                    if (!uniqueShowtimes.contains(showtime)) {
                        uniqueShowtimes.add(showtime);
                    }
                }
            }

            // Sort uniqueShowtimes by showtime (ascending order)
            uniqueShowtimes.sort(Comparator.comparing(ShowtimeDTO::getShowTime));

            model.addAttribute("uniqueShowtimes", uniqueShowtimes);
        }

        model.addAttribute("films", films);
        Set<ShowtimeDTO> sts = new HashSet<>();
        for (FilmDTO film : films) {
            sts.addAll(film.getShowtimes());
        }
        ArrayList<ShowtimeDTO> sortedSts = new ArrayList<>();
        sortedSts.addAll(sts);

        Collections.sort(sortedSts, new Comparator<ShowtimeDTO>() {
            @Override
            public int compare(ShowtimeDTO o1, ShowtimeDTO o2) {
                // Parse hours, minutes, and seconds from each showtime
                String[] time1Parts = o1.getShowTime().split(":");
                String[] time2Parts = o2.getShowTime().split(":");

                // Compare hours first
                int hourDiff = Integer.valueOf(time1Parts[0]) - Integer.valueOf(time2Parts[0]);
                if (hourDiff != 0) {
                    return hourDiff; // If hours are different, return the difference
                }

                // If hours are the same, compare minutes
                if (time1Parts.length > 1 && time2Parts.length > 1) {
                    int minuteDiff = Integer.valueOf(time1Parts[1]) - Integer.valueOf(time2Parts[1]);
                    if (minuteDiff != 0) {
                        return minuteDiff; // If minutes are different, return the difference
                    }
                }

                // If hours and minutes are the same, compare seconds
                if (time1Parts.length > 2 && time2Parts.length > 2) {
                    return Integer.valueOf(time1Parts[2]) - Integer.valueOf(time2Parts[2]);
                }

                return 0; // Times are equal
            }
        });
        model.addAttribute("showtimes", sortedSts);

        // Lọc danh sách showtimes dựa trên các filter được chọn
        if (showTime != null) {
            System.out.println("Filtering by selected showtimeId: " + showTime);

            // Lọc danh sách phim dựa trên showtimeId đã chọn
            List<FilmDTO> filteredFilms = films.stream()
                    .filter(film -> {
                        boolean hasShowtime = film.getShowtimes().stream().anyMatch(st -> st.getShowTime().equals(showTime));
                        System.out.println("Filtering by selected showtimeId: " + showTime);
                        System.out.println("Film: " + film.getFilmId() + " | Has Showtime: " + hasShowtime);
                        return hasShowtime;
                    })
                    .collect(Collectors.toList());

            System.out.println("Total filtered films: " + filteredFilms.size());

            // Áp dụng pagination cho danh sách đã lọc
            int totalItems = filteredFilms.size();
            int fromIndex = Math.min((page - 1) * size, totalItems);
            int toIndex = Math.min(fromIndex + size, totalItems);
            System.out.println("Pagination - fromIndex: " + fromIndex + ", toIndex: " + toIndex);
            System.out.println("Total item with showtime filter: " + totalItems);
            List<FilmDTO> paginatedFilms = filteredFilms.subList(fromIndex, toIndex);
            System.out.println("Paginated films count: " + paginatedFilms.size());

            // Thêm danh sách phim đã phân trang vào model
            model.addAttribute("films", paginatedFilms);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", (int) Math.ceil((double) totalItems / size));
            System.out.println("TotalPages: " + (int) Math.ceil((double) totalItems / size));
            model.addAttribute("totalItems", totalItems);
        } else {
            System.out.println("No specific showTime selected, loading all showtimes.");

            for (FilmDTO film : films) {
                List<ShowtimeDTO> allShowtimes = showtimeService.getAllShowtimesByCinemaAndFilm(cinemaId, film.getFilmId());
                film.setShowtimes(allShowtimes);
                System.out.println("Film: " + film.getFilmId() + " | Total Showtimes: " + allShowtimes.size());
            }

            System.out.println("Total films without filter: " + films.size());

            model.addAttribute("films", films);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", filmsPage.getTotalPages());
            model.addAttribute("totalItems", filmsPage.getTotalElements());
        }

        // Age restriction note
        model.addAttribute("ageRestriction", true);
        return "showtime";
    }

    private void handleUserSession(Model model, HttpServletRequest request) {
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
    }


    //--------------------------ADMIN-------------------------
    @GetMapping("/manage-showtimes")
    public String manageShowtimes(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String searchField,
            @RequestParam(required = false) String cinemaId,
            @RequestParam(required = false) String filmId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date date,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            Model model,
            HttpServletRequest request) {

        // Check if user has admin role
//    if (!userService.hasRole("ROLE_ADMIN")) {
//        return "redirect:/access-denied";
//    }

        // Add user to model
        addUserToModel(model, request);

        // Get all cinemas and films for the dropdown filters
        List<Cinema> cinemas = cinemaService.getAllCinemas();
        List<Film> films = filmService.getAllFilms();
        model.addAttribute("cinemas", cinemas);
        model.addAttribute("films", films);

        // Create pageable object for database pagination
        Pageable pageable = PageRequest.of(page - 1, size);

        // Get showtimes with pagination
        Page<Showtime> showtimesPage;

        try {
            if (search != null && !search.isEmpty()) {
                // Search showtimes based on searchField
                switch (searchField) {
                    case "film":
                        showtimesPage = showtimeService.searchShowtimesByFilmNamePaginated(search, pageable);
                        break;
                    case "cinema":
                        showtimesPage = showtimeService.searchShowtimesByCinemaNamePaginated(search, pageable);
                        break;
                    case "room":
                        showtimesPage = showtimeService.searchShowtimesByRoomNamePaginated(search, pageable);
                        break;
                    default:
                        // "all" or any other value - search across all fields
                        showtimesPage = showtimeService.searchShowtimesPaginated(search, pageable);
                }
                model.addAttribute("searchTerm", search);
                model.addAttribute("searchField", searchField);
            } else if (date != null) {
                // Filter showtimes by date
                showtimesPage = showtimeService.getShowtimesByDatePaginated(date, pageable);
                model.addAttribute("selectedDate", new SimpleDateFormat("yyyy-MM-dd").format(date));
                model.addAttribute("searchField", "date");
            } else if (cinemaId != null && !cinemaId.isEmpty()) {
                // Filter showtimes by cinema
                showtimesPage = showtimeService.getShowtimesByCinemaPaginated(Long.parseLong(cinemaId), pageable);
                model.addAttribute("selectedCinema", cinemaId);
            } else if (filmId != null && !filmId.isEmpty()) {
                // Filter showtimes by film
                showtimesPage = showtimeService.getShowtimesByFilmPaginated(Long.parseLong(filmId), pageable);
                model.addAttribute("selectedFilm", filmId);
            } else {
                // Get all showtimes with pagination
                showtimesPage = showtimeService.getAllShowtimesPaginated(pageable);
                model.addAttribute("searchField", "all");
            }

            List<ShowtimeDTO> showtimeDTOs = mapShowtimesWithDetails(showtimesPage.getContent());

            model.addAttribute("showtimes", showtimeDTOs);

            // Add pagination parameters
            model.addAttribute("currentPage", page);
            model.addAttribute("pageSize", size);
            model.addAttribute("totalShowtimes", showtimesPage.getTotalElements());
            model.addAttribute("totalPages", showtimesPage.getTotalPages());
            model.addAttribute("pageSizes", Arrays.asList(5, 10, 20, 50));

        } catch (Exception e) {
            model.addAttribute("showtimes", new ArrayList<>());
            model.addAttribute("errorMessage", "Error fetching showtimes: " + e.getMessage());
            model.addAttribute("currentPage", 1);
            model.addAttribute("pageSize", size);
            model.addAttribute("totalShowtimes", 0);
            model.addAttribute("totalPages", 0);
        }

        // Add currPage attribute for sidebar active menu
        model.addAttribute("currPage", "manage-showtimes");

        return "manage-showtime";
    }

// Các phương thức khác giữ nguyên

    private List<ShowtimeDTO> mapShowtimesWithDetails(List<Showtime> showtimes) {
        return showtimes.stream().map(showtime -> {
            ShowtimeDTO dto = new ShowtimeDTO();
            dto.setShowtimeId(showtime.getShowtimeId());
            dto.setShowDate(showtime.getShowDate());
            dto.setShowTime(showtime.getShowTime());

            // Set film-related properties
            if (showtime.getFilm() != null) {
                dto.setFilmId(showtime.getFilm().getFilmId());
                dto.setFilmName(showtime.getFilm().getFilmName());
            }

            // Set cinema-related properties
            if (showtime.getCinema() != null) {
                dto.setCinemaId(showtime.getCinema().getCinemaId());
                dto.setCinemaName(showtime.getCinema().getCinemaName());
            }

            // Set room-related properties
            if (showtime.getRoom() != null) {
                dto.setRoomId(showtime.getRoom().getRoomId());
                dto.setRoomName(showtime.getRoom().getRoomName());
            }

            return dto;
        }).collect(Collectors.toList());
    }

    // In ShowtimeController.java
    @PostMapping("/showtimes/delete")
    @Transactional
    public String deleteShowtimes(@RequestParam("selectedIds") List<Long> showtimeIds,
                                  RedirectAttributes redirectAttributes,
                                  @RequestParam(value = "showSuccessModal", required = false) Boolean showSuccessModal) {
        // Check if user has admin role
//        if (!userService.hasRole("ROLE_ADMIN")) {
//            return "redirect:/access-denied";
//        }

        try {
            int deletedCount = showtimeService.deleteShowtimesByIds(showtimeIds);

            // No flash attribute message, only show the modal
            if (showSuccessModal != null && showSuccessModal) {
                return "redirect:/manage-showtimes?deleteSuccess=true";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Error deleting showtimes: " + e.getMessage());
        }

        return "redirect:/manage-showtimes";
    }

    @GetMapping("/showtimes/add")
    public String addShowtimeForm(Model model, HttpServletRequest request) {
        // Check if user has admin role
//        if (!userService.hasRole("ROLE_ADMIN")) {
//            return "redirect:/access-denied";
//        }

        // Add user to model
        addUserToModel(model, request);

        // Add necessary attributes for the form
        model.addAttribute("showtime", new Showtime());
        model.addAttribute("films", filmService.getAllFilms());
        model.addAttribute("cinemas", cinemaService.getAllCinemas());
        model.addAttribute("rooms", roomService.getAllRooms());
        model.addAttribute("currPage", "manage-showtimes");

        return "add-showtime";
    }

    @PostMapping("/showtimes/save")
    public String saveShowtime(@Valid @ModelAttribute("showtime") Showtime showtime,
                               BindingResult bindingResult,
                               Model model,
                               HttpServletRequest request) {
        // Check if user has admin role
//

        // Validate input fields
        if (showtime.getFilm() == null || showtime.getFilm().getFilmId() == null) {
            bindingResult.rejectValue("film", "error.showtime", "Film must be selected");
        }

        if (showtime.getCinema() == null || showtime.getCinema().getCinemaId() == null) {
            bindingResult.rejectValue("cinema", "error.showtime", "Cinema must be selected");
        }

        if (showtime.getRoom() == null || showtime.getRoom().getRoomId() == null) {
            bindingResult.rejectValue("room", "error.showtime", "Room must be selected");
        }

        if (showtime.getShowDate() == null) {
            bindingResult.rejectValue("showDate", "error.showtime", "Show date must be selected");
        }

        if (showtime.getShowTime() == null || showtime.getShowTime().trim().isEmpty()) {
            bindingResult.rejectValue("showTime", "error.showtime", "Show time must be entered");
        }

        // Check if there are validation errors
        if (bindingResult.hasErrors()) {
            // Add necessary attributes back to the form
            addUserToModel(model, request);
            model.addAttribute("films", filmService.getAllFilms());
            model.addAttribute("cinemas", cinemaService.getAllCinemas());
            model.addAttribute("rooms", roomService.getAllRooms());
            model.addAttribute("currPage", "manage-showtimes");
            return "add-showtime";
        }

        // Save the showtime
        showtimeService.saveShowtime(showtime);

        // Add success message to the same page to show the overlay
        model.addAttribute("successMessage", "Showtime has been added successfully.");

        // Return to the same page to show the success overlay
        addUserToModel(model, request);
        model.addAttribute("showtime", new Showtime()); // Reset form with new showtime object
        model.addAttribute("films", filmService.getAllFilms());
        model.addAttribute("cinemas", cinemaService.getAllCinemas());
        model.addAttribute("rooms", roomService.getAllRooms());
        model.addAttribute("currPage", "manage-showtimes");

        return "add-showtime";
    }

    @GetMapping("/showtimes/edit/{id}")
    public String showEditShowtimeForm(@PathVariable("id") Long id,
                                       Model model,
                                       HttpServletRequest request) {
        // Check if user has admin role


        // Add user to model
        addUserToModel(model, request);

        // Get the showtime by ID with eager loading of necessary relations
        Optional<Showtime> showtimeOptional = showtimeService.getShowtimeByIdWithDetails(id);

        if (showtimeOptional.isEmpty()) {
            // Showtime not found, redirect with error message
            return "redirect:/manage-showtimes?error=Showtime+not+found";
        }

        Showtime showtime = showtimeOptional.get();
        // Create a DTO or ensure entity is properly prepared for form binding
        // This avoids the direct binding of complex objects like Film entities

        // Add showtime to the model
        model.addAttribute("showtime", showtime);

        // Add films, cinemas, and rooms for the dropdowns
        model.addAttribute("films", filmService.getAllFilms());
        model.addAttribute("cinemas", cinemaService.getAllCinemas());
        model.addAttribute("rooms", roomService.getAllRooms());

        // Set current page for navigation
        model.addAttribute("currPage", "manage-showtimes");

        return "edit-showtime";
    }

    @PostMapping("/showtimes/update/{id}")
    @ResponseBody
    public Map<String, Object> updateShowtime(@PathVariable("id") Long id,
                                              @Valid @ModelAttribute Showtime showtimeData,
                                              BindingResult bindingResult) {

        Map<String, Object> response = new HashMap<>();

        // Check if user has admin role
        if (!userService.hasRole("ROLE_ADMIN")) {
            response.put("success", false);
            response.put("message", "Access denied");
            return response;
        }

        // Check for validation errors
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .collect(Collectors.toList());

            response.put("success", false);
            response.put("message", "Validation errors");
            response.put("errors", errors);
            return response;
        }

        try {
            // Get the existing showtime with its orders
            Showtime existingShowtime = showtimeService.getShowtimeById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid showtime ID"));

            // Get the entities based on their IDs
            Film film = filmService.getFilmById(showtimeData.getFilm().getFilmId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid film ID"));

            Cinema cinema = cinemaService.getCinemaById(showtimeData.getCinema().getCinemaId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid cinema ID"));

            // Verify that room belongs to the selected cinema
            Room room = roomService.getRoomById(showtimeData.getRoom().getRoomId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid room ID"));

            if (!room.getCinema().getCinemaId().equals(cinema.getCinemaId())) {
                throw new IllegalArgumentException("Room does not belong to the selected cinema");
            }

            // Update the existing showtime with new values while preserving orders
            existingShowtime.setFilm(film);
            existingShowtime.setCinema(cinema);
            existingShowtime.setRoom(room);
            existingShowtime.setShowDate(showtimeData.getShowDate());
            existingShowtime.setShowTime(showtimeData.getShowTime());

            // Save the updated showtime (with orders preserved)
            Showtime updatedShowtime = showtimeService.updateShowtime(existingShowtime);

            response.put("success", true);
            response.put("message", "Showtime updated successfully!");
            response.put("showtimeId", updatedShowtime.getShowtimeId());
            return response;

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error updating showtime: " + e.getMessage());
            return response;
        }
    }

    private void addUserToModel(Model model, HttpServletRequest request) {
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
    }



}