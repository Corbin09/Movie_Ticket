package Se2.MovieTicket.dto;

import java.util.List;

public class WeekDTO {
    private List<DayDTO> days;  // Danh sách các ngày trong tuần

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
