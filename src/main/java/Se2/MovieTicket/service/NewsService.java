package Se2.MovieTicket.service;

import Se2.MovieTicket.dto.NewsDTO;
import Se2.MovieTicket.model.News;
import Se2.MovieTicket.repository.NewsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NewsService {
    @Autowired
    private NewsRepository newsRepository;

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

    public void deleteNews(Long id) {
        newsRepository.deleteById(id);
    }
}