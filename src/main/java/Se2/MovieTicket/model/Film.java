package Se2.MovieTicket.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "films")
@Data
@NoArgsConstructor
@AllArgsConstructor
//@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Film {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "film_id")
    private Long filmId;

    @Column(name = "film_name", nullable = false)
    private String filmName;

    @Column(name = "film_img")
    private String filmImg;

    @Column(name = "film_trailer")
    private String filmTrailer;

    @Column(name = "release_date")
    @Temporal(TemporalType.DATE)
    private Date releaseDate;

    @Column(name = "film_describe")
    private String filmDescription;

    @Column(name = "age_limit")
    private Integer ageLimit;

    @Column(name = "duration")
    private Integer duration;

    @Column(name = "film_type")
    private String filmType;

    @Column(name = "country")
    private String country;

    @OneToMany(mappedBy = "film", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<FilmDirector> filmDirectors;

    @OneToMany(mappedBy = "film", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<FilmActor> filmActors;

    @OneToMany(mappedBy = "film", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<UserReview> userReviews;

    @OneToOne(mappedBy = "film", fetch = FetchType.LAZY)
    @JsonIgnore
    private FilmRating filmRating;

    @OneToMany(mappedBy = "film", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<FilmCategory> filmCategories;

    @OneToMany(mappedBy = "film", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Showtime> showtimes;

    @OneToMany(mappedBy = "film", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<News> news;

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

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
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

    public Set<FilmDirector> getFilmDirectors() {
        return filmDirectors;
    }

    public void setFilmDirectors(Set<FilmDirector> filmDirectors) {
        this.filmDirectors = filmDirectors;
    }

    public Set<FilmActor> getFilmActors() {
        return filmActors;
    }

    public void setFilmActors(Set<FilmActor> filmActors) {
        this.filmActors = filmActors;
    }

    public Set<UserReview> getUserReviews() {
        return userReviews;
    }

    public void setUserReviews(Set<UserReview> userReviews) {
        this.userReviews = userReviews;
    }

    public FilmRating getFilmRating() {
        return filmRating;
    }

    public void setFilmRating(FilmRating filmRating) {
        this.filmRating = filmRating;
    }

    public Set<FilmCategory> getFilmCategories() {
        return filmCategories;
    }

    public void setFilmCategories(Set<FilmCategory> filmCategories) {
        this.filmCategories = filmCategories;
    }

    public Set<Showtime> getShowtimes() {
        return showtimes;
    }

    public void setShowtimes(Set<Showtime> showtimes) {
        this.showtimes = showtimes;
    }

    public Set<News> getNews() {
        return news;
    }

    public void setNews(Set<News> news) {
        this.news = news;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;

        // Include all fields except `filmRating`
        result = prime * result + ((filmId == null) ? 0 : filmId.hashCode());
        result = prime * result + ((filmName == null) ? 0 : filmName.hashCode());
        result = prime * result + ((filmImg == null) ? 0 : filmImg.hashCode());
        result = prime * result + ((filmTrailer == null) ? 0 : filmTrailer.hashCode());
        result = prime * result + ((releaseDate == null) ? 0 : releaseDate.hashCode());
        result = prime * result + ((filmDescription == null) ? 0 : filmDescription.hashCode());
        result = prime * result + ((ageLimit == null) ? 0 : ageLimit.hashCode());
        result = prime * result + ((duration == null) ? 0 : duration.hashCode());
        result = prime * result + ((filmType == null) ? 0 : filmType.hashCode());
        result = prime * result + ((country == null) ? 0 : country.hashCode());

        // Include collections if needed, ensure null is handled
        result = prime * result + ((filmDirectors == null) ? 0 : filmDirectors.hashCode());
        result = prime * result + ((filmActors == null) ? 0 : filmActors.hashCode());
        result = prime * result + ((userReviews == null) ? 0 : userReviews.hashCode());
        result = prime * result + ((filmCategories == null) ? 0 : filmCategories.hashCode());
        result = prime * result + ((showtimes == null) ? 0 : showtimes.hashCode());
        result = prime * result + ((news == null) ? 0 : news.hashCode());

        return result;
    }
}