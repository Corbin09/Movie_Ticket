package Se2.MovieTicket.service;

import Se2.MovieTicket.dto.CinemaClusterDTO;
import Se2.MovieTicket.model.CinemaCluster;
import Se2.MovieTicket.repository.CinemaClusterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CinemaClusterService {
    @Autowired
    private CinemaClusterRepository cinemaClusterRepository;

    public List<CinemaCluster> getAllCinemaClusters() {
        return cinemaClusterRepository.findAll();
    }

    public Optional<CinemaCluster> getCinemaClusterById(Long id) {
        return cinemaClusterRepository.findById(id);
    }

    public CinemaCluster createCinemaCluster(CinemaClusterDTO cinemaClusterDTO) {
        CinemaCluster cinemaCluster = new CinemaCluster();
        cinemaCluster.setClusterName(cinemaClusterDTO.getClusterName());
        return cinemaClusterRepository.save(cinemaCluster);
    }

    public CinemaCluster updateCinemaCluster(Long id, CinemaClusterDTO cinemaClusterDTO) {
        Optional<CinemaCluster> cinemaClusterData = cinemaClusterRepository.findById(id);
        if (cinemaClusterData.isPresent()) {
            CinemaCluster cinemaCluster = cinemaClusterData.get();
            cinemaCluster.setClusterName(cinemaClusterDTO.getClusterName());
            return cinemaClusterRepository.save(cinemaCluster);
        }
        return null;
    }

    public void deleteCinemaCluster(Long id) {
        cinemaClusterRepository.deleteById(id);
    }
}