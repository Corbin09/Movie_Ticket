
package Se2.MovieTicket.service;
import Se2.MovieTicket.dto.ShowtimeDTO;
import Se2.MovieTicket.model.*;
import Se2.MovieTicket.repository.ShowtimeRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.*;
import Se2.MovieTicket.dto.FilmDTO;
import Se2.MovieTicket.repository.FilmRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.Duration;
import java.time.Instant;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FilmService {
    @Autowired
    private FilmRepository filmRepository;
    @Autowired
    private ShowtimeRepository showtimeRepository;

    @Autowired
    private EntityManager em;

    public List<Film> filterFilms(String name, Date releaseDate, String country, String type, Integer age) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Film> cq = cb.createQuery(Film.class);
        Root<Film> film = cq.from(Film.class);

        List<Predicate> predicates = new ArrayList<>();
        if (name != null && !name.isEmpty()) {
            predicates.add(cb.like(film.get("filmName"), "%" + name + "%"));
        }
        if (releaseDate != null) {
            predicates.add(cb.greaterThanOrEqualTo(film.get("releaseDate"), releaseDate));
        }

        if (country != null && !country.isEmpty()) {
            predicates.add(cb.equal(film.get("country"), country));
        }
        if (type != null && !type.isEmpty()) {
            predicates.add(cb.equal(film.get("filmType"), type));
        }
        if (age != null) {
            predicates.add(cb.equal(film.get("ageLimit"), age));
        }

        cq.where(predicates.toArray(new Predicate[0]));
        return em.createQuery(cq).getResultList();
    }

    public List<Film> getAllFilms() {
        return filmRepository.findAll();
    }

    public List<Film> searchFilms(String name) {
        if (name != null && !name.isEmpty()) {
            return filmRepository.searchByFilmName(name);
        }
        // Add other filters as needed
        return filmRepository.findAll();
    }


    // In FilmService
    private List<Film> cachedFilms;
    private Instant cacheTimestamp;
    private static final long CACHE_DURATION_MINUTES = 30;

    public List<Film> getCachedFilms() {
        // Cache films for 30 minutes to avoid repeated database queries
        if (cachedFilms == null ||
                cacheTimestamp == null ||
                Duration.between(cacheTimestamp, Instant.now()).toMinutes() > CACHE_DURATION_MINUTES) {

            cachedFilms = filmRepository.findAll();
            cacheTimestamp = Instant.now();
        }
        return cachedFilms;
    }

    // Optional: Add pagination for films if the list is large
    public List<Film> getPaginatedFilms(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return filmRepository.findAll(pageable).getContent();
    }

    public Optional<Film> getFilmById(Long id) {
        return filmRepository.findById(id);

    }

    public Film createFilm(FilmDTO filmDTO) {
        Film film = new Film();
        film.setFilmName(filmDTO.getFilmName());
        film.setFilmImg(filmDTO.getFilmImg());
        film.setFilmTrailer(filmDTO.getFilmTrailer());
        film.setReleaseDate(filmDTO.getReleaseDate());
        film.setFilmDescription(filmDTO.getFilmDescription());
        film.setAgeLimit(filmDTO.getAgeLimit());
        film.setDuration(filmDTO.getDuration());
        film.setFilmType(filmDTO.getFilmType());
        film.setCountry(filmDTO.getCountry());
        return filmRepository.save(film);
    }

    public Film updateFilm(Long id, FilmDTO filmDTO) {
        Optional<Film> filmData = filmRepository.findById(id);
        if (filmData.isPresent()) {
            Film film = filmData.get();
            film.setFilmName(filmDTO.getFilmName());
            film.setFilmImg(filmDTO.getFilmImg());
            film.setFilmTrailer(filmDTO.getFilmTrailer());
            film.setReleaseDate(filmDTO.getReleaseDate());
            film.setFilmDescription(filmDTO.getFilmDescription());
            film.setAgeLimit(filmDTO.getAgeLimit());
            film.setDuration(filmDTO.getDuration());
            film.setFilmType(filmDTO.getFilmType());
            film.setCountry(filmDTO.getCountry());
            return filmRepository.save(film);
        }
        return null;
    }

    public void deleteFilm(Long id) {
        filmRepository.deleteById(id);
    }


    private static final Logger logger = LoggerFactory.getLogger(FilmService.class);

    public List<Film> getFilmsByType(String type) {
        if (type != null && !type.isEmpty()) {
            List<Film> films = filmRepository.findByFilmType(type);
            if (!films.isEmpty()) {
                logger.info("Số lượng phim '{}' tìm thấy: {}", type, films.size());
            } else {
                logger.warn("Không tìm thấy phim nào thuộc thể loại '{}'.", type);
            }
            return films;
        }
        return new ArrayList<>();
    }

    public Page<Film> getNowShowingFilms(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        return filmRepository.findByFilmType("Now Showing", pageable);
    }

    public Page<Film> getComingSoonFilms(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        return filmRepository.findByFilmType("Coming Soon", pageable);
    }


    private Page<Film> getPagedFilms(List<Film> films, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), films.size());
        List<Film> pagedList = films.subList(start, end);
        return new PageImpl<>(pagedList, pageable, films.size());
    }

    public List<Film> findNowShowingFilms() {
        Date currentDate = new Date();
        return filmRepository.findByReleaseDateBeforeOrderByReleaseDateDesc(currentDate);
    }

    public List<Film> findComingSoonFilms() {
        Date currentDate = new Date();
        return filmRepository.findByReleaseDateAfterOrderByReleaseDateAsc(currentDate);
    }

    public Page<Film> findNowShowingFilmsWithPagination(Pageable pageable) {
        Date currentDate = new Date();
        return filmRepository.findByReleaseDateBeforeOrderByReleaseDateDesc(currentDate, pageable);
    }

    public Page<Film> findComingSoonFilmsWithPagination(Pageable pageable) {
        Date currentDate = new Date();
        return filmRepository.findByReleaseDateAfterOrderByReleaseDateAsc(currentDate, pageable);
    }

    // New search method
    public Page<Film> searchFilms(String query, Pageable pageable) {
        // Convert query to lowercase for case-insensitive search
        String searchQuery = "%" + query.toLowerCase() + "%";
        return filmRepository.findBySearchTerm(searchQuery, pageable);
    }

    public Page<FilmDTO> getFilmsWithShowtimesByDate(LocalDate selectedDate, Pageable pageable) {
        // Find all films that have showtimes on the selected date
        Date date = java.sql.Date.valueOf(selectedDate);

        // Get films from repository with showtimes matching the date
        Page<Film> films = filmRepository.findFilmsWithShowtimesByDate(date, pageable);

        // Convert to DTOs with showtime information
        return films.map(film -> {
            FilmDTO filmDTO = convertToDTO(film);

            // Extract director names
            if (film.getFilmDirectors() != null) {
                List<String> directorNames = film.getFilmDirectors().stream()
                        .map(director -> director.getDirector().getDirectorName())
                        .collect(java.util.stream.Collectors.toList());
                filmDTO.setDirectorNames(directorNames);
            }

            // Extract actor names
            if (film.getFilmActors() != null) {
                List<String> actorNames = film.getFilmActors().stream()
                        .map(actor -> actor.getActor().getActorName())
                        .collect(java.util.stream.Collectors.toList());
                filmDTO.setActorNames(actorNames);
            }

            // Extract category names
            if (film.getFilmCategories() != null) {
                List<String> categoryNames = film.getFilmCategories().stream()
                        .map(category -> category.getCategory().getCategoryName())
                        .collect(java.util.stream.Collectors.toList());
                filmDTO.setCategoryNames(categoryNames);
            }

            return filmDTO;
        });
    }

    public Page<FilmDTO> getFilmsByRegionAndDate(Long regionId, LocalDate selectedDate, Pageable pageable) {
        // Find all films that have showtimes in the specified region on the selected date
        Date date = java.sql.Date.valueOf(selectedDate);

        // Get films from repository with showtimes matching the region and date
        Page<Film> films = filmRepository.findFilmsByRegionAndDate(regionId, date, pageable);

        // Convert to DTOs with relevant information
        return films.map(film -> {
            FilmDTO filmDTO = convertToDTO(film);

            // Extract director names
            if (film.getFilmDirectors() != null) {
                List<String> directorNames = film.getFilmDirectors().stream()
                        .map(director -> director.getDirector().getDirectorName())
                        .collect(java.util.stream.Collectors.toList());
                filmDTO.setDirectorNames(directorNames);
            }

            // Extract actor names
            if (film.getFilmActors() != null) {
                List<String> actorNames = film.getFilmActors().stream()
                        .map(actor -> actor.getActor().getActorName())
                        .collect(java.util.stream.Collectors.toList());
                filmDTO.setActorNames(actorNames);
            }

            // Extract category names
            if (film.getFilmCategories() != null) {
                List<String> categoryNames = film.getFilmCategories().stream()
                        .map(category -> category.getCategory().getCategoryName())
                        .collect(java.util.stream.Collectors.toList());
                filmDTO.setCategoryNames(categoryNames);
            }

            return filmDTO;
        });
    }


    public FilmDTO convertToDTO(Film film) {
        FilmDTO filmDTO = new FilmDTO();
        filmDTO.setFilmId(film.getFilmId());
        filmDTO.setFilmName(film.getFilmName());
        filmDTO.setFilmImg(film.getFilmImg());
        filmDTO.setFilmTrailer(film.getFilmTrailer());
        filmDTO.setReleaseDate(film.getReleaseDate());
        filmDTO.setFilmDescription(film.getFilmDescription());
        filmDTO.setAgeLimit(film.getAgeLimit());
        filmDTO.setDuration(film.getDuration());
        filmDTO.setFilmType(film.getFilmType());
        filmDTO.setCountry(film.getCountry());
        filmDTO.setDirectorNames(film.getFilmDirectors().stream()
                .map(filmDirector -> filmDirector.getDirector().getDirectorName())
                .collect(Collectors.toList()));
        filmDTO.setActorNames(film.getFilmActors().stream()
                .map(filmActor -> filmActor.getActor().getActorName())
                .collect(Collectors.toList()));

        // Lấy danh sách thể loại phim
        filmDTO.setCategoryNames(film.getFilmCategories().stream()
                .map(filmCategory -> filmCategory.getCategory().getCategoryName())
                .collect(Collectors.toList()));


        filmDTO.setAverageRating(film.getFilmRating() != null ? film.getFilmRating().getFilmRate() : null);

        filmDTO.setShowtimes(film.getShowtimes().stream()
                .map(showtime -> new ShowtimeDTO(showtime.getShowtimeId(), showtime.getShowDate(), showtime.getShowTime(),
                        showtime.getFilm().getFilmId(), showtime.getFilm().getFilmName(),
                        showtime.getRoom().getRoomId(), showtime.getRoom().getRoomName(),
                        showtime.getCinema().getCinemaId(), showtime.getCinema().getCinemaName(),
                        null)) // Null cho giá trị price nếu chưa có
                .collect(Collectors.toList()));

        filmDTO.setNews(film.getNews());
        filmDTO.setUserReviews(film.getUserReviews());

        return filmDTO;
    }
    public Page<FilmDTO> getAllFilms(Pageable pageable) {
        Page<Film> films = filmRepository.findAll(pageable);
        return films.map(this::convertToDTO);
    }


    public Page<FilmDTO> getFilmsByRegionThroughShowtimes(Long regionId, Pageable pageable) {
        // Get all films that have showtimes in any cinema within the given region
        Page<Film> films = filmRepository.findFilmsByRegionId(regionId, pageable);
        return films.map(this::convertToDTO);
    }

    public Page<FilmDTO> getFilmsByCinemaThroughShowtimes(Long cinemaId, Pageable pageable) {
        // Get all films that have showtimes in the given cinema
        Page<Film> films = filmRepository.findFilmsByCinemaId(cinemaId, pageable);
        return films.map(this::convertToDTO);
    }


    public List<Film> getLikedFilmsByUserId(Long userId) {
        return filmRepository.findLikedFilmsByUserId(userId);
    }

    public List<String> getCategoryNamesByFilmId(Long filmId) {
        Film film = filmRepository.findById(filmId).orElse(null);
        if (film != null) {
            Optional<Object> categoriesOpt = film.getCategories();
            if (categoriesOpt.isPresent() && categoriesOpt.get() instanceof List<?>) {
                List<?> categories = (List<?>) categoriesOpt.get();
                return categories.stream()
                        .filter(obj -> obj instanceof Category)
                        .map(obj -> ((Category) obj).getCategoryName())
                        .collect(Collectors.toList());
            }
        }
        return new ArrayList<>();
    }


    /**
     * Get films available in a specific region
     */
    public Page<FilmDTO> getFilmsByRegion(Long regionId, Pageable pageable) {
        Page<Film> films = filmRepository.findFilmsByRegionId(regionId, pageable);
        return films.map(this::convertToDTO);
    }

    /**
     * Get films available in a specific cinema
     */
    public Page<FilmDTO> getFilmsByCinema(Long cinemaId, Pageable pageable) {
        Page<Film> films = filmRepository.findFilmsByCinemaId(cinemaId, pageable);
        return films.map(this::convertToDTO);
    }
    public Page<FilmDTO> getFilmsByShowTime(String showTime, Long cinemaId, Long regionId, Pageable pageable) {
        return filmRepository.findFilmsByShowTime(showTime, cinemaId, regionId, pageable);
    }

    public List<Showtime> getAllShowtimesByCinemaAndFilm(Long cinemaId, Long filmId) {
        return showtimeRepository.findByCinema_CinemaIdAndFilm_FilmId(cinemaId, filmId);
    }
    /**
     * Get films with showtimes on a specific date in a specific cinema
     */
    public Page<FilmDTO> getFilmsByCinemaAndDate(Long cinemaId, LocalDate date, Pageable pageable) {
        Page<Film> films = filmRepository.findFilmsByCinemaAndDate(cinemaId, date, pageable);
        return films.map(this::convertToDTO);
    }
    public Page<Film> getFilmsByActorId(Long actorId, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        return filmRepository.findFilmsByActorId(actorId, pageable);
    }

    public Page<Film> getFilmsByDirectorId(Long directorId, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        return filmRepository.findByDirectorIdPage(directorId, pageable);
    }

    public Film findById(Long filmId) {
        if (filmId == null) {
            throw new IllegalArgumentException("Film ID cannot be null");
        }

        return filmRepository.findById(filmId)
                .orElseThrow(() -> new EntityNotFoundException("Film not found with ID: " + filmId));
    }
}