package Se2.MovieTicket.repository;

import Se2.MovieTicket.model.Order;
import Se2.MovieTicket.model.User;
import Se2.MovieTicket.model.Showtime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("SELECT o FROM Order o WHERE o.user = :user")
    List<Order> findByUser (@Param("user") User user);

    @Query("SELECT o FROM Order o WHERE o.showtime = :showtime")
    List<Order> findByShowtime(@Param("showtime") Showtime showtime);

    @Query("SELECT o FROM Order o WHERE o.orderDate BETWEEN :start AND :end")
    List<Order> findByOrderDateBetween(@Param("start") Date start, @Param("end") Date end);
}