package Se2.MovieTicket.dto;

public class UserGenderStatsDTO {
    private String sex;
    private Long total;

    public UserGenderStatsDTO(String sex, Long total) {
        this.sex = sex;
        this.total = total;
    }

    // Getters and setters
    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }
}
