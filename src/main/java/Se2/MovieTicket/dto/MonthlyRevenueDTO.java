package Se2.MovieTicket.dto;

public class MonthlyRevenueDTO {
    private String month;
    private Double revenue;

    public MonthlyRevenueDTO(String month, Double revenue) {
        this.month = month;
        this.revenue = revenue;
    }

    public String getMonth() {
        return month;
    }

    public Double getRevenue() {
        return revenue;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public void setRevenue(Double revenue) {
        this.revenue = revenue;
    }
}
