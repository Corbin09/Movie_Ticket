package Se2.MovieTicket.repository;

import Se2.MovieTicket.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query("SELECT c FROM Category c WHERE c.categoryName LIKE %:name%")
    List<Category> findByCategoryNameContaining(@Param("name") String name);
}