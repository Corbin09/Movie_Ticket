package Se2.MovieTicket.service;

import Se2.MovieTicket.dto.SeatStatusDTO;
import Se2.MovieTicket.model.SeatStatus;
import Se2.MovieTicket.repository.SeatStatusRepository;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class SeatStatusService {
    @Autowired
    private SeatStatusRepository seatStatusRepository;

    public List<SeatStatus> getAllSeatStatuses() {
        return seatStatusRepository.findAll();
    }

    public Optional<SeatStatus> getSeatStatusById(Long id) {
        return seatStatusRepository.findById(id);
    }

    public SeatStatus createSeatStatus(SeatStatusDTO seatStatusDTO) {
        SeatStatus seatStatus = new SeatStatus();
        seatStatus.setSeatId(seatStatusDTO.getSeatId());
        seatStatus.setShowtimeId(seatStatusDTO.getShowtimeId());
        seatStatus.setSeatStatus(seatStatusDTO.getSeatStatus());
        seatStatus.setReservedUntil(seatStatusDTO.getReservedUntil());
        return seatStatusRepository.save(seatStatus);
    }

    public SeatStatus updateSeatStatus(Long id, SeatStatusDTO seatStatusDTO) {
        Optional<SeatStatus> seatStatusData = seatStatusRepository.findById(id);
        if (seatStatusData.isPresent()) {
            SeatStatus seatStatus = seatStatusData.get();
            seatStatus.setSeatId(seatStatusDTO.getSeatId());
            seatStatus.setShowtimeId(seatStatusDTO.getShowtimeId());
            seatStatus.setSeatStatus(seatStatusDTO.getSeatStatus());
            seatStatus.setReservedUntil(seatStatusDTO.getReservedUntil());
            return seatStatusRepository.save(seatStatus);
        }
        return null;
    }

    public void deleteSeatStatus(Long id) {
        seatStatusRepository.deleteById(id);
    }

    @Autowired
    private EntityManager entityManager;

    public void saveSeatStatus(SeatStatus seatStatus) {
        seatStatusRepository.save(seatStatus);
    }

    @Transactional
    public int updateSeatStatusesInBatch(Set<Long> seatIds, Long showtimeId, String status) {
        return seatStatusRepository.updateStatusInBatch(seatIds, showtimeId, status);
    }
}