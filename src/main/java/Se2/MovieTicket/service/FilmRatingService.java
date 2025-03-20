package Se2.MovieTicket.service;

import Se2.MovieTicket.dto.FilmRatingDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class FilmRatingService {

    private final List<FilmRatingDTO> filmRatings = new ArrayList<>();

    public Optional<FilmRatingDTO> getFilmRatingByFilmId(Long filmId) {
        return filmRatings.stream().filter(rating -> rating.getFilmId().equals(filmId)).findFirst();
    }

    public FilmRatingDTO createFilmRating(FilmRatingDTO filmRatingDTO) {
        filmRatings.add(filmRatingDTO);
        return filmRatingDTO;
    }

    public FilmRatingDTO updateFilmRating(Long filmId, FilmRatingDTO filmRatingDTO) {
        Optional<FilmRatingDTO> existingRating = getFilmRatingByFilmId(filmId);
        if (existingRating.isPresent()) {
            filmRatings.remove(existingRating.get());
            filmRatings.add(filmRatingDTO);
            return filmRatingDTO;
        }
        throw new RuntimeException("Film rating not found");
    }

    public void deleteFilmRating(Long filmId) {
        filmRatings.removeIf(rating -> rating.getFilmId().equals(filmId));
    }

    public List<FilmRatingDTO> getAllFilmRatings() {
        return new ArrayList<>(filmRatings);
    }
}
