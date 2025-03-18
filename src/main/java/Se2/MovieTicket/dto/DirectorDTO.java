package Se2.MovieTicket.dto;

import lombok.Data;

@Data
public class DirectorDTO {
    private Long directorId;
    private String directorName;
    private String directorImg;
    private String directorDescription;

    public Long getDirectorId() {
        return directorId;
    }

    public void setDirectorId(Long directorId) {
        this.directorId = directorId;
    }

    public String getDirectorName() {
        return directorName;
    }

    public void setDirectorName(String directorName) {
        this.directorName = directorName;
    }

    public String getDirectorImg() {
        return directorImg;
    }

    public void setDirectorImg(String directorImg) {
        this.directorImg = directorImg;
    }

    public String getDirectorDescription() {
        return directorDescription;
    }

    public void setDirectorDescription(String directorDescription) {
        this.directorDescription = directorDescription;
    }
}