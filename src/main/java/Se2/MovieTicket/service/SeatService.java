package Se2.MovieTicket.service;

import Se2.MovieTicket.dto.SeatDTO;
import Se2.MovieTicket.model.Seat;
import Se2.MovieTicket.model.SeatStatus;
import Se2.MovieTicket.repository.SeatRepository;
import Se2.MovieTicket.repository.SeatStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SeatService {
    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private SeatStatusRepository seatStatusRepository;

    public List<Seat> getAllSeats() {
        return seatRepository.findAll();
    }

    public Optional<Seat> getSeatById(Long id) {
        return seatRepository.findById(id);
    }

    public Seat createSeat(SeatDTO seatDTO) {
        Seat seat = new Seat();
        seat.setRoomId(seatDTO.getRoomId());
        seat.setSeatRow(seatDTO.getSeatRow());
        seat.setSeatNumber(seatDTO.getSeatNumber());
        seat.setSeatType(seatDTO.getSeatType());
        return seatRepository.save(seat);
    }

    public Seat updateSeat(Long id, SeatDTO seatDTO) {
        Optional<Seat> seatData = seatRepository.findById(id);
        if (seatData.isPresent()) {
            Seat seat = seatData.get();
            seat.setRoomId(seatDTO.getRoomId());
            seat.setSeatRow(seatDTO.getSeatRow());
            seat.setSeatNumber(seatDTO.getSeatNumber());
            seat.setSeatType(seatDTO.getSeatType());
            return seatRepository.save(seat);
        }
        return null;
    }

    public void deleteSeat(Long id) {
        seatRepository.deleteById(id);
    }


    public List<SeatDTO> getSeatsByRoomId(Long roomId) {
        List<Seat> seats = seatRepository.findByRoomRoomId(roomId);
        return seats.stream()
                .map(this::convertToSeatDTO)
                .collect(Collectors.toList());
    }


    public Map<String, String> getSeatStatusMap(Long showtimeId) {
        List<SeatStatus> seatStatuses = seatStatusRepository.findByShowtimeShowtimeId(showtimeId);

        Map<String, String> statusMap = new HashMap<>();
        for (SeatStatus status : seatStatuses) {
            Seat seat = status.getSeat();
            String seatKey = seat.getSeatRow() + seat.getSeatNumber();
            statusMap.put(seatKey, status.getSeatStatus());
        }

        return statusMap;
    }

    private SeatDTO convertToSeatDTO(Seat seat) {
        SeatDTO dto = new SeatDTO();
        dto.setSeatId(seat.getSeatId());
        dto.setSeatRow(seat.getSeatRow());
        dto.setSeatNumber(seat.getSeatNumber());
        dto.setSeatType(seat.getSeatType());
        return dto;
    }
}