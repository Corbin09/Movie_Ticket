package Se2.MovieTicket.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "evaluate")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserReview {
    @EmbeddedId
    private UserReviewId id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @ManyToOne
    @MapsId("filmId")
    @JoinColumn(name = "film_id", nullable = false)
    @JsonIgnore
    private Film film;

    @Column(name = "comments")
    private String comments;

    @Column(name = "star")
    private Integer star;

    @Column(name = "date_posted")
    @Temporal(TemporalType.TIMESTAMP)
    private Date datePosted;

    public UserReviewId getId() {
        return id;
    }

    public void setId(UserReviewId id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Film getFilm() {
        return film;
    }

    public void setFilm(Film film) {
        this.film = film;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Integer getStar() {
        return star;
    }

    public void setStar(Integer star) {
        this.star = star;
    }

    public Date getDatePosted() {
        return datePosted;
    }

    public void setDatePosted(Date datePosted) {
        this.datePosted = datePosted;
    }

    public void setUserId(Long userId) {
        if (this.user == null) {
            this.user = new User();
        }
        this.user.setUserId(userId);
    }

    public void setFilmId(Long filmId) {
        if (this.film == null) {
            this.film = new Film();
        }
        this.film.setFilmId(filmId);
    }
}