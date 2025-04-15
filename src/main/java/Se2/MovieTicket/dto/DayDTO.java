package Se2.MovieTicket.dto;

public class DayDTO {
    private String dayOfMonth;
    private String shortName;
    private boolean isToday;
    private boolean isPast;
    private boolean hasShowtimes;
    private String fullDate;
    private boolean isWeekend;
    private boolean isSelected;
    private boolean isCurrentMonth;

    // Constructors
    // Constructor for displaying the week header (Mon, Tue, Wed, ...)
    public DayDTO(String shortName, boolean isWeekend) {
        this.shortName = shortName;
        this.isWeekend = isWeekend;
    }

    // Full constructor for calendar days
    public DayDTO(
            String dayOfMonth,
            String shortName,
            boolean isToday,
            boolean isPast,
            boolean hasShowtimes,
            String fullDate,
            boolean isWeekend,
            boolean isSelected,
            boolean isCurrentMonth) {
        this.dayOfMonth = dayOfMonth;
        this.shortName = shortName;
        this.isToday = isToday;
        this.isPast = isPast;
        this.hasShowtimes = hasShowtimes;
        this.fullDate = fullDate;
        this.isWeekend = isWeekend;
        this.isSelected = isSelected;
        this.isCurrentMonth = isCurrentMonth;
    }

    public DayDTO(
            String dayOfMonth,
            String shortName,
            boolean isToday,
            boolean isPast,
            boolean hasShowtimes,
            String fullDate,
            boolean isWeekend) {
        this(dayOfMonth, shortName, isToday, isPast, hasShowtimes, fullDate, isWeekend, false, true);
    }

    // Getters và setters
    public String getDayOfMonth() {
        return dayOfMonth;
    }

    public void setDayOfMonth(String dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public boolean isToday() {
        return isToday;
    }

    public void setToday(boolean today) {
        isToday = today;
    }

    public boolean isPast() {
        return isPast;
    }

    public void setPast(boolean past) {
        isPast = past;
    }

    public boolean isHasShowtimes() {
        return hasShowtimes;
    }

    public void setHasShowtimes(boolean hasShowtimes) {
        this.hasShowtimes = hasShowtimes;
    }

    public String getFullDate() {
        return fullDate;
    }

    public void setFullDate(String fullDate) {
        this.fullDate = fullDate;
    }

    public boolean isWeekend() {
        return isWeekend;
    }

    public void setWeekend(boolean weekend) {
        isWeekend = weekend;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isCurrentMonth() {
        return isCurrentMonth;
    }

    public void setCurrentMonth(boolean currentMonth) {
        isCurrentMonth = currentMonth;
    }
}