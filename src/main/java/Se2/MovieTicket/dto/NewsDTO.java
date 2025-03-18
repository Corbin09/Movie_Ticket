package Se2.MovieTicket.dto;

import lombok.Data;

import java.util.Date;

@Data
public class NewsDTO {
    private Long newsId;
    private Long filmId;
    private Long userId;
    private String newsContent;
    private String newsImg;
    private Date newsTime;
    private String newsHeader;
    private String newsFooter;

    public Long getNewsId() {
        return newsId;
    }

    public void setNewsId(Long newsId) {
        this.newsId = newsId;
    }

    public Long getFilmId() {
        return filmId;
    }

    public void setFilmId(Long filmId) {
        this.filmId = filmId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getNewsContent() {
        return newsContent;
    }

    public void setNewsContent(String newsContent) {
        this.newsContent = newsContent;
    }

    public String getNewsImg() {
        return newsImg;
    }

    public void setNewsImg(String newsImg) {
        this.newsImg = newsImg;
    }

    public Date getNewsTime() {
        return newsTime;
    }

    public void setNewsTime(Date newsTime) {
        this.newsTime = newsTime;
    }

    public String getNewsHeader() {
        return newsHeader;
    }

    public void setNewsHeader(String newsHeader) {
        this.newsHeader = newsHeader;
    }

    public String getNewsFooter() {
        return newsFooter;
    }

    public void setNewsFooter(String newsFooter) {
        this.newsFooter = newsFooter;
    }
}