package Se2.MovieTicket.controllers;


import Se2.MovieTicket.model.FilmCategory;
import Se2.MovieTicket.model.FilmCategoryId;
import Se2.MovieTicket.repository.FilmCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/film-categories")
public class FilmCategoryController {

    @Autowired
    private FilmCategoryRepository filmCategoryRepository;

    @GetMapping
    public ResponseEntity<List<FilmCategory>> getAllFilmCategories() {
        try {
            List<FilmCategory> filmCategories = new ArrayList<>(filmCategoryRepository.findAll());

            if (filmCategories.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(filmCategories, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{filmId}/{categoryId}")
    public ResponseEntity<FilmCategory> getFilmCategoryById(@PathVariable("filmId") Long filmId, @PathVariable("categoryId") Long categoryId) {
        Optional<FilmCategory> filmCategoryData = filmCategoryRepository.findByFilmIdAndCategoryId(filmId, categoryId);

        return filmCategoryData.map(filmCategory -> new ResponseEntity<>(filmCategory, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/film/{filmId}")
    public ResponseEntity<List<FilmCategory>> getCategoriesByFilmId(@PathVariable("filmId") Long filmId) {
        try {
            List<FilmCategory> filmCategories = filmCategoryRepository.findByFilmId(filmId);

            if (filmCategories.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(filmCategories, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<FilmCategory>> getFilmsByCategoryId(@PathVariable("categoryId") Long categoryId) {
        try {
            List<FilmCategory> filmCategories = filmCategoryRepository.findByCategoryId(categoryId);

            if (filmCategories.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(filmCategories, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping
    public ResponseEntity<FilmCategory> createFilmCategory(@RequestBody FilmCategory filmCategory) {
        try {
            FilmCategory newFilmCategory = filmCategoryRepository.save(filmCategory);
            return new ResponseEntity<>(newFilmCategory, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{filmId}/{categoryId}")
    public ResponseEntity<HttpStatus> deleteFilmCategory(
            @PathVariable("filmId") Long filmId,
            @PathVariable("categoryId") Long categoryId) {
        try {
            FilmCategoryId filmCategoryId = new FilmCategoryId(filmId, categoryId);
            filmCategoryRepository.deleteById(filmCategoryId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}