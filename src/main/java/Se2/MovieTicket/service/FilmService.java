package Se2.MovieTicket.service;

import Se2.MovieTicket.dto.FilmDTO;
import Se2.MovieTicket.model.Film;
import Se2.MovieTicket.repository.FilmRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FilmService {
    @Autowired
    private FilmRepository filmRepository;

    public List<Film> getAllFilms() {
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
}