package Se2.MovieTicket.dto;

public class DashboardSummaryDTO {
    private Double totalRevenue;
    private Long totalTickets;
    private Long totalFilms;
    private Long totalUsers;

    public DashboardSummaryDTO(Double totalRevenue, Long totalTickets, Long totalFilms, Long totalUsers) {
        this.totalRevenue = totalRevenue;
        this.totalTickets = totalTickets;
        this.totalFilms = totalFilms;
        this.totalUsers = totalUsers;
    }

    // Getters and Setters
    public Long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(Long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public Long getTotalFilms() {
        return totalFilms;
    }

    public void setTotalFilms(Long totalFilms) {
        this.totalFilms = totalFilms;
    }

    public Long getTotalTickets() {
        return totalTickets;
    }

    public void setTotalTickets(Long totalTickets) {
        this.totalTickets = totalTickets;
    }

    public Double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(Double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }
}
