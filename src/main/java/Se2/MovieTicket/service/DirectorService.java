package Se2.MovieTicket.service;

import Se2.MovieTicket.dto.DirectorDTO;
import Se2.MovieTicket.model.Director;
import Se2.MovieTicket.repository.DirectorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DirectorService {
    @Autowired
    private DirectorRepository directorRepository;

    public List<Director> getAllDirectors() {
        return directorRepository.findAll();
    }

    public Optional<Director> getDirectorById(Long id) {
        return directorRepository.findById(id);
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