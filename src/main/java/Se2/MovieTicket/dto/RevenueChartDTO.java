package Se2.MovieTicket.dto;

import java.sql.Date;

public class RevenueChartDTO {
    private String filmName;
    private Date orderDate;
    private Double revenue;

    // Constructor with correct parameter types
    public RevenueChartDTO(String filmName, Date orderDate, Double revenue) {
        this.filmName = filmName;
        this.orderDate = orderDate;
        this.revenue = revenue;
    }

    // Getters and Setters
    public String getFilmName() {
        return filmName;
    }

    public void setFilmName(String filmName) {
        this.filmName = filmName;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public Double getRevenue() {
        return revenue;
    }

    public void setRevenue(Double revenue) {
        this.revenue = revenue;
    }
}
