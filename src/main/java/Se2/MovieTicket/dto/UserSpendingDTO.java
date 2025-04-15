package Se2.MovieTicket.dto;

public class UserSpendingDTO {
    private String username;
    private Double totalSpent;

    public UserSpendingDTO(String username, Double totalSpent) {
        this.username = username;
        this.totalSpent = totalSpent;
    }

    // Getters and setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Double getTotalSpent() {
        return totalSpent;
    }

    public void setTotalSpent(Double totalSpent) {
        this.totalSpent = totalSpent;
    }
}

