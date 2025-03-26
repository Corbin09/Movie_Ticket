package Se2.MovieTicket.service;

import Se2.MovieTicket.dto.FilmDTO;
import Se2.MovieTicket.dto.NewsDTO;
import Se2.MovieTicket.model.Film;
import Se2.MovieTicket.model.News;
import Se2.MovieTicket.repository.FilmRepository;
import Se2.MovieTicket.repository.NewsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import java.util.List;
import java.util.Optional;

@Service
public class NewsService {
    @Autowired
    private NewsRepository newsRepository;
@Autowired
private FilmRepository filmRepository;
    public List<News> getAllNews() {
        return newsRepository.findAll();
    }

    public Optional<News> getNewsById(Long id) {
        return newsRepository.findById(id);
    }

    public News createNews(NewsDTO newsDTO) {
        News news = new News();
        news.setFilmId(newsDTO.getFilmId());
        news.setUserId(newsDTO.getUserId());
        news.setNewsContent(newsDTO.getNewsContent());
        news.setNewsImg(newsDTO.getNewsImg());
        news.setNewsTime(newsDTO.getNewsTime());
        news.setNewsHeader(newsDTO.getNewsHeader());
        news.setNewsFooter(newsDTO.getNewsFooter());
        return newsRepository.save(news);
    }

    public News updateNews(Long id, NewsDTO newsDTO) {
        Optional<News> newsData = newsRepository.findById(id);
        if (newsData.isPresent()) {
            News news = newsData.get();
            news.setFilmId(newsDTO.getFilmId());
            news.setUserId(newsDTO.getUserId());
            news.setNewsContent(newsDTO.getNewsContent());
            news.setNewsImg(newsDTO.getNewsImg());
            news.setNewsTime(newsDTO.getNewsTime());
            news.setNewsHeader(newsDTO.getNewsHeader());
            news.setNewsFooter(newsDTO.getNewsFooter());
            return newsRepository.save(news);
        }
        return null;
    }

    // Get latest news
    public News getLatestNews() {
        return newsRepository.findLatestNews();
    }

    // Get news for a specific film
    public List<News> getNewsByFilm(Long filmId) {
        return newsRepository.findByFilm_FilmId(filmId);
    }

    // Get paginated news
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
            // Optional: You could throw a custom exception if the news doesn't exist
            throw new RuntimeException("News not found with id: " + id);
        }
    }

    public List<News> getNewsReviews() {
        // Get all news sorted by date descending (latest first)
        List<News> allNews = newsRepository.findAllByOrderByNewsTimeDesc();

        // Remove the latest news (the one with the most recent date)
        if (!allNews.isEmpty()) {
            allNews.remove(0); // Remove the first element, which is the latest news
        }

        return allNews;
    }

    // 2. Get paginated Vietnamese movies
    public Page<Film> getVietnameseMovies(int vietnamesePage, int size) {
        Pageable pageable = PageRequest.of(vietnamesePage, size);
        // Assuming Vietnamese movies are identified by their film type
        return filmRepository.findByFilmOrigin("Vietnamese", pageable);
    }

    // 3. Get paginated International movies
    public Page<Film> getInternationalMovies(int internationalPage, int size) {
        Pageable pageable = PageRequest.of(internationalPage, size);
        // Assuming International movies are identified by their film type as not being Vietnamese
        return filmRepository.findByFilmOriginNot("Vietnamese", pageable);
    }


}