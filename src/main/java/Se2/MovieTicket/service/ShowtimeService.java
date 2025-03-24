package Se2.MovieTicket.service;

import Se2.MovieTicket.dto.ShowtimeDTO;
import Se2.MovieTicket.model.Showtime;
import Se2.MovieTicket.repository.ShowtimeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    public boolean hasShowtimesForFilmAndDate(Long filmId, LocalDate date) {
        // Truy vấn cơ sở dữ liệu để kiểm tra xem có suất chiếu nào
        // cho phim và ngày được chỉ định hay không
        Integer count = showtimeRepository.countByFilmIdAndShowDate(filmId, date);
        return count != null && count > 0;
    }

    public List<ShowtimeDTO> getShowtimesByFilmAndCinemaAndDate(Long filmId, Long cinemaId, LocalDate selectedDate) {
        List<Showtime> showtimes = showtimeRepository.findByFilmAndCinemaAndDate(filmId, cinemaId, selectedDate);
        return showtimes.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ShowtimeDTO> getShowtimesByFilmAndRegionAndDate(Long filmId, Long regionId, LocalDate selectedDate) {
        List<Showtime> showtimes = showtimeRepository.findByFilmAndRegionAndDate(filmId, regionId, selectedDate);
        return showtimes.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ShowtimeDTO> getShowtimesByFilmAndDate(Long filmId, LocalDate selectedDate) {
        List<Showtime> showtimes = showtimeRepository.findByFilmAndDate(filmId, selectedDate);
        return showtimes.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Helper method to convert Showtime to ShowtimeDTO
    private ShowtimeDTO convertToDTO(Showtime showtime) {
        ShowtimeDTO dto = new ShowtimeDTO();
        dto.setShowtimeId(showtime.getShowtimeId());
        dto.setFilmId(showtime.getFilm().getFilmId());
        dto.setRoomId(showtime.getRoom().getRoomId());
        dto.setCinemaId(showtime.getCinema().getCinemaId());
        dto.setShowDate(showtime.getShowDate());
        dto.setShowTime(showtime.getShowTime());
        return dto;
    }
}