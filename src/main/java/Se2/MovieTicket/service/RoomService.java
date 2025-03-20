package Se2.MovieTicket.service;

import Se2.MovieTicket.dto.RoomDTO;
import Se2.MovieTicket.model.Room;
import Se2.MovieTicket.repository.RoomRepository;
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
public class RoomService {
    @Autowired
    private RoomRepository roomRepository;

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
}