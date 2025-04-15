package Se2.MovieTicket.service;

import Se2.MovieTicket.model.Film;
import Se2.MovieTicket.model.News;
import Se2.MovieTicket.model.User;
import Se2.MovieTicket.repository.FilmRepository;
import Se2.MovieTicket.repository.NewsRepository;
import Se2.MovieTicket.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NewsService {
    @Autowired
    private NewsRepository newsRepository;
    @Autowired
    private FilmRepository filmRepository;

    @Autowired
    private UserRepository userRepository;

    public List<News> getAllNews() {
        return newsRepository.findAll();
    }

    public News getNewsById(Long id) {
        return newsRepository.findNewsById(id);
    }

    public News getLatestNews() {
        return newsRepository.findLatestNews();
    }

    public List<News> getNewsByFilm(Long filmId) {
        return newsRepository.findByFilm_FilmId(filmId);
    }

    public Page<News> getNewsPage(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        return newsRepository.findAll(pageable);
    }

    // Delete news by ID
    public void deleteNews(Long id) {
        // Check if the news exists before deleting
        if (newsRepository.existsById(id)) {
            newsRepository.deleteById(id);
        } else {
            throw new RuntimeException("News not found with id: " + id);
        }
    }

    public List<News> getNewsReviews() {
        List<News> allNews = newsRepository.findAllByOrderByNewsTimeDesc();

        if (!allNews.isEmpty()) {
            allNews.remove(0);
        }

        return allNews;
    }

    public Page<Film> getVietnameseMovies(int vietnamesePage, int size) {
        Pageable pageable = PageRequest.of(vietnamesePage, size);

        return filmRepository.findByFilmOrigin("Vietnamese", pageable);
    }

    public Page<Film> getInternationalMovies(int internationalPage, int size) {
        Pageable pageable = PageRequest.of(internationalPage, size);

        return filmRepository.findByFilmOriginNot("Vietnamese", pageable);
    }

    /**
     * Find all news articles created by a specific user
     *
     * @param userId the ID of the user
     * @return list of news articles associated with the user
     */
    public List<News> findNewsByUser(Long userId) {
        return newsRepository.findByUserUserId(userId);
    }

    /**
     * Find all news articles created by a specific user
     *
     * @param user the user entity
     * @return list of news articles associated with the user
     */
    public List<News> findNewsByUser(User user) {
        return newsRepository.findByUser(user);
    }

    @Transactional
    public News saveNews(News news) {
        // Make sure the time is set
        if (news.getNewsTime() == null) {
            news.setNewsTime(LocalDateTime.now());
        }

        // Load the full Film entity
        if (news.getFilm() != null && news.getFilm().getFilmId() != null) {
            Film film = filmRepository.findById(news.getFilm().getFilmId())
                    .orElseThrow(
                            () -> new EntityNotFoundException("Film not found with ID: " + news.getFilm().getFilmId()));
            news.setFilm(film);
        }

        // Load the full User entity
        if (news.getUser() != null && news.getUser().getUserId() != null) {
            User user = userRepository.findById(news.getUser().getUserId())
                    .orElseThrow(
                            () -> new EntityNotFoundException("User not found with ID: " + news.getUser().getUserId()));
            news.setUser(user);
        }

        return newsRepository.save(news);
    }
}