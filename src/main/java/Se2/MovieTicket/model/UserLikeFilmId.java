package Se2.MovieTicket.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Data
@NoArgsConstructor  // Giữ để JPA có thể khởi tạo object
@AllArgsConstructor // Đã có, không cần định nghĩa lại constructor tay
@Embeddable
public class UserLikeFilmId implements Serializable {
    private Long userId;
    private Long filmId;

    // Không cần viết lại constructor nữa vì @AllArgsConstructor đã làm điều đó

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getFilmId() {
        return filmId;
    }

    public void setFilmId(Long filmId) {
        this.filmId = filmId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserLikeFilmId that = (UserLikeFilmId) o;
        return Objects.equals(userId, that.userId) && Objects.equals(filmId, that.filmId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, filmId);
    }
}
