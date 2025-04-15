package Se2.MovieTicket.controllers;

import Se2.MovieTicket.dto.*;
import Se2.MovieTicket.model.*;
import Se2.MovieTicket.service.*;
import Se2.MovieTicket.impl.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class NewsController {

    @Autowired
    private NewsService newsService;

    @Autowired
    private UserService userService;

    @GetMapping("/news")
    public String getNewsPage(
            @RequestParam(defaultValue = "1") int vnPage,
            @RequestParam(defaultValue = "1") int intPage,
            Model model, HttpServletRequest request) {
        model.addAttribute("currPage", "news");
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

        News latestNews = newsService.getLatestNews();
        model.addAttribute("latestNews", latestNews);
        List<News> newsReviews = newsService.getNewsReviews();
        model.addAttribute("newsReviews", newsReviews);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        // Retrieve and convert Vietnamese movie list to FilmDTO
        Page<Film> vnFilmPage = newsService.getVietnameseMovies(vnPage - 1, 8);
        List<FilmDTO> vnMovieList = vnFilmPage.getContent().stream().map(film -> {
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

            filmDTO.setCategoryNames(film.getFilmCategories().stream()
                    .map(fc -> fc.getCategory().getCategoryName())
                    .collect(Collectors.toList()));
            return filmDTO;
        }).collect(Collectors.toList());

        // Retrieve and convert International movie list to FilmDTO
        Page<Film> intFilmPage = newsService.getInternationalMovies(intPage - 1, 8);
        List<FilmDTO> intMovieList = intFilmPage.getContent().stream().map(film -> {
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

            filmDTO.setCategoryNames(film.getFilmCategories().stream()
                    .map(fc -> fc.getCategory().getCategoryName())
                    .collect(Collectors.toList()));
            return filmDTO;
        }).collect(Collectors.toList());

        // Add movie list and pagination info to the model
        model.addAttribute("vnMovies", vnMovieList);
        model.addAttribute("vnPages", vnFilmPage.getTotalPages());
        model.addAttribute("currentVnPage", vnPage);

        model.addAttribute("intMovies", intMovieList);
        model.addAttribute("intPages", intFilmPage.getTotalPages());
        model.addAttribute("currentIntPage", intPage);

        return "news";
    }



    @GetMapping("/news/{id}")
    public String getNewsDetails(@PathVariable Long id, Model model, HttpServletRequest request) {
        model.addAttribute("currPage", "news");
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

        List<News> allNews = newsService.getAllNews();
        int totalNews = allNews.size();

        // Find the position of the news with id in the list
        int currentIndex = -1;
        for (int i = 0; i < totalNews; i++) {
            if (allNews.get(i).getNewsId().equals(id)) {
                currentIndex = i;
                break;
            }
        }

        // Identify next news based on location
        News currentNews = allNews.get(currentIndex);
        News nextNews = allNews.get((currentIndex + 1) % totalNews);
        News prevNews = allNews.get((currentIndex - 1 + totalNews) % totalNews);

        model.addAttribute("currentNews", currentNews);
        model.addAttribute("nextNews", nextNews);
        model.addAttribute("prevNews", prevNews);
        model.addAttribute("totalNews", totalNews);

        return "news-details";
}}
