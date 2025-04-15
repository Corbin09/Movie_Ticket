package Se2.MovieTicket.dto;

import java.util.List;

public class WeekDTO {
    private List<DayDTO> days;

    public WeekDTO(List<DayDTO> days) {
        this.days = days;
    }

    public List<DayDTO> getDays() {
        return days;
    }

    public void setDays(List<DayDTO> days) {
        this.days = days;
    }
}
