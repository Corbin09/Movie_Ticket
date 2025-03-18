package Se2.MovieTicket.service;

import Se2.MovieTicket.dto.CinemaDTO;
import Se2.MovieTicket.model.Cinema;
import Se2.MovieTicket.repository.CinemaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CinemaService {
    @Autowired
    private CinemaRepository cinemaRepository;

    public List<Cinema> getAllCinemas() {
        return cinemaRepository.findAll();
    }

    public Optional<Cinema> getCinemaById(Long id) {
        return cinemaRepository.findById(id);
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