package Se2.MovieTicket.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "film_evaluate")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FilmRating {
    @Id
    @Column(name = "film_id")
    private Long filmId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "film_id")
    @JsonIgnore
    private Film film;

    @Column(name = "film_rate", nullable = false)
    private Double filmRate;

    @Column(name = "sum_rate")
    private Integer sumRate;

    @Column(name = "sum_star")
    private Integer sumStar;

    public Long getFilmId() {
        return filmId;
    }

    public void setFilmId(Long filmId) {
        this.filmId = filmId;
    }

    public Film getFilm() {
        return film;
    }

    public void setFilm(Film film) {
        this.film = film;
    }

    public Double getFilmRate() {
        return filmRate;
    }

    public void setFilmRate(Double filmRate) {
        this.filmRate = filmRate;
    }

    public Integer getSumRate() {
        return sumRate;
    }

    public void setSumRate(Integer sumRate) {
        this.sumRate = sumRate;
    }

    public Integer getSumStar() {
        return sumStar;
    }

    public void setSumStar(Integer sumStar) {
        this.sumStar = sumStar;
    }
}