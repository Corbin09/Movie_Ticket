package Se2.MovieTicket.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

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
    @ToString.Exclude
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;

        // Include the composite key `FilmDirectorId` in the hash code calculation
        result = prime * result + ((id == null) ? 0 : id.hashCode());

        return result;
    }
    @Override
    public String toString() {
        return "FilmDirector{" +
                "id=" + id +
                ", filmId=" + (film != null ? film.getFilmId() : "null") +
                ", directorId=" + (director != null ? director.getDirectorId() : "null") +
                '}';
    }

}