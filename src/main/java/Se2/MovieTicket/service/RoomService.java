package Se2.MovieTicket.service;

import Se2.MovieTicket.dto.RoomDTO;
import Se2.MovieTicket.model.Cinema;
import Se2.MovieTicket.model.Room;
import Se2.MovieTicket.repository.RoomRepository;
import Se2.MovieTicket.repository.SeatRepository;
import Se2.MovieTicket.repository.SeatStatusRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RoomService {
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private SeatRepository seatRepository;
    @Autowired
    private SeatStatusRepository seatStatusRepository;
    @Autowired
    private EntityManager em;

    public List<Room> filterRooms(String name) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Room> cq = cb.createQuery(Room.class);
        Root<Room> room = cq.from(Room.class);

        List<Predicate> predicates = new ArrayList<>();
        if (name != null && !name.isEmpty()) {
            predicates.add(cb.like(room.get("roomName"), "%" + name + "%"));
        }

        cq.where(predicates.toArray(new Predicate[0]));
        return em.createQuery(cq).getResultList();
    }

    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    public Optional<Room> getRoomById(Long id) {
        return roomRepository.findById(id);
    }

    public Room createRoom(RoomDTO roomDTO) {
        Room room = new Room();
        room.setRoomName(roomDTO.getRoomName());
        room.setCinemaId(roomDTO.getCinemaId());
        return roomRepository.save(room);
    }

    public Room updateRoom(Long id, RoomDTO roomDTO) {
        Optional<Room> roomData = roomRepository.findById(id);
        if (roomData.isPresent()) {
            Room room = roomData.get();
            room.setRoomName(roomDTO.getRoomName());
            room.setCinemaId(roomDTO.getCinemaId());
            return roomRepository.save(room);
        }
        return null;
    }

    public void deleteRoom(Long id) {
        roomRepository.deleteById(id);
    }

    public List<Room> searchRooms(String search, String filterBy) {
        if (search == null || search.trim().isEmpty()) {
            return filterBy != null && !filterBy.trim().isEmpty()
                    ? filterRooms(filterBy)
                    : roomRepository.findAll();
        }

        // Get base list of rooms (either all or filtered)
        List<Room> baseRooms = filterBy != null && !filterBy.trim().isEmpty()
                ? filterRooms(filterBy)
                : roomRepository.findAll();

        // Try to search by roomId if search is a number
        try {
            Long roomId = Long.parseLong(search);
            Optional<Room> roomOpt = roomRepository.findById(roomId);
            if (roomOpt.isPresent() && baseRooms.contains(roomOpt.get())) {
                return List.of(roomOpt.get());
            }
        } catch (NumberFormatException e) {
            // Not a number, continue with other search methods
        }

        String searchLower = search.toLowerCase();

        // Search across all relevant fields
        return baseRooms.stream()
                .filter(room -> {
                    // Check room ID as string
                    if (room.getRoomId() != null && room.getRoomId().toString().contains(search)) {
                        return true;
                    }

                    // Check room name
                    if (room.getRoomName() != null && room.getRoomName().toLowerCase().contains(searchLower)) {
                        return true;
                    }

                    // Check cinema name
                    if (room.getCinema() != null && room.getCinema().getCinemaName() != null
                            && room.getCinema().getCinemaName().toLowerCase().contains(searchLower)) {
                        return true;
                    }

                    // Check if room has seats matching the search
                    if (room.getSeats() != null && room.getSeats().stream()
                            .anyMatch(seat -> seat.getSeatId() != null &&
                                    String.valueOf(seat.getSeatId()).toLowerCase().contains(searchLower))) {
                        return true;
                    }

                    // Check if room has showtimes matching the search
                    if (room.getShowtimes() != null && room.getShowtimes().stream()
                            .anyMatch(showtime ->
                                    // Check showtime's film name
                                    (showtime.getFilm() != null &&
                                            showtime.getFilm().getFilmName() != null &&
                                            showtime.getFilm().getFilmName().toLowerCase().contains(searchLower)) ||
                                            // Check showtime's date/time
                                            (showtime.getShowTime() != null &&
                                                    showtime.getShowTime().toString().toLowerCase().contains(searchLower))
                            )) {
                        return true;
                    }

                    return false;
                })
                .collect(Collectors.toList());
    }

    public List<Room> getRoomsByCinemaId(Long cinemaId) {
        if (cinemaId == null) {
            return new ArrayList<>();
        }
        return roomRepository.findByCinemaCinemaId(cinemaId);
    }
    public Long countSeatsByRoomId(Long roomId) {
        return roomRepository.countSeatsByRoomId(roomId);
    }
    public List<Room> searchRoomsByField(String searchField, String searchText) {
        try {
            switch (searchField) {
                case "roomName":
                    // Search by room name only
                    return roomRepository.findByRoomNameContaining(searchText);

                case "cinema":
                    // Search by cinema name only
                    return roomRepository.findByCinemaNameContaining(searchText);

                case "cinemaComplex":
                    // Search by cluster name only
                    return roomRepository.findByClusterNameContaining(searchText);

                case "seats":
                    try {
                        // Parse seat count if it's a number
                        Long seatCount = Long.parseLong(searchText);
                        // Get all rooms
                        List<Room> allRooms = roomRepository.findAll();
                        List<Room> matchingRooms = new ArrayList<>();

                        // For each room, check if its seat count matches the search criteria
                        for (Room room : allRooms) {
                            Long roomSeatCount = roomRepository.countSeatsByRoomId(room.getRoomId());
                            if (roomSeatCount.equals(seatCount)) {
                                matchingRooms.add(room);
                            }
                        }
                        return matchingRooms;
                    } catch (NumberFormatException ignored) {
                        // If not a number, search by seat row
                        return roomRepository.findBySeatRow(searchText);
                    }

                default:
                    // If the search field is not recognized, search all fields
                    return roomRepository.searchAllFields(searchText);
            }
        } catch (Exception e) {
            // Log the exception
            // Return empty list or throw an exception based on your error handling strategy
            return Collections.emptyList();
        }
    }

    @Transactional
    public int deleteRoomsByIds(List<Long> roomIds) {
        if (roomIds == null || roomIds.isEmpty()) {
            return 0;
        }
        return roomRepository.deleteByRoomIdIn(roomIds);
    }

    /**
     * Save a new room to the database
     *
     * @param room The room entity to save
     * @return The saved room with generated ID
     * @throws Exception If there's an error saving the room
     */
    public Room saveRoom(Room room) throws Exception {
        try {
            // Validation can be added here if needed
            if (room.getRoomName() == null || room.getRoomName().trim().isEmpty()) {
                throw new Exception("Room name cannot be empty");
            }

            if (room.getCinema() == null || room.getCinema().getCinemaId() == null) {
                throw new Exception("Cinema must be selected");
            }

            // Check if a room with the same name already exists in the same cinema
            Room existingRoom = roomRepository.findByRoomNameAndCinemaCinemaId(
                    room.getRoomName(),
                    room.getCinema().getCinemaId()
            );

            if (existingRoom != null) {
                throw new Exception("A room with this name already exists in the selected cinema");
            }

            // Save the room to the database
            return roomRepository.save(room);
        } catch (Exception e) {
            // You can log the exception here if needed
            // logger.error("Error saving room: " + e.getMessage(), e);

            // Re-throw the exception to be handled by the controller
            throw new Exception("Failed to save room: " + e.getMessage(), e);
        }
    }

    public Room saveRoomWhenEdit(Room room) throws Exception {
        try {
            // Validation can be added here if needed
            if (room.getRoomName() == null || room.getRoomName().trim().isEmpty()) {
                throw new Exception("Room name cannot be empty");
            }

            if (room.getCinema() == null || room.getCinema().getCinemaId() == null) {
                throw new Exception("Cinema must be selected");
            }

            // Save the room to the database
            return roomRepository.save(room);
        } catch (Exception e) {
            // You can log the exception here if needed
            // logger.error("Error saving room: " + e.getMessage(), e);

            // Re-throw the exception to be handled by the controller
            throw new Exception("Failed to save room: " + e.getMessage(), e);
        }
    }

    // Get all rooms with pagination directly from database
    public Page<Room> getAllRoomsPaginated(Pageable pageable) {
        return roomRepository.findAll(pageable);
    }

    // Search rooms by field with pagination
    public Page<Room> searchRoomsByFieldPaginated(String searchField, String searchText, Pageable pageable) {
        switch (searchField) {
            case "roomName":
                return roomRepository.findByRoomNameContainingPaginated(searchText, pageable);
            case "cinema":
                return roomRepository.findByCinemaNameContainingPaginated(searchText, pageable);
            case "cinemaComplex":
                return roomRepository.findByClusterNameContainingPaginated(searchText, pageable);
            case "seats":
                try {
                    Long seatCount = Long.parseLong(searchText);
                    return roomRepository.findBySeatCountPaginated(seatCount, pageable);
                } catch (NumberFormatException ignored) {
                    return roomRepository.findBySeatRowPaginated(searchText, pageable);
                }
            default:
                return roomRepository.searchAllFieldsPaginated(searchText, pageable);
        }
    }

    // Get seat counts for multiple rooms in a single query
    public Map<Long, Long> getSeatCountsForRooms(List<Long> roomIds) {
        if (roomIds.isEmpty()) {
            return new HashMap<>();
        }

        List<Object[]> results = roomRepository.countSeatsByRoomIds(roomIds);
        Map<Long, Long> seatCounts = new HashMap<>();

        for (Object[] result : results) {
            Long roomId = (Long) result[0];
            Long seatCount = (Long) result[1];
            seatCounts.put(roomId, seatCount);
        }

        return seatCounts;
    }

    /**
     * Get a room by ID with eager loading of necessary relations
     * This optimizes the edit form by loading all required data in a single query
     */
    public Optional<Room> getRoomByIdWithDetails(Long id) {
        return roomRepository.findByIdWithDetails(id);
    }

    /**
     * Direct update method for room that focuses only on updatable fields
     * to avoid unnecessary database operations
     */
    @Transactional
    public Room updateRoomDirect(Room room) throws Exception {
        if (room.getRoomName() == null || room.getRoomName().trim().isEmpty()) {
            throw new Exception("Room name cannot be empty");
        }

        if (room.getCinema() == null || room.getCinema().getCinemaId() == null) {
            throw new Exception("Cinema must be selected");
        }

        // Get the existing room to avoid loading all relationships
        Optional<Room> existingRoomOpt = roomRepository.findById(room.getRoomId());
        if (existingRoomOpt.isEmpty()) {
            throw new Exception("Room not found");
        }

        Room existingRoom = existingRoomOpt.get();

        // Only update the fields that can change to avoid unnecessary updates
        existingRoom.setRoomName(room.getRoomName());

        // Only update cinema if it actually changed
        if (!existingRoom.getCinema().getCinemaId().equals(room.getCinema().getCinemaId())) {
            // Get cinema reference without loading all of its data
            Cinema cinema = em.getReference(Cinema.class, room.getCinema().getCinemaId());
            existingRoom.setCinema(cinema);
        }

        return roomRepository.save(existingRoom);
    }
}