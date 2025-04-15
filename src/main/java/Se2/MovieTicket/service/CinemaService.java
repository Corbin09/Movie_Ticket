package Se2.MovieTicket.service;

import Se2.MovieTicket.dto.CinemaDTO;
import Se2.MovieTicket.dto.CinemaWithShowtimesDTO;
import Se2.MovieTicket.dto.ShowtimeDTO;
import Se2.MovieTicket.model.Cinema;
import Se2.MovieTicket.model.CinemaCluster;
import Se2.MovieTicket.model.Showtime;
import Se2.MovieTicket.repository.CinemaRepository;
import Se2.MovieTicket.repository.ShowtimeRepository;

import jakarta.validation.Valid;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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

    public void deleteCinema(Long id) {
        cinemaRepository.deleteById(id);
    }

    public List<CinemaWithShowtimesDTO> getCinemasWithShowtimesForFilm(Long filmId) {
        List<Cinema> allCinemas = cinemaRepository.findAll();
        List<CinemaWithShowtimesDTO> result = new ArrayList<>();

        for (Cinema cinema : allCinemas) {
            Set<Showtime> cinemaShowtimes = cinema.getShowtimes();
            if (cinemaShowtimes == null || cinemaShowtimes.isEmpty()) {
                continue;
            }

            List<Showtime> filteredShowtimes = cinemaShowtimes.stream()
                    .filter(showtime -> showtime.getFilm() != null &&
                            showtime.getFilm().getFilmId() != null &&
                            showtime.getFilm().getFilmId().equals(filmId))
                    .collect(Collectors.toList());

            if (!filteredShowtimes.isEmpty()) {
                CinemaWithShowtimesDTO dto = new CinemaWithShowtimesDTO();
                dto.setCinemaId(cinema.getCinemaId());
                dto.setCinemaName(cinema.getCinemaName());
                dto.setAddress(cinema.getAddress());

                List<ShowtimeDTO> showtimeDTOs = filteredShowtimes.stream()
                        .map(showtime -> {
                            ShowtimeDTO showtimeDTO = new ShowtimeDTO();
                            showtimeDTO.setShowtimeId(showtime.getShowtimeId());
                            showtimeDTO.setShowDate(showtime.getShowDate());
                            showtimeDTO.setShowTime(showtime.getShowTime());

                            if (showtime.getFilm() != null) {
                                showtimeDTO.setFilmId(showtime.getFilm().getFilmId());
                                showtimeDTO.setFilmName(showtime.getFilm().getFilmName());
                            }

                            if (showtime.getRoom() != null) {
                                showtimeDTO.setRoomId(showtime.getRoom().getRoomId());
                                showtimeDTO.setRoomName(showtime.getRoom().getRoomName());
                            }

                            if (showtime.getCinema() != null) {
                                showtimeDTO.setCinemaId(showtime.getCinema().getCinemaId());
                                showtimeDTO.setCinemaName(showtime.getCinema().getCinemaName());
                            }

                            Double price = 0.0;
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

    public List<CinemaWithShowtimesDTO> getCinemasWithShowtimesForFilmAndDate(Long filmId, LocalDate date) {
        List<Object[]> results = cinemaRepository.findCinemasWithShowtimesByFilmAndDate(filmId, date);
        Map<Long, CinemaWithShowtimesDTO> cinemaMap = new HashMap<>();

        for (Object[] result : results) {
            Cinema cinema = (Cinema) result[0];
            Showtime showtime = (Showtime) result[1];

            CinemaWithShowtimesDTO cinemaDTO = cinemaMap.computeIfAbsent(
                    cinema.getCinemaId(),
                    id -> {
                        CinemaWithShowtimesDTO dto = new CinemaWithShowtimesDTO();
                        dto.setCinemaId(cinema.getCinemaId());
                        dto.setCinemaName(cinema.getCinemaName());
                        dto.setAddress(cinema.getAddress());
                        dto.setShowtimes(new ArrayList<>());
                        return dto;
                    });

            ShowtimeDTO showtimeDTO = new ShowtimeDTO();
            showtimeDTO.setShowtimeId(showtime.getShowtimeId());
            showtimeDTO.setShowTime(showtime.getShowTime());

            cinemaDTO.getShowtimes().add(showtimeDTO);
        }

        return new ArrayList<>(cinemaMap.values());
    }

    public List<CinemaDTO> getCinemasByRegionId(Long regionId) {
        List<Cinema> cinemas = cinemaRepository.findByRegionId(regionId);
        return cinemas.stream()
                .map(this::convertToCinemaDTO)
                .collect(Collectors.toList());
    }

    private CinemaDTO convertToCinemaDTO(Cinema cinema) {
        CinemaDTO cinemaDTO = new CinemaDTO();
        cinemaDTO.setCinemaId(cinema.getCinemaId());
        cinemaDTO.setCinemaName(cinema.getCinemaName());
        cinemaDTO.setAddress(cinema.getAddress());

        if (cinema.getCinemaCluster() != null) {
            cinemaDTO.setClusterId(cinema.getCinemaCluster().getClusterId());
        }

        if (cinema.getShowtimes() != null && !cinema.getShowtimes().isEmpty()) {
            Set<ShowtimeDTO> showtimeDTOs = cinema.getShowtimes().stream()
                    .map(showtime -> {
                        ShowtimeDTO dto = new ShowtimeDTO();
                        dto.setShowtimeId(showtime.getShowtimeId());
                        dto.setShowDate(showtime.getShowDate());
                        dto.setShowTime(showtime.getShowTime());

                        if (showtime.getFilm() != null) {
                            dto.setFilmId(showtime.getFilm().getFilmId());
                        }

                        if (showtime.getRoom() != null) {
                            dto.setRoomId(showtime.getRoom().getRoomId());
                        }

                        dto.setCinemaId(cinema.getCinemaId());

                        return dto;
                    })
                    .collect(Collectors.toSet());

            cinemaDTO.setShowtimes(showtimeDTOs);
        }

        return cinemaDTO;
    }

    /**
     * Get only basic cinema information for dropdowns
     * This avoids loading unnecessary data
     */
    public List<Cinema> getCinemasBasicInfo() {
        return cinemaRepository.findAllBasicInfo();
    }

    public int deleteCinemasByIds(List<Long> cinemaIds) {
        if (cinemaIds == null || cinemaIds.isEmpty()) {
            return 0;
        }

        int deletedCount = 0;
        for (Long id : cinemaIds) {
            try {
                cinemaRepository.deleteById(id);
                deletedCount++;
            } catch (Exception e) {
                System.err.println("Failed to delete cinema with ID: " + id + ". Error: " + e.getMessage());
            }
        }

        return deletedCount;
    }

    public void saveCinema(@Valid Cinema cinema) {
        cinemaRepository.save(cinema);
    }

    public Optional<Cinema> getCinemaByIdWithDetails(Long id) {
        if (id == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(em.createQuery(
                        "SELECT c FROM Cinema c " +
                                "LEFT JOIN FETCH c.cinemaCluster " +
                                "LEFT JOIN FETCH c.region " +
                                "LEFT JOIN FETCH c.rooms " +
                                "WHERE c.cinemaId = :id",
                        Cinema.class)
                .setParameter("id", id)
                .getResultStream()
                .findFirst()
                .orElse(null));
    }

    public void updateCinema(@Valid Cinema cinema) {
        if (cinema == null || cinema.getCinemaId() == null) {
            throw new IllegalArgumentException("Cinema or cinema ID cannot be null");
        }

        // Check if cinema exists before updating
        if (!cinemaRepository.existsById(cinema.getCinemaId())) {
            throw new NoSuchElementException("Cinema with ID " + cinema.getCinemaId() + " not found");
        }

        cinemaRepository.save(cinema);
    }

    private List<CinemaDTO> mapCinemasWithComplexInfo(List<Cinema> cinemas) {
        return cinemas.stream().map(cinema -> {
            CinemaDTO dto = new CinemaDTO();
            dto.setCinemaId(cinema.getCinemaId());
            dto.setCinemaName(cinema.getCinemaName());
            dto.setAddress(cinema.getAddress());

            // Set complex-related properties
            if (cinema.getCinemaCluster() != null) {
                dto.setClusterId(cinema.getCinemaCluster().getClusterId());
                dto.setComplexName(cinema.getCinemaCluster().getClusterName());
                dto.setComplexColor(getColorClassForCluster(cinema.getCinemaCluster()));
            }

            return dto;
        }).collect(Collectors.toList());
    }

    private String getColorClassForCluster(CinemaCluster cluster) {
        if (cluster == null) {
            return "bg-secondary";
        }

        String[] colorClasses = { "bg-primary", "bg-success", "bg-warning", "bg-danger", "bg-info" };
        return colorClasses[(int) (cluster.getClusterId() % colorClasses.length)];
    }

    public Page<Cinema> searchCinemasByNameOrAddressPaginated(String search, Pageable pageable) {
        if (search == null || search.trim().isEmpty()) {
            return cinemaRepository.findAll(pageable);
        }
        return cinemaRepository.findByCinemaNameContainingIgnoreCaseOrAddressContainingIgnoreCase(search, search,
                pageable);
    }

    public Page<Cinema> getCinemasByComplexNamePaginated(String complex, Pageable pageable) {
        if (complex == null || complex.trim().isEmpty()) {
            return Page.empty();
        }
        return cinemaRepository.findByCinemaCluster_ClusterNameContainingIgnoreCase(complex, pageable);
    }

    public Page<Cinema> getAllCinemasPaginated(Pageable pageable) {
        return cinemaRepository.findAll(pageable);
    }

}