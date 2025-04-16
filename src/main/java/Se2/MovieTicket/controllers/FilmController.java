package Se2.MovieTicket.controllers;

import Se2.MovieTicket.dto.*;
import Se2.MovieTicket.impl.UserDetailsImpl;
import Se2.MovieTicket.model.*;
import Se2.MovieTicket.repository.FilmRepository;
import Se2.MovieTicket.service.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/")
public class FilmController {
    @Autowired
    private UserService userService;

    @Autowired
    private final FilmService filmService;

    @Autowired
    private ShowtimeService showtimeService;

    @Autowired
    private CinemaService cinemaService;


    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping("/manage-movies")
    public String manageMovies(
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false) String searchQuery,
            @RequestParam(required = false) String searchCriteria,
            Model model,
            HttpServletRequest request) {

        addUserToModel(model, request);

        Pageable pageable = PageRequest.of(page - 1, size);
        Page<FilmDTO> filmsPage;

        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            filmsPage = filmService.searchFilms(searchQuery, pageable).map(filmService::convertToDTO);
        } else {
            filmsPage = filmService.getAllFilms(pageable);
        }

        List<FilmDTO> films = new ArrayList<>(filmsPage.getContent());

        if (searchCriteria != null) {
            switch (searchCriteria) {
                case "name_asc":
                    films.sort(Comparator.comparing(FilmDTO::getFilmName, String.CASE_INSENSITIVE_ORDER));
                    break;
                case "age_low":
                    films.sort(Comparator.comparing(FilmDTO::getAgeLimit, Comparator.nullsLast(Integer::compareTo)));
                    break;
                case "status_now":
                    films.removeIf(f -> f.getReleaseDate() == null || f.getReleaseDate().isAfter(LocalDate.now()));
                    break;
                case "status_soon":
                    films.removeIf(f -> f.getReleaseDate() == null || !f.getReleaseDate().isAfter(LocalDate.now()));
                    break;
                case "normal":
                default:
                    // No filter
                    break;
            }
        }

        model.addAttribute("films", films);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("totalItems", filmsPage.getTotalElements());
        model.addAttribute("totalPages", filmsPage.getTotalPages());
        model.addAttribute("pageSizes", Arrays.asList(5, 10, 20, 50));
        model.addAttribute("currPage", "manage-movies");
        model.addAttribute("searchQuery", searchQuery);
        model.addAttribute("searchCriteria", searchCriteria);

        return "manage-movies";
    }


    @GetMapping("/manage-movies/add-movie")
    public String showAddMovieForm(Model model, HttpServletRequest request) {
        // Create empty FilmDTO for the form
        FilmDTO filmDTO = new FilmDTO();

        // Add to model
        model.addAttribute("film", filmDTO);
        model.addAttribute("allCategories", categoryService.getAllCategories());
        model.addAttribute("allDirectors", directorService.getAllDirectors());
        model.addAttribute("allActors", actorService.getAllActors());
        model.addAttribute("allCountries", getCountriesList());
        addUserToModel(model, request);

        return "add-movie";
    }

    @PostMapping("/manage-movies/add-movie")
    public String addMovie(
            @ModelAttribute FilmDTO filmDTO,
            @RequestParam(value = "posterFile", required = false) MultipartFile posterFile,
            @RequestParam(value = "trailerUrl", required = false) String trailerUrl,
            @RequestParam(value = "directorNames", required = false) List<String> directorNames,
            @RequestParam(value = "actorNames", required = false) List<String> actorNames,
            @RequestParam(value = "categoryNames", required = false) List<String> categoryNames,
            Model model,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {

        // Validate trailer URL if provided
        if (trailerUrl == null || trailerUrl.isEmpty()) {
            model.addAttribute("errorMessage", "Trailer URL is required");
            model.addAttribute("allCategories", categoryService.getAllCategories());
            model.addAttribute("allDirectors", directorService.getAllDirectors());
            model.addAttribute("allActors", actorService.getAllActors());
            model.addAttribute("allCountries", getCountriesList());
            model.addAttribute("film", filmDTO); // Return the current data to the form
            addUserToModel(model, request);
            return "add-movie";
        } else if (!isValidYoutubeUrl(trailerUrl)) {
            model.addAttribute("errorMessage", "Please enter a valid YouTube URL");
            model.addAttribute("allCategories", categoryService.getAllCategories());
            model.addAttribute("allDirectors", directorService.getAllDirectors());
            model.addAttribute("allActors", actorService.getAllActors());
            model.addAttribute("allCountries", getCountriesList());
            model.addAttribute("film", filmDTO); // Return the current data to the form
            addUserToModel(model, request);
            return "add-movie";
        }

        try {
            // Handle image file upload if provided
            if (posterFile != null && !posterFile.isEmpty()) {
                String uploadDir = "src/main/resources/static/images/films/";
                String fileName = StringUtils.cleanPath(Objects.requireNonNull(posterFile.getOriginalFilename()));
                String fileExtension = fileName.substring(fileName.lastIndexOf("."));
                String uniqueFileName = System.currentTimeMillis() + fileExtension;
                Path uploadPath = Paths.get(uploadDir);

                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                try (InputStream inputStream = posterFile.getInputStream()) {
                    Path filePath = uploadPath.resolve(uniqueFileName);
                    Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
                    filmDTO.setFilmImg("/images/films/" + uniqueFileName);
                }
            } else {
                // Set a default image if none provided
                filmDTO.setFilmImg("/images/films/default-movie-poster.jpg");
            }

            // Set the trailer URL
            filmDTO.setFilmTrailer(trailerUrl);

            // Ensure lists are not null
            if (directorNames == null) directorNames = new ArrayList<>();
            if (actorNames == null) actorNames = new ArrayList<>();
            if (categoryNames == null) categoryNames = new ArrayList<>();

            // Save the film first to get an ID
            Film newFilm = filmService.saveFilm(filmDTO);

            if (newFilm != null) {
                // Now handle relationships

                // Add categories
                Set<Category> categories = new HashSet<>();
                for (String categoryName : categoryNames) {
                    Category category = categoryService.findByName(categoryName);
                    if (category != null) {
                        categories.add(category);
                    } else {
                        // Create new category if it doesn't exist
                        Category newCategory = new Category();
                        newCategory.setCategoryName(categoryName);
                        categories.add(categoryService.saveCategory(newCategory));
                    }
                }

                // Add category relationships
                for (Category category : categories) {
                    filmRepository.addCategoryToFilm(newFilm.getFilmId(), category.getCategoryId());
                }

                // Add directors
                Set<Director> directors = new HashSet<>();
                for (String directorName : directorNames) {
                    Director director = directorService.findByName(directorName);
                    if (director != null) {
                        directors.add(director);
                    } else {
                        // Create new director if it doesn't exist
                        Director newDirector = new Director();
                        newDirector.setDirectorName(directorName);
                        directors.add(directorService.saveDirector(newDirector));
                    }
                }

                // Add director relationships
                for (Director director : directors) {
                    filmRepository.addDirectorToFilm(newFilm.getFilmId(), director.getDirectorId());
                }

                // Add actors
                Set<Actor> actors = new HashSet<>();
                for (String actorName : actorNames) {
                    Actor actor = actorService.findByName(actorName);
                    if (actor != null) {
                        actors.add(actor);
                    } else {
                        // Create new actor if it doesn't exist
                        Actor newActor = new Actor();
                        newActor.setActorName(actorName);
                        actors.add(actorService.saveActor(newActor));
                    }
                }
                filmService.updateFilmActors(newFilm.getFilmId(), actors);

                // Add success message
                redirectAttributes.addFlashAttribute("successMessage", "Movie added successfully!");
                return "redirect:/manage-movies/add-movie?success=true";
            } else {
                // Add error message
                redirectAttributes.addFlashAttribute("errorMessage", "Failed to add movie.");
                return "redirect:/manage-movies/add-movie?error=true";
            }
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "An error occurred: " + e.getMessage());
            return "redirect:/manage-movies/add-movie?error=true";
        }
    }
    @Autowired
    private CategoryService categoryService;


    @Autowired
    private DirectorService directorService;

    @Autowired
    private ActorService actorService;

    @GetMapping("/manage-movies/edit-movie/{id}")
    public String showEditMovieForm(@PathVariable Long id, Model model, HttpServletRequest request) {
        Optional<Film> filmOpt = filmService.getFilmById(id);

        if (filmOpt.isPresent()) {
            // Convert to DTO for the form
            FilmDTO filmDTO = filmService.convertToDTO(filmOpt.get());

            // Add the film DTO to the model
            model.addAttribute("film", filmDTO);

            // Add all categories for the dropdown
            model.addAttribute("allCategories", categoryService.getAllCategories());

            // Add all directors for the dropdown
            model.addAttribute("allDirectors", directorService.getAllDirectors());

            // Add all actors for the dropdown
            model.addAttribute("allActors", actorService.getAllActors());

            // Add countries list
            model.addAttribute("allCountries", getCountriesList());

            // Add user to model
            addUserToModel(model, request);

            return "edit-movie";
        }

        return "redirect:/manage-movies?error=true";
    }

    @Autowired
    private FilmRepository filmRepository;


    @PostMapping("/manage-movies/edit-movie/{id}")
    public String updateMovie(
            @PathVariable Long id,
            @ModelAttribute FilmDTO filmDTO,
            @RequestParam(value = "posterFile", required = false) MultipartFile posterFile,
            @RequestParam(value = "trailerUrl", required = false) String trailerUrl,
            @RequestParam(value = "directorNames", required = false) List<String> directorNames,
            @RequestParam(value = "actorNames", required = false) List<String> actorNames,
            @RequestParam(value = "categoryNames", required = false) List<String> categoryNames,
            Model model,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {

        // Validate trailer URL if provided
        if (trailerUrl == null || trailerUrl.isEmpty()) {
            model.addAttribute("errorMessage", "Trailer URL is required");
            model.addAttribute("allCategories", categoryService.getAllCategories());
            model.addAttribute("allDirectors", directorService.getAllDirectors());
            model.addAttribute("allActors", actorService.getAllActors());
            model.addAttribute("allCountries", getCountriesList());
            model.addAttribute("film", filmDTO); // Return the current data to the form
            addUserToModel(model, request);
            return "edit-movie";
        } else if (!isValidYoutubeUrl(trailerUrl)) {
            model.addAttribute("errorMessage", "Please enter a valid YouTube URL");
            model.addAttribute("allCategories", categoryService.getAllCategories());
            model.addAttribute("allDirectors", directorService.getAllDirectors());
            model.addAttribute("allActors", actorService.getAllActors());
            model.addAttribute("allCountries", getCountriesList());
            model.addAttribute("film", filmDTO); // Return the current data to the form
            addUserToModel(model, request);
            return "edit-movie";
        }

        try {
            // Get the existing film first
            Optional<Film> existingFilmOpt = filmRepository.findById(id);
            if (!existingFilmOpt.isPresent()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Film not found");
                return "redirect:/manage-movies";
            }

            Film existingFilm = existingFilmOpt.get();

            // Handle image file upload if provided
            if (posterFile != null && !posterFile.isEmpty()) {
                String uploadDir = "src/main/resources/static/uploads/films/";
                String fileName = StringUtils.cleanPath(Objects.requireNonNull(posterFile.getOriginalFilename()));
                String fileExtension = fileName.substring(fileName.lastIndexOf("."));
                String uniqueFileName = System.currentTimeMillis() + "_" + id + fileExtension;
                Path uploadPath = Paths.get(uploadDir);

                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                try (InputStream inputStream = posterFile.getInputStream()) {
                    Path filePath = uploadPath.resolve(uniqueFileName);
                    Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
                    filmDTO.setFilmImg("/uploads/films/" + uniqueFileName);
                }
            }

            // Set the trailer URL
            filmDTO.setFilmTrailer(trailerUrl);

            // Ensure lists are not null
            if (directorNames == null) directorNames = new ArrayList<>();
            if (actorNames == null) actorNames = new ArrayList<>();
            if (categoryNames == null) categoryNames = new ArrayList<>();

            // Update the basic film data first
            Film updatedFilm = filmService.updateFilm(id, filmDTO);

            if (updatedFilm != null) {
                // Now handle relationships manually since updateFilm doesn't process them

                // Update categories
                Set<Category> categories = new HashSet<>();
                for (String categoryName : categoryNames) {
                    Category category = categoryService.findByName(categoryName);
                    if (category != null) {
                        categories.add(category);
                    } else {
                        // Create new category if it doesn't exist
                        Category newCategory = new Category();
                        newCategory.setCategoryName(categoryName);
                        categories.add(categoryService.saveCategory(newCategory));
                    }
                }

// Delete existing category relationships
                filmRepository.deleteAllCategoriesByFilmId(updatedFilm.getFilmId());

// Add new category relationships
                for (Category category : categories) {
                    filmRepository.addCategoryToFilm(updatedFilm.getFilmId(), category.getCategoryId());
                }
                // Update directors
                Set<Director> directors = new HashSet<>();
                for (String directorName : directorNames) {
                    Director director = directorService.findByName(directorName);
                    if (director != null) {
                        directors.add(director);
                    } else {
                        // Create new director if it doesn't exist
                        Director newDirector = new Director();
                        newDirector.setDirectorName(directorName);
                        directors.add(directorService.saveDirector(newDirector));
                    }
                }

// Delete existing director relationships
                filmRepository.deleteAllDirectorsByFilmId(updatedFilm.getFilmId());

// Add new director relationships
                for (Director director : directors) {
                    filmRepository.addDirectorToFilm(updatedFilm.getFilmId(), director.getDirectorId());
                }
                // Update actors
                Set<Actor> actors = new HashSet<>();
                for (String actorName : actorNames) {
                    Actor actor = actorService.findByName(actorName);
                    if (actor != null) {
                        actors.add(actor);
                    } else {
                        // Create new actor if it doesn't exist
                        Actor newActor = new Actor();
                        newActor.setActorName(actorName);
                        actors.add(actorService.saveActor(newActor));
                    }
                }
                filmService.updateFilmActors(updatedFilm.getFilmId(), actors);

// Save the updated film with relationships
                filmRepository.save(updatedFilm);

                // Add success message
                redirectAttributes.addFlashAttribute("successMessage", "Movie updated successfully!");
                return "redirect:/manage-movies/edit-movie/" + id + "?success=true";
            } else {
                // Add error message
                redirectAttributes.addFlashAttribute("errorMessage", "Failed to update movie.");
                return "redirect:/manage-movies/edit-movie/" + id + "?error=true";
            }
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "An error occurred: " + e.getMessage());
            return "redirect:/manage-movies/edit-movie/" + id + "?error=true";
        }
    }


    // Helper method to validate YouTube URL
    private boolean isValidYoutubeUrl(String url) {
        try {
            new URL(url).toURI();
            // Check if it's a YouTube URL
            return url.matches("^(https?://)?((www\\.)?youtube\\.com/watch\\?v=|youtu\\.be/)[a-zA-Z0-9_-]{11}.*$");
        } catch (Exception e) {
            return false;
        }
    }
    // Helper method to get countries list
    private List<String> getCountriesList() {
        // Use Locale to get all available countries
        return Arrays.stream(Locale.getISOCountries())
                .map(countryCode -> new Locale("", countryCode).getDisplayCountry())
                .sorted()
                .collect(Collectors.toList());
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

    @GetMapping("/api/filtered-movies")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getFilteredMovies(
            @RequestParam String criteria,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page - 1, size);
        Page<FilmDTO> filmsPage = filmService.filteredFilms(criteria, pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("films", filmsPage.getContent());
        response.put("totalPages", filmsPage.getTotalPages());
        response.put("totalItems", filmsPage.getTotalElements());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/View-movie-ticket")
    public String viewMovieTicket(
            @RequestParam("id") Long filmId,
            @RequestParam(required = false) String date,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year,
            Model model, HttpServletRequest request) {

        model.addAttribute("currPage", "showtime");
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

        // Xử lý date, month, year
        LocalDate selectedDate;
        YearMonth yearMonth;
        LocalDate today = LocalDate.now();

        // Xử lý tham số ngày và tháng
        if (date != null && !date.isEmpty()) {
            try {
                selectedDate = LocalDate.parse(date);
            } catch (DateTimeParseException e) {
                selectedDate = today;
            }
        } else {
            selectedDate = today;
        }

        // Xử lý tham số tháng và năm
        if (month != null && year != null) {
            try {
                yearMonth = YearMonth.of(year, month);
            } catch (DateTimeException e) {
                yearMonth = YearMonth.of(selectedDate.getYear(), selectedDate.getMonth());
            }
        } else {
            yearMonth = YearMonth.of(selectedDate.getYear(), selectedDate.getMonth());
        }

        // Fetch film and convert to DTO
        Optional<Film> filmOptional = filmService.getFilmById(filmId);
        if (filmOptional.isPresent()) {
            Film film = filmOptional.get();

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

            // Extract related data using DTOs
            filmDTO.setDirectorNames(film.getFilmDirectors().stream()
                    .map(fd -> fd.getDirector().getDirectorName())
                    .collect(Collectors.toList()));

            filmDTO.setActorNames(film.getFilmActors().stream()
                    .map(fa -> fa.getActor().getActorName())
                    .collect(Collectors.toList()));
// Add new code: Extract Director DTOs with IDs
            List<DirectorDTO> directors = film.getFilmDirectors().stream()
                    .map(fd -> {
                        DirectorDTO dto = new DirectorDTO();
                        dto.setDirectorId(fd.getDirector().getDirectorId());
                        dto.setDirectorName(fd.getDirector().getDirectorName());
                        return dto;
                    })
                    .collect(Collectors.toList());
            filmDTO.setDirectors(directors);

            // Add new code: Extract Actor DTOs with IDs
            List<ActorDTO> actors = film.getFilmActors().stream()
                    .map(fa -> {
                        ActorDTO dto = new ActorDTO();
                        dto.setActorId(fa.getActor().getActorId());
                        dto.setActorName(fa.getActor().getActorName());
                        return dto;
                    })
                    .collect(Collectors.toList());
            filmDTO.setActors(actors);
            filmDTO.setCategoryNames(film.getFilmCategories().stream()
                    .map(fc -> fc.getCategory().getCategoryName())
                    .collect(Collectors.toList()));

            model.addAttribute("film", filmDTO);
        } else {
            return "redirect:/films";
        }

        // Fetch cinema/showtimes dựa trên date được chọn
        List<CinemaWithShowtimesDTO> cinemasWithShowtimes = cinemaService.getCinemasWithShowtimesForFilmAndDate(filmId, selectedDate);
        model.addAttribute("cinemas", cinemasWithShowtimes);

        // Add calendar data với tháng và năm được chọn
        model.addAttribute("currentMonth", yearMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault()));
        model.addAttribute("currentYear", yearMonth.getYear());
        model.addAttribute("currentMonthNum", yearMonth.getMonthValue());
        model.addAttribute("weekDays", getWeekDays());
        model.addAttribute("weeks", getCalendarWeeks(yearMonth, selectedDate, filmId));
        model.addAttribute("selectedDate", selectedDate.toString());

        return "View-movie-ticket";
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

    private List<WeekDTO> getCalendarWeeks(YearMonth yearMonth, LocalDate selectedDate, Long filmId) {
        List<WeekDTO> weeks = new ArrayList<>();

        LocalDate today = LocalDate.now();
        LocalDate firstDayOfMonth = yearMonth.atDay(1);
        LocalDate lastDayOfMonth = yearMonth.atEndOfMonth();

        // Lấy ngày đầu tiên của tuần từ thứ Hai
        LocalDate firstCalendarDate = firstDayOfMonth.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        LocalDate date = firstCalendarDate;
        while (!date.isAfter(lastDayOfMonth)) {
            List<DayDTO> days = new ArrayList<>();

            for (int i = 0; i < 7; i++) {
                boolean isToday = date.equals(today);
                boolean isPast = date.isBefore(today);
                boolean isCurrentMonth = date.getMonth() == yearMonth.getMonth();
                boolean hasShowtimes = showtimeService.hasShowtimesForFilmAndDate(filmId, date);
                String formattedDate = date.toString();  // yyyy-MM-dd format
                String shortName = date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.getDefault());

                days.add(new DayDTO(String.valueOf(date.getDayOfMonth()),   // dayOfMonth (số ngày)
                        shortName,                             // shortName (Mon, Tue...)
                        isToday,                               // isToday
                        isPast,                                // isPast
                        hasShowtimes,                          // hasShowtimes
                        formattedDate,                         // fullDate
                        date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY,  // isWeekend
                        date.equals(selectedDate),             // isSelected
                        isCurrentMonth                         // isCurrentMonth (để làm mờ các ngày không thuộc tháng hiện tại)
                ));

                date = date.plusDays(1);  // Chuyển sang ngày tiếp theo
            }
            weeks.add(new WeekDTO(days));  // Thêm tuần với danh sách các ngày
        }
        return weeks;
    }
}