package Se2.MovieTicket.service;

import Se2.MovieTicket.dto.CinemaDTO;
import Se2.MovieTicket.dto.CinemaWithShowtimesDTO;
import Se2.MovieTicket.dto.ShowtimeDTO;
import Se2.MovieTicket.model.Cinema;
import Se2.MovieTicket.model.Showtime;
import Se2.MovieTicket.repository.CinemaRepository;
import Se2.MovieTicket.repository.ShowtimeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CinemaService {
    @Autowired
    private CinemaRepository cinemaRepository;
    @Autowired
    private ShowtimeRepository showtimeRepository;
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
// In CinemaService.java class

//    public List<Cinema> getCinemasWithShowtimesForFilm(Long filmId) {
//        List<Cinema> allCinemas = cinemaRepository.findAll();
//        List<Cinema> cinemasWithShowtimes = new ArrayList<>();
//
//        for (Cinema cinema : allCinemas) {
//            // Get all showtimes for this cinema
//            Set<Showtime> cinemaShowtimes = cinema.getShowtimes();
//            if (cinemaShowtimes == null || cinemaShowtimes.isEmpty()) {
//                continue;
//            }
//
//            // Filter showtimes for the specific film
//            List<Showtime> filteredShowtimes = cinemaShowtimes.stream()
//                    .filter(showtime -> showtime.getFilm() != null &&
//                            showtime.getFilm().getFilmId() != null &&
//                            showtime.getFilm().getFilmId().equals(filmId))
//                    .collect(Collectors.toList());
//
//            if (!filteredShowtimes.isEmpty()) {
//                // Create a new cinema object with only relevant showtimes
//                Cinema cinemaWithShowtimes = new Cinema();
//                cinemaWithShowtimes.setCinemaId(cinema.getCinemaId());
//                cinemaWithShowtimes.setCinemaName(cinema.getCinemaName());
//                cinemaWithShowtimes.setAddress(cinema.getAddress());
//                // Add any other necessary cinema fields
//
//                // Set only the filtered showtimes to avoid loading the entire collection
//                Set<Showtime> showtimeSet = new HashSet<>(filteredShowtimes);
//                cinemaWithShowtimes.setShowtimes(showtimeSet);
//
//                cinemasWithShowtimes.add(cinemaWithShowtimes);
//            }
//        }
//
//        return cinemasWithShowtimes;
//    }
    public void deleteCinema(Long id) {
        cinemaRepository.deleteById(id);
    }

    public List<CinemaWithShowtimesDTO> getCinemasWithShowtimesForFilm(Long filmId) {
        List<Cinema> allCinemas = cinemaRepository.findAll();
        List<CinemaWithShowtimesDTO> result = new ArrayList<>();

        for (Cinema cinema : allCinemas) {
            // Get all showtimes for this cinema
            Set<Showtime> cinemaShowtimes = cinema.getShowtimes();
            if (cinemaShowtimes == null || cinemaShowtimes.isEmpty()) {
                continue;
            }

            // Filter showtimes for the specific film
            List<Showtime> filteredShowtimes = cinemaShowtimes.stream()
                    .filter(showtime -> showtime.getFilm() != null &&
                            showtime.getFilm().getFilmId() != null &&
                            showtime.getFilm().getFilmId().equals(filmId))
                    .collect(Collectors.toList());

            if (!filteredShowtimes.isEmpty()) {
                // Create a DTO instead of entity
                CinemaWithShowtimesDTO dto = new CinemaWithShowtimesDTO();
                dto.setCinemaId(cinema.getCinemaId());
                dto.setCinemaName(cinema.getCinemaName());
                dto.setAddress(cinema.getAddress());

                // Convert Showtime entities to ShowtimeDTO objects
                List<ShowtimeDTO> showtimeDTOs = filteredShowtimes.stream()
                        .map(showtime -> {
                            ShowtimeDTO showtimeDTO = new ShowtimeDTO();
                            showtimeDTO.setShowtimeId(showtime.getShowtimeId());
                            showtimeDTO.setShowDate(showtime.getShowDate());
                            showtimeDTO.setShowTime(showtime.getShowTime());  // Note: Changed from startTime to showTime

                            // Only include essential information from related entities
                            if (showtime.getFilm() != null) {
                                showtimeDTO.setFilmId(showtime.getFilm().getFilmId());
                                showtimeDTO.setFilmName(showtime.getFilm().getFilmName());
                            }

                            if (showtime.getRoom() != null) {
                                showtimeDTO.setRoomId(showtime.getRoom().getRoomId());
                                showtimeDTO.setRoomName(showtime.getRoom().getRoomName());
                            }

                            // Include cinema ID and name as well
                            if (showtime.getCinema() != null) {
                                showtimeDTO.setCinemaId(showtime.getCinema().getCinemaId());
                                showtimeDTO.setCinemaName(showtime.getCinema().getCinemaName());
                            }

                            // Get price if available
                            // Note: You may need to adjust this if price is stored elsewhere
                            Double price = 0.0; // Default value
                            // Add logic to get the actual price if it exists somewhere
                            showtimeDTO.setPrice(price);

                            return showtimeDTO;
                        })
                        .collect(Collectors.toList());

                dto.setShowtimes(showtimeDTOs);
                result.add(dto);
            }
        }

        return result;
    }
    // Trong CinemaService
    public List<CinemaWithShowtimesDTO> getCinemasWithShowtimesForFilmAndDate(Long filmId, LocalDate date) {
        // Lấy tất cả rạp có suất chiếu cho phim và ngày cụ thể
        List<Object[]> results = cinemaRepository.findCinemasWithShowtimesByFilmAndDate(filmId, date);
        Map<Long, CinemaWithShowtimesDTO> cinemaMap = new HashMap<>();

        for (Object[] result : results) {
            Cinema cinema = (Cinema) result[0];
            Showtime showtime = (Showtime) result[1];

            // Lấy hoặc tạo mới DTO cho rạp
            CinemaWithShowtimesDTO cinemaDTO = cinemaMap.computeIfAbsent(
                    cinema.getCinemaId(),
                    id -> {
                        CinemaWithShowtimesDTO dto = new CinemaWithShowtimesDTO();
                        dto.setCinemaId(cinema.getCinemaId());
                        dto.setCinemaName(cinema.getCinemaName());
                        dto.setAddress(cinema.getAddress());
                        dto.setShowtimes(new ArrayList<>());
                        return dto;
                    }
            );

            // Thêm showtime vào danh sách
            ShowtimeDTO showtimeDTO = new ShowtimeDTO();
            showtimeDTO.setShowtimeId(showtime.getShowtimeId());
            showtimeDTO.setShowTime(showtime.getShowTime());
            // Có thể thêm các thông tin khác của showtime nếu cần

            cinemaDTO.getShowtimes().add(showtimeDTO);
        }

        // Chuyển map thành list và trả về
        return new ArrayList<>(cinemaMap.values());
    }
}