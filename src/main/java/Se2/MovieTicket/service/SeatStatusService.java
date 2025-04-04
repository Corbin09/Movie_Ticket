package Se2.MovieTicket.service;

import Se2.MovieTicket.dto.SeatStatusDTO;
import Se2.MovieTicket.model.Seat;
import Se2.MovieTicket.model.SeatStatus;
import Se2.MovieTicket.model.Showtime;
import Se2.MovieTicket.repository.SeatStatusRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
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

//    public SeatStatus findBySeatAndShowtime(Seat seat, Showtime showtime) {
//        return seatStatusRepository.findBySeatAndShowtime(seat, showtime);
//    }

//    public void saveSeatStatus(SeatStatus seatStatus) {
//        seatStatusRepository.save(seatStatus);
//    }

//    // New method to update multiple seat statuses at once
//    @Transactional
//    public void updateSeatStatusesInBatch(Set<Long> seatIds, Long showtimeId, String status) {
//        // Native query for better performance
//        Query query = entityManager.createNativeQuery(
//                "UPDATE seat_status SET seat_status = :status " +
//                        "WHERE seat_id IN :seatIds AND showtime_id = :showtimeId");
//
//        query.setParameter("status", status);
//        query.setParameter("seatIds", seatIds);
//        query.setParameter("showtimeId", showtimeId);
//
//        query.executeUpdate();
//    }
//    public SeatStatus findBySeatAndShowtime(Seat seat, Showtime showtime) {
//        return seatStatusRepository.findBySeatAndShowtime(seat, showtime).orElse(null);
//    }

    public void saveSeatStatus(SeatStatus seatStatus) {
        seatStatusRepository.save(seatStatus);
    }

    // Add this to SeatStatusService
    @Transactional
    public int updateSeatStatusesInBatch(Set<Long> seatIds, Long showtimeId, String status) {
        // Execute a single bulk update query instead of individual updates
        return seatStatusRepository.updateStatusInBatch(seatIds, showtimeId, status);
    }
}