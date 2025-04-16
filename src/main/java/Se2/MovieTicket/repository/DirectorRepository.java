package Se2.MovieTicket.repository;

import Se2.MovieTicket.model.Director;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DirectorRepository extends JpaRepository<Director, Long> {
    List<Director> findByDirectorNameContaining(String name);

    List<Director> findByDirectorName(String directorName);
}