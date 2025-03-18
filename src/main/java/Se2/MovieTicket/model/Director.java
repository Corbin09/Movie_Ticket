package Se2.MovieTicket.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Entity
@Table(name = "directors")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Director {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "director_id")
    private Long directorId;

    @Column(name = "director_name", nullable = false)
    private String directorName;

    @Column(name = "director_img")
    private String directorImg;

    @Column(name = "director_describe")
    private String directorDescription;

    @OneToMany(mappedBy = "director", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<FilmDirector> filmDirectors;

    public Long getDirectorId() {
        return directorId;
    }

    public void setDirectorId(Long directorId) {
        this.directorId = directorId;
    }

    public String getDirectorName() {
        return directorName;
    }

    public void setDirectorName(String directorName) {
        this.directorName = directorName;
    }

    public String getDirectorImg() {
        return directorImg;
    }

    public void setDirectorImg(String directorImg) {
        this.directorImg = directorImg;
    }

    public String getDirectorDescription() {
        return directorDescription;
    }

    public void setDirectorDescription(String directorDescription) {
        this.directorDescription = directorDescription;
    }

    public Set<FilmDirector> getFilmDirectors() {
        return filmDirectors;
    }

    public void setFilmDirectors(Set<FilmDirector> filmDirectors) {
        this.filmDirectors = filmDirectors;
    }
}