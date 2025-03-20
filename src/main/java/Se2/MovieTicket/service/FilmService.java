package Se2.MovieTicket.service;
import org.springframework.data.domain.PageImpl;
import Se2.MovieTicket.dto.FilmDTO;
import Se2.MovieTicket.model.Film;
import Se2.MovieTicket.repository.FilmRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class FilmService {
    @Autowired
    private FilmRepository filmRepository;

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
        List<Film> films = getFilmsByType("Now Showing");
        return getPagedFilms(films, page, size);
    }

    public Page<Film> getComingSoonFilms(int page, int size) {
        List<Film> films = getFilmsByType("Coming Soon");
        return getPagedFilms(films, page, size);
    }

    private Page<Film> getPagedFilms(List<Film> films, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), films.size());
        List<Film> pagedList = films.subList(start, end);
        return new PageImpl<>(pagedList, pageable, films.size());
    }
}