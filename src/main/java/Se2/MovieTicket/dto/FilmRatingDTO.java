package Se2.MovieTicket.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FilmRatingDTO {
    private Long filmId;
    private Double filmRate;
    private Integer sumRate;
    private Integer sumStar;

    public Integer getSumStar() {
        return sumStar;
    }

    public void setSumStar(Integer sumStar) {
        this.sumStar = sumStar;
    }

    public Integer getSumRate() {
        return sumRate;
    }

    public void setSumRate(Integer sumRate) {
        this.sumRate = sumRate;
    }

    public Double getFilmRate() {
        return filmRate;
    }

    public void setFilmRate(Double filmRate) {
        this.filmRate = filmRate;
    }

    public Long getFilmId() {
        return filmId;
    }

    public void setFilmId(Long filmId) {
        this.filmId = filmId;
    }
}