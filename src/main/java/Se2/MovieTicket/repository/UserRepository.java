package Se2.MovieTicket.repository;

import Se2.MovieTicket.model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
//    @Query("SELECT u FROM User u WHERE u.username = :username")
//    Optional<User> findByUsername(@Param("username") String username);
//
//    @Query("SELECT u FROM User u WHERE u.email = :email")
//    Optional<User> findByEmail(@Param("email") String email);

    // In UserRepository
    @Query("SELECT u FROM User u WHERE u.userId = :userId")
    User findUserByIdWithoutRelations(@Param("userId") Long userId);
    // Sử dụng query tối ưu hơn để chỉ lấy dữ liệu cần thiết
    @Query("SELECT u FROM User u WHERE u.username = :username")
    Optional<User> findByUsername(@Param("username") String username);

    @Query("SELECT u FROM User u WHERE u.email = :email")
    Optional<User> findByEmail(@Param("email") String email);

    // Thêm một phương thức mới để chỉ cập nhật hình ảnh
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.userImg = :imageUrl WHERE u.userId = :userId")
    void updateUserImage(@Param("userId") Long userId, @Param("imageUrl") String imageUrl);
}