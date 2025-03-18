package Se2.MovieTicket.repository;

import Se2.MovieTicket.model.PopcornOrder;
import Se2.MovieTicket.model.Order;
import Se2.MovieTicket.model.PopcornCombo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PopcornOrderRepository extends JpaRepository<PopcornOrder, Long> {
    @Query("SELECT p FROM PopcornOrder p WHERE p.order = :order")
    List<PopcornOrder> findByOrder(@Param("order") Order order);

    @Query("SELECT p FROM PopcornOrder p WHERE p.popcornCombo = :combo")
    List<PopcornOrder> findByPopcornCombo(@Param("combo") PopcornCombo combo);
}