package Se2.MovieTicket.service;

import Se2.MovieTicket.dto.FilmRatingDTO;
import Se2.MovieTicket.model.Film;
import Se2.MovieTicket.model.FilmRating;
import Se2.MovieTicket.model.UserReview;
import Se2.MovieTicket.repository.FilmRatingRepository;
import Se2.MovieTicket.repository.FilmRepository;
import Se2.MovieTicket.repository.UserReviewRepository;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class FilmRatingService {
    @Autowired
    private UserReviewRepository userReviewRepository;

    @Autowired
    private FilmRatingRepository filmRatingRepository;

    @Autowired
    private FilmRepository filmRepository;
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

    @Transactional
    public void updateFilmRating(Long filmId) {

        List<UserReview> reviews = userReviewRepository.findByFilmFilmId(filmId);

        // Calculate totals
        int sumRate = reviews.size();
        int sumStar = reviews.stream().mapToInt(UserReview::getStar).sum();

        // Calculate average rating
        double filmRate = sumRate > 0 ? (double) sumStar / sumRate : 0.0;

        // Round to 1 decimal place
        filmRate = Math.round(filmRate * 10.0) / 10.0;

        // Update or create the film rating
        Optional<FilmRating> ratingOptional = filmRatingRepository.findById(filmId);
        FilmRating rating;

        if (ratingOptional.isPresent()) {
            rating = ratingOptional.get();
            rating.setSumRate(sumRate);
            rating.setSumStar(sumStar);
            rating.setFilmRate(filmRate);
        } else {
            rating = new FilmRating();
            rating.setFilmId(filmId);

            Optional<Film> filmOptional = filmRepository.findById(filmId);
            if (filmOptional.isPresent()) {
                rating.setFilm(filmOptional.get());
            } else {
                return;
            }

            rating.setSumRate(sumRate);
            rating.setSumStar(sumStar);
            rating.setFilmRate(filmRate);
        }

        filmRatingRepository.save(rating);
    }

    public FilmRating getFilmRatingById(Long filmId) {
        return filmRatingRepository.findById(filmId).orElse(null);
    }

    @Transactional
    public void updateFilmRating(Long filmId, Integer newStarValue) {
        FilmRating rating = filmRatingRepository.findById(filmId).orElse(null);
        if (rating != null) {
            rating.setSumRate(rating.getSumRate() + 1);

            rating.setSumStar(rating.getSumStar() + newStarValue);

            // Recalculate average rating
            double newRating = (double) rating.getSumStar() / rating.getSumRate();
            rating.setFilmRate(newRating);

            filmRatingRepository.save(rating);
        }
    }
}
