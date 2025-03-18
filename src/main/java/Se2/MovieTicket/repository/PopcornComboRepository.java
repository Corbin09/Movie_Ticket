package Se2.MovieTicket.repository;

import Se2.MovieTicket.model.PopcornCombo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PopcornComboRepository extends JpaRepository<PopcornCombo, Long> {
    @Query("SELECT p FROM PopcornCombo p WHERE p.comboName LIKE %:name%")
    List<PopcornCombo> findByComboNameContaining(@Param("name") String name);

    @Query("SELECT p FROM PopcornCombo p WHERE p.comboPrice = :price")
    List<PopcornCombo> findByComboPrice(@Param("price") Double price);

    @Query("SELECT p FROM PopcornCombo p WHERE p.comboPrice <= :maxPrice")
    List<PopcornCombo> findByComboPriceLessThanEqual(@Param("maxPrice") Double maxPrice);
}