package Se2.MovieTicket.service;

import Se2.MovieTicket.dto.ShowtimeDTO;
import Se2.MovieTicket.model.Showtime;
import Se2.MovieTicket.repository.ShowtimeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
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
    public List<Showtime> getFilteredShowtimes(Long regionId, Long cinemaId, Long showtimeId) {
        // Trả về danh sách các showtimes đã lọc thông qua repository
        return showtimeRepository.findShowtimes(regionId, cinemaId, showtimeId);
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

    public List<LocalDate> getAvailableDatesForCinema(Long cinemaId) {
        // Lấy danh sách các ngày có suất chiếu cho rạp phim đã chọn
        List<LocalDate> availableDates = showtimeRepository.findDistinctShowDatesByCinemaId(cinemaId);

        // Sắp xếp danh sách ngày theo thứ tự tăng dần
        availableDates.sort(Comparator.naturalOrder());

        return availableDates;
    }

    public List<ShowtimeDTO> getShowtimesByCinemaFilmAndDate(Long cinemaId, Long filmId, LocalDate selectedDate) {
        List<Showtime> showtimes = showtimeRepository.findByCinemaAndFilmAndDate(cinemaId, filmId, selectedDate);
        return showtimes.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ShowtimeDTO> getAllShowtimesByCinemaAndFilm(Long cinemaId, Long filmId) {
        // Gọi repository để lấy danh sách showtime theo cinema và film
        List<Showtime> showtimes = showtimeRepository.findByCinemaIdAndFilmId(cinemaId, filmId);

        // Chuyển đổi danh sách showtime sang danh sách ShowtimeDTO
        return showtimes.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ShowtimeDTO> getAllShowtimesByFilm(Long filmId) {
        List<Showtime> showtimes = showtimeRepository.findByFilmId(filmId);
        return showtimes.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ShowtimeDTO> getAllShowtimesByRegionAndFilm(Long regionId, Long filmId) {
        List<Showtime> showtimes = showtimeRepository.findByRegionIdAndFilmId(regionId, filmId);
        return showtimes.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // In ShowtimeService:
//    public Optional<Showtime> getShowtimeWithDetails(Long showtimeId) {
//        return showtimeRepository.findShowtimeWithDetails(showtimeId);
//    }

    // Example for ShowtimeService
    @Transactional(readOnly = true)
    public Optional<Showtime> getShowtimeWithDetails(Long showtimeId) {
        // Use a single optimized query with all necessary joins
        return showtimeRepository.findByIdWithDetails(showtimeId);
    }




    public Page<Showtime> getAllShowtimesPaginated(Pageable pageable) {
        return showtimeRepository.findAll(pageable);
    }


//    public Optional<Showtime> getShowtimeById(Long id) {
//        return showtimeRepository.findById(id);
//    }


    public Optional<Showtime> getShowtimeByIdWithDetails(Long id) {
        return showtimeRepository.findByIdWithDetails(id);
    }


    public Page<Showtime> searchShowtimesPaginated(String searchTerm, Pageable pageable) {
        return showtimeRepository.findAll((Specification<Showtime>) (root, query, criteriaBuilder) -> {
            String likePattern = "%" + searchTerm.toLowerCase() + "%";

            return criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("film").get("filmName")), likePattern),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("cinema").get("cinemaName")), likePattern)
            );
        }, pageable);
    }


    public Page<Showtime> getShowtimesByFilmPaginated(Long filmId, Pageable pageable) {
        return showtimeRepository.findByFilmFilmId(filmId, pageable);
    }


    public Page<Showtime> getShowtimesByCinemaPaginated(Long cinemaId, Pageable pageable) {
        return showtimeRepository.findByCinemaCinemaId(cinemaId, pageable);
    }


    public Page<Showtime> getShowtimesByDatePaginated(Date date, Pageable pageable) {
        return showtimeRepository.findByShowDate(date, pageable);
    }


    @Transactional
    public Showtime saveShowtime(Showtime showtime) {
        return showtimeRepository.save(showtime);
    }


    @Transactional
    public Showtime updateShowtime(Showtime showtime) {
        return showtimeRepository.save(showtime);
    }


    // In ShowtimeService.java
    @Transactional
    public int deleteShowtimesByIds(List<Long> showtimeIds) {
        return showtimeRepository.deleteByShowtimeIdIn(showtimeIds);
    }

    public Page<Showtime> searchShowtimesByFilmNamePaginated(String search, Pageable pageable) {
        return showtimeRepository.findAll((Specification<Showtime>) (root, query, criteriaBuilder) -> {
            String likePattern = "%" + search.toLowerCase() + "%";
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("film").get("filmName")), likePattern);
        }, pageable);
    }

    public Page<Showtime> searchShowtimesByCinemaNamePaginated(String search, Pageable pageable) {
        return showtimeRepository.findAll((Specification<Showtime>) (root, query, criteriaBuilder) -> {
            String likePattern = "%" + search.toLowerCase() + "%";
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("cinema").get("cinemaName")), likePattern);
        }, pageable);
    }

    public Page<Showtime> searchShowtimesByRoomNamePaginated(String search, Pageable pageable) {
        return showtimeRepository.findAll((Specification<Showtime>) (root, query, criteriaBuilder) -> {
            String likePattern = "%" + search.toLowerCase() + "%";
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("room").get("roomName")), likePattern);
        }, pageable);
    }


//    @Transactional
//    public boolean deleteShowtime(Long id) {
//        try {
//            showtimeRepository.deleteById(id);
//            return true;
//        } catch (Exception e) {
//            return false;
//        }
//    }

}