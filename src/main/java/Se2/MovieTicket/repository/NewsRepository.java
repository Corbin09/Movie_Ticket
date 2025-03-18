package Se2.MovieTicket.repository;

import Se2.MovieTicket.model.News;
import Se2.MovieTicket.model.Film;
import Se2.MovieTicket.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {
    @Query("SELECT n FROM News n WHERE n.film = :film")
    List<News> findByFilm(@Param("film") Film film);

    @Query("SELECT n FROM News n WHERE n.user = :user")
    List<News> findByUser (@Param("user") User user);

    @Query("SELECT n FROM News n WHERE LOWER(n.newsHeader) LIKE LOWER(CONCAT('%', :header, '%'))")
    List<News> findByNewsHeaderContaining(@Param("header") String header);

    @Query("SELECT n FROM News n WHERE LOWER(n.newsContent) LIKE LOWER(CONCAT('%', :content, '%'))")
    List<News> findByNewsContentContaining(@Param("content") String content);
}