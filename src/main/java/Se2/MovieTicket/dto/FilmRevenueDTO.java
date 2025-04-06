package Se2.MovieTicket.dto;

public class FilmRevenueDTO {
    private String filmName;
    private Double totalRevenue;
    private Long ticketsSold;
    private Double avgRating; // Thêm trường này

    public FilmRevenueDTO(String filmName, Double totalRevenue, Long ticketsSold) {
        this.filmName = filmName;
        this.totalRevenue = totalRevenue;
        this.ticketsSold = ticketsSold;
        this.avgRating = 0.0; // Giá trị mặc định
    }

    // Constructor mới với avgRating
    public FilmRevenueDTO(String filmName, Double totalRevenue, Long ticketsSold, Double avgRating) {
        this.filmName = filmName;
        this.totalRevenue = totalRevenue;
        this.ticketsSold = ticketsSold;
        this.avgRating = avgRating;
    }

    // Thêm getter và setter cho avgRating
    public Double getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(Double avgRating) {
        this.avgRating = avgRating;
    }

    public String getFilmName() {
        return filmName;
    }

    public void setFilmName(String filmName) {
        this.filmName = filmName;
    }

    public Double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(Double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public Long getTicketsSold() {
        return ticketsSold;
    }

    public void setTicketsSold(Long ticketsSold) {
        this.ticketsSold = ticketsSold;
    }
// Getters and Setters
}
