package Se2.MovieTicket.service;

import Se2.MovieTicket.dto.DirectorDTO;
import Se2.MovieTicket.model.Director;
import Se2.MovieTicket.repository.DirectorRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DirectorService {
    @Autowired
    private DirectorRepository directorRepository;

    @Autowired
    private EntityManager em;

    public List<Director> filterDirectors(String name) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Director> cq = cb.createQuery(Director.class);
        Root<Director> director = cq.from(Director.class);

        List<Predicate> predicates = new ArrayList<>();
        if (name != null && !name.isEmpty()) {
            predicates.add(cb.like(director.get("directorName"), "%" + name + "%"));
        }

        cq.where(predicates.toArray(new Predicate[0]));
        return em.createQuery(cq).getResultList();
    }

    public List<Director> getAllDirectors() {
        return directorRepository.findAll();
    }

    public Optional<Director> getDirectorById(Long id) {
        return directorRepository.findById(id);
    }

    public List<Director> searchDirectors(String name) {
        return directorRepository.findByDirectorNameContaining(name);
    }

    public Director createDirector(DirectorDTO directorDTO) {
        Director director = new Director();
        director.setDirectorName(directorDTO.getDirectorName());
        director.setDirectorImg(directorDTO.getDirectorImg());
        director.setDirectorDescription(directorDTO.getDirectorDescription());
        return directorRepository.save(director);
    }

    public Director updateDirector(Long id, DirectorDTO directorDTO) {
        Optional<Director> directorData = directorRepository.findById(id);
        if (directorData.isPresent()) {
            Director director = directorData.get();
            director.setDirectorName(directorDTO.getDirectorName());
            director.setDirectorImg(directorDTO.getDirectorImg());
            director.setDirectorDescription(directorDTO.getDirectorDescription());
            return directorRepository.save(director);
        }
        return null;
    }

    public void deleteDirector(Long id) {
        directorRepository.deleteById(id);
    }
}