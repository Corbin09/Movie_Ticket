package Se2.MovieTicket.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "director_film")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FilmDirector {
    @EmbeddedId
    private FilmDirectorId id;

    @ManyToOne
    @MapsId("filmId")
    @JoinColumn(name = "film_id", nullable = false)
    @JsonIgnore
    private Film film;

    @ManyToOne
    @MapsId("directorId")
    @JoinColumn(name = "director_id", nullable = false)
    @JsonIgnore
    private Director director;

    public FilmDirectorId getId() {
        return id;
    }

    public void setId(FilmDirectorId id) {
        this.id = id;
    }

    public Film getFilm() {
        return film;
    }

    public void setFilm(Film film) {
        this.film = film;
    }

    public Director getDirector() {
        return director;
    }

    public void setDirector(Director director) {
        this.director = director;
    }
}