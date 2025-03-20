package Se2.MovieTicket.controllers;

import Se2.MovieTicket.Model.Movie;
import Se2.MovieTicket.Repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping(value = "/")
public class HomeController {
    @Autowired
    private MovieRepository movieRepository;

    @RequestMapping(value = "/")
    public String getHomePage(Model model) {
        // Banner images
        List<String> bannerImages = Arrays.asList(
                "/images/NhaGiaTien.png",
                "/images/DarkNun.png",
                "/images/Den.webp"
        );
        model.addAttribute("bannerImages", bannerImages);

        // Get a sample of movies for initial view
        List<Movie> nowShowingMovies = movieRepository.findByStatus("Now Showing",
                PageRequest.of(0, 4));
        List<Movie> comingSoonMovies = movieRepository.findByStatus("Coming Soon",
                PageRequest.of(0, 4));

        model.addAttribute("nowShowingMovies", nowShowingMovies);
        model.addAttribute("comingSoonMovies", comingSoonMovies);

        // Set default pagination values
        model.addAttribute("nowShowingCurrentPage", 1);
        model.addAttribute("nowShowingTotalPages", 3); // Replace with actual count from DB
        model.addAttribute("comingSoonCurrentPage", 1);
        model.addAttribute("comingSoonTotalPages", 3); // Replace with actual count from DB

        return "index";
    }

    @RequestMapping(value = "/movies")
    public String getAllMovies(
            @RequestParam(value = "category", required = false, defaultValue = "all") String category,
            @RequestParam(value = "sort", required = false, defaultValue = "0") int sortMode,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            Model model) {

        // Default values
        Sort.Direction sortOrder = Sort.Direction.DESC;
        String sortColumn = "id";

        // Determine sort direction and column
        if (sortMode == 1 || sortMode == 2) {
            sortOrder = Sort.Direction.ASC;
        }
        if (sortMode == 2 || sortMode == 3) {
            sortColumn = "title";
        }

        // Create pageable object
        Pageable pageable = PageRequest.of(page - 1, 4, Sort.by(sortOrder, sortColumn));

        // Get movies based on category
        Page<Movie> moviePage;
        if (category.equals("all")) {
            moviePage = movieRepository.findAll(pageable);
        } else {
            moviePage = (Page<Movie>) movieRepository.findByStatus(category, pageable);
        }

        // Banner images for carousel
        List<String> bannerImages = Arrays.asList(
                "/images/NhaGiaTien.png",
                "/images/DarkNun.png",
                "/images/Den.webp"
        );

        // Add attributes to model
        model.addAttribute("bannerImages", bannerImages);
        model.addAttribute("movies", moviePage.getContent());
        model.addAttribute("category", category);
        model.addAttribute("sortMode", sortMode);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", moviePage.getTotalPages());

        return "movieList";
    }

    @RequestMapping(value = "/movie/detail/{id}")
    public String getMovieById(@PathVariable(value = "id") Long id, Model model) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid movie Id: " + id));
        model.addAttribute("movie", movie);
        return "movieDetail";
    }

    @GetMapping("/now-showing")
    public String getNowShowingMovies(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "sort", required = false, defaultValue = "0") int sortMode,
            Model model) {

        // Default values
        Sort.Direction sortOrder = Sort.Direction.DESC;
        String sortColumn = "id";

        // Determine sort direction and column
        if (sortMode == 1 || sortMode == 2) {
            sortOrder = Sort.Direction.ASC;
        }
        if (sortMode == 2 || sortMode == 3) {
            sortColumn = "title";
        }

        Pageable pageable = PageRequest.of(page - 1, 4, Sort.by(sortOrder, sortColumn));
        Page<Movie> nowShowingPage = (Page<Movie>) movieRepository.findByStatus("Now Showing", pageable);

        // Banner images
        List<String> bannerImages = Arrays.asList(
                "/images/NhaGiaTien.png",
                "/images/DarkNun.png",
                "/images/Den.webp"
        );

        model.addAttribute("bannerImages", bannerImages);
        model.addAttribute("nowShowingMovies", nowShowingPage.getContent());
        model.addAttribute("sortMode", sortMode);
        model.addAttribute("nowShowingCurrentPage", page);
        model.addAttribute("nowShowingTotalPages", nowShowingPage.getTotalPages());

        return "nowShowing";
    }

    @GetMapping("/coming-soon")
    public String getComingSoonMovies(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "sort", required = false, defaultValue = "0") int sortMode,
            Model model) {

        // Default values
        Sort.Direction sortOrder = Sort.Direction.DESC;
        String sortColumn = "id";

        // Determine sort direction and column
        if (sortMode == 1 || sortMode == 2) {
            sortOrder = Sort.Direction.ASC;
        }
        if (sortMode == 2 || sortMode == 3) {
            sortColumn = "title";
        }

        Pageable pageable = PageRequest.of(page - 1, 4, Sort.by(sortOrder, sortColumn));
        Page<Movie> comingSoonPage = (Page<Movie>) movieRepository.findByStatus("Coming Soon", pageable);

        // Banner images
        List<String> bannerImages = Arrays.asList(
                "/images/NhaGiaTien.png",
                "/images/DarkNun.png",
                "/images/Den.webp"
        );

        model.addAttribute("bannerImages", bannerImages);
        model.addAttribute("comingSoonMovies", comingSoonPage.getContent());
        model.addAttribute("sortMode", sortMode);
        model.addAttribute("comingSoonCurrentPage", page);
        model.addAttribute("comingSoonTotalPages", comingSoonPage.getTotalPages());

        return "comingSoon";
    }
}