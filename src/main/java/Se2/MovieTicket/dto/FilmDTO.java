package Se2.MovieTicket.dto;

import Se2.MovieTicket.model.News;
import Se2.MovieTicket.model.UserReview;
import lombok.Data;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

@Data
public class FilmDTO {
    private Long filmId;
    private String filmName;
    private String filmImg;
    private String filmTrailer;
    private LocalDate releaseDate;
    private String filmDescription;
    private Integer ageLimit;
    private Integer duration;
    private String filmType;
    private String country;
    private List<String> directorNames;
    private List<String> actorNames;
    private List<String> categoryNames;
    private Double averageRating;
    private List<ShowtimeDTO> showtimes;
    private Set<News> news;
    private Set<UserReview> userReviews;
    private String formattedReleaseDate;
    private List<DirectorDTO> directors;
    private List<ActorDTO> actors;
    public Long getFilmId() {
        return filmId;
    }

    public void setFilmId(Long filmId) {
        this.filmId = filmId;
    }

    public String getFilmName() {
        return filmName;
    }

    public void setFilmName(String filmName) {
        this.filmName = filmName;
    }

    public String getFilmImg() {
        return filmImg;
    }

    public void setFilmImg(String filmImg) {
        this.filmImg = filmImg;
    }

    public String getFilmTrailer() {
        return filmTrailer;
    }

    public void setFilmTrailer(String filmTrailer) {
        this.filmTrailer = filmTrailer;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getFilmDescription() {
        return filmDescription;
    }

    public void setFilmDescription(String filmDescription) {
        this.filmDescription = filmDescription;
    }

    public Integer getAgeLimit() {
        return ageLimit;
    }

    public void setAgeLimit(Integer ageLimit) {
        this.ageLimit = ageLimit;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getFilmType() {
        return filmType;
    }

    public void setFilmType(String filmType) {
        this.filmType = filmType;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public List<String> getDirectorNames() {
        return directorNames;
    }

    public void setDirectorNames(List<String> directorNames) {
        this.directorNames = directorNames;
    }

    public List<String> getActorNames() {
        return actorNames;
    }

    public void setActorNames(List<String> actorNames) {
        this.actorNames = actorNames;
    }

    public List<String> getCategoryNames() {
        return categoryNames;
    }

    public void setCategoryNames(List<String> categoryNames) {
        this.categoryNames = categoryNames;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public List<ShowtimeDTO> getShowtimes() {
        return showtimes;
    }

    public void setShowtimes(List<ShowtimeDTO> showtimes) {
        this.showtimes = showtimes;
    }

    public Set<News> getNews() {
        return news;
    }

    public void setNews(Set<News> news) {
        this.news = news;
    }

    public Set<UserReview> getUserReviews() {
        return userReviews;
    }

    public void setUserReviews(Set<UserReview> userReviews) {
        this.userReviews = userReviews;
    }


    // Getter tùy chỉnh để format LocalDate sang dạng dd/MM/yyyy
    public String getFormattedReleaseDate() {
        if (releaseDate != null) {
            return releaseDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        }
        return null;  // Nếu releaseDate null thì trả về null
    }

    public void setFormattedReleaseDate(String formattedReleaseDate) {
        if (formattedReleaseDate != null && !formattedReleaseDate.isEmpty()) {
            // Convert String date to LocalDate using the expected format
            this.releaseDate = LocalDate.parse(formattedReleaseDate, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        }
        this.formattedReleaseDate = formattedReleaseDate; // Gán giá trị chuỗi cho property nếu cần hiển thị lại
    }

    public void setReleaseDateFormatted(String format) {
        if (releaseDate != null) {
            // Gán giá trị ngày được format theo định dạng tùy chỉnh
            this.formattedReleaseDate = releaseDate.format(DateTimeFormatter.ofPattern(format));
        }
    }

    public List<DirectorDTO> getDirectors() {
        return directors;
    }

    public void setDirectors(List<DirectorDTO> directors) {
        this.directors = directors;
    }

    public List<ActorDTO> getActors() {
        return actors;
    }

    public void setActors(List<ActorDTO> actors) {
        this.actors = actors;
    }
}