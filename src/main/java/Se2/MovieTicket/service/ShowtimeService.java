package Se2.MovieTicket.service;

import Se2.MovieTicket.dto.ShowtimeDTO;
import Se2.MovieTicket.model.Showtime;
import Se2.MovieTicket.repository.ShowtimeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ShowtimeService {
    @Autowired
    private ShowtimeRepository showtimeRepository;

    public List<Showtime> getAllShowtimes() {
        return showtimeRepository.findAll();
    }

    public Optional<Showtime> getShowtimeById(Long id) {
        return showtimeRepository.findById(id);
    }

    public Showtime createShowtime(ShowtimeDTO showtimeDTO) {
        Showtime showtime = new Showtime();
        showtime.setFilmId(showtimeDTO.getFilmId());
        showtime.setRoomId(showtimeDTO.getRoomId());
        showtime.setCinemaId(showtimeDTO.getCinemaId());
        showtime.setShowDate(showtimeDTO.getShowDate());
        showtime.setShowTime(showtimeDTO.getShowTime());
        return showtimeRepository.save(showtime);
    }

    public Showtime updateShowtime(Long id, ShowtimeDTO showtimeDTO) {
        Optional<Showtime> showtimeData = showtimeRepository.findById(id);
        if (showtimeData.isPresent()) {
            Showtime showtime = showtimeData.get();
            showtime.setFilmId(showtimeDTO.getFilmId());
            showtime.setRoomId(showtimeDTO.getRoomId());
            showtime.setCinemaId(showtimeDTO.getCinemaId());
            showtime.setShowDate(showtimeDTO.getShowDate());
            showtime.setShowTime(showtimeDTO.getShowTime());
            return showtimeRepository.save(showtime);
        }
        return null;
    }

    public void deleteShowtime(Long id) {
        showtimeRepository.deleteById(id);
    }
}