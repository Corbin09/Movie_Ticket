package Se2.MovieTicket.service;

import Se2.MovieTicket.dto.SeatDTO;
import Se2.MovieTicket.model.Seat;
import Se2.MovieTicket.repository.SeatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SeatService {
    @Autowired
    private SeatRepository seatRepository;

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
}