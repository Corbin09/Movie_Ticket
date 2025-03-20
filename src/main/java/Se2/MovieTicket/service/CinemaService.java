package Se2.MovieTicket.service;

import Se2.MovieTicket.dto.CinemaDTO;
import Se2.MovieTicket.model.Cinema;
import Se2.MovieTicket.repository.CinemaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CinemaService {
    @Autowired
    private CinemaRepository cinemaRepository;

    @Autowired
    private EntityManager em;

    public List<Cinema> filterCinemas(String name, String address) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Cinema> cq = cb.createQuery(Cinema.class);
        Root<Cinema> cinema = cq.from(Cinema.class);

        List<Predicate> predicates = new ArrayList<>();
        if (name != null && !name.isEmpty()) {
            predicates.add(cb.like(cinema.get("cinemaName"), "%" + name + "%"));
        }
        if (address != null && !address.isEmpty()) {
            predicates.add(cb.like(cinema.get("address"), "%" + address + "%"));
        }

        cq.where(predicates.toArray(new Predicate[0]));
        return em.createQuery(cq).getResultList();
    }

    public List<Cinema> getAllCinemas() {
        return cinemaRepository.findAll();
    }

    public Optional<Cinema> getCinemaById(Long id) {
        return cinemaRepository.findById(id);
    }

    public List<Cinema> searchCinemas(String name, String address) {
        if (name != null && !name.isEmpty()) {
            return cinemaRepository.findByCinemaNameContaining(name);
        } else if (address != null && !address.isEmpty()) {
            return cinemaRepository.findByAddressContaining(address);
        }
        return cinemaRepository.findAll();
    }

    public Cinema createCinema(CinemaDTO cinemaDTO) {
        Cinema cinema = new Cinema();
        cinema.setCinemaName(cinemaDTO.getCinemaName());
        cinema.setAddress(cinemaDTO.getAddress());
        // Set other fields as necessary
        return cinemaRepository.save(cinema);
    }

    public Cinema updateCinema(Long id, CinemaDTO cinemaDTO) {
        Optional<Cinema> cinemaData = cinemaRepository.findById(id);
        if (cinemaData.isPresent()) {
            Cinema cinema = cinemaData.get();
            cinema.setCinemaName(cinemaDTO.getCinemaName());
            cinema.setAddress(cinemaDTO.getAddress());
            // Update other fields as necessary
            return cinemaRepository.save(cinema);
        }
        return null;
    }

    public void deleteCinema(Long id) {
        cinemaRepository.deleteById(id);
    }
}