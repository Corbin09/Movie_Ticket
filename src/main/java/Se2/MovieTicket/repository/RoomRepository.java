package Se2.MovieTicket.repository;

import Se2.MovieTicket.model.Room;
import Se2.MovieTicket.model.Cinema;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    @Query("SELECT r FROM Room r WHERE r.cinema = :cinema")
    List<Room> findByCinema(@Param("cinema") Cinema cinema);

    @Query("SELECT r FROM Room r WHERE r.cinema.cinemaId = :cinemaId")
    List<Room> findByCinemaCinemaId(@Param("cinemaId") Long cinemaId);

    @Query("SELECT r FROM Room r WHERE r.roomId = :roomId")
    List<Room> findByRoomId(@Param("roomId") Long roomId);

    @Query("SELECT r FROM Room r WHERE LOWER(r.roomName) LIKE LOWER(CONCAT('%', :roomName, '%'))")
    List<Room> findByRoomNameContaining(@Param("roomName") String roomName);

    @Query("SELECT r FROM Room r WHERE r.cinema.cinemaId = :cinemaId")
    List<Room> findByCinemaId(@Param("cinemaId") Long cinemaId);

    @Query("SELECT r FROM Room r WHERE LOWER(r.cinema.cinemaName) LIKE LOWER(CONCAT('%', :cinemaName, '%'))")
    List<Room> findByCinemaNameContaining(@Param("cinemaName") String cinemaName);

    @Query("SELECT r FROM Room r WHERE r.cinema.cinemaCluster.clusterId = :clusterId")
    List<Room> findByClusterId(@Param("clusterId") Long clusterId);

    @Query("SELECT r FROM Room r WHERE LOWER(r.cinema.cinemaCluster.clusterName) LIKE LOWER(CONCAT('%', :clusterName, '%'))")
    List<Room> findByClusterNameContaining(@Param("clusterName") String clusterName);


    @Query("SELECT DISTINCT r FROM Room r JOIN r.seats s WHERE s.seatNumber = :seatNumber")
    List<Room> findBySeatNumber(@Param("seatNumber") Integer seatNumber);

    @Query("SELECT DISTINCT r FROM Room r JOIN r.seats s WHERE LOWER(s.seatRow) LIKE LOWER(CONCAT('%', :seatRow, '%'))")
    List<Room> findBySeatRow(@Param("seatRow") String seatRow);

    @Query("SELECT DISTINCT r FROM Room r LEFT JOIN r.cinema c LEFT JOIN c.cinemaCluster cc LEFT JOIN c.region reg LEFT JOIN r.seats s WHERE " +
            "CAST(r.roomId AS string) LIKE CONCAT('%', :searchText, '%') OR " +
            "LOWER(r.roomName) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
            "CAST(c.cinemaId AS string) LIKE CONCAT('%', :searchText, '%') OR " +
            "LOWER(c.cinemaName) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
            "CAST(cc.clusterId AS string) LIKE CONCAT('%', :searchText, '%') OR " +
            "LOWER(cc.clusterName) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
            "CAST(s.seatNumber AS string) LIKE CONCAT('%', :searchText, '%') OR " +
            "LOWER(s.seatRow) LIKE LOWER(CONCAT('%', :searchText, '%'))")
    List<Room> searchAllFields(@Param("searchText") String searchText);

    @Query("SELECT COUNT(s) FROM Seat s WHERE s.room.roomId = :roomId")
    Long countSeatsByRoomId(@Param("roomId") Long roomId);

    // Paginated versions of search methods
    @Query("SELECT r FROM Room r WHERE LOWER(r.roomName) LIKE LOWER(CONCAT('%', :roomName, '%'))")
    Page<Room> findByRoomNameContainingPaginated(@Param("roomName") String roomName, Pageable pageable);

    @Query("SELECT r FROM Room r WHERE LOWER(r.cinema.cinemaName) LIKE LOWER(CONCAT('%', :cinemaName, '%'))")
    Page<Room> findByCinemaNameContainingPaginated(@Param("cinemaName") String cinemaName, Pageable pageable);

    @Query("SELECT r FROM Room r WHERE LOWER(r.cinema.cinemaCluster.clusterName) LIKE LOWER(CONCAT('%', :clusterName, '%'))")
    Page<Room> findByClusterNameContainingPaginated(@Param("clusterName") String clusterName, Pageable pageable);

    @Query("SELECT DISTINCT r FROM Room r JOIN r.seats s WHERE LOWER(s.seatRow) LIKE LOWER(CONCAT('%', :seatRow, '%'))")
    Page<Room> findBySeatRowPaginated(@Param("seatRow") String seatRow, Pageable pageable);

    @Query("SELECT r FROM Room r WHERE (SELECT COUNT(s) FROM Seat s WHERE s.room = r) = :seatCount")
    Page<Room> findBySeatCountPaginated(@Param("seatCount") Long seatCount, Pageable pageable);

    @Query(value = "SELECT DISTINCT r FROM Room r LEFT JOIN FETCH r.cinema c LEFT JOIN FETCH c.cinemaCluster cc " +
            "WHERE LOWER(r.roomName) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
            "LOWER(c.cinemaName) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
            "LOWER(cc.clusterName) LIKE LOWER(CONCAT('%', :searchText, '%'))",
            countQuery = "SELECT COUNT(DISTINCT r) FROM Room r LEFT JOIN r.cinema c LEFT JOIN c.cinemaCluster cc " +
                    "WHERE LOWER(r.roomName) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
                    "LOWER(c.cinemaName) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
                    "LOWER(cc.clusterName) LIKE LOWER(CONCAT('%', :searchText, '%'))")
    Page<Room> searchAllFieldsPaginated(@Param("searchText") String searchText, Pageable pageable);

    // Efficient batch query for seat counts
    @Query("SELECT r.roomId, COUNT(s) FROM Room r LEFT JOIN r.seats s WHERE r.roomId IN :roomIds GROUP BY r.roomId")
    List<Object[]> countSeatsByRoomIds(@Param("roomIds") List<Long> roomIds);

    @Transactional
    @Modifying
    @Query("DELETE FROM Room r WHERE r.roomId IN :roomIds")
    int deleteByRoomIdIn(@Param("roomIds") List<Long> roomIds);

    /**
     * Find room by ID with eager loading of necessary relations
     */
    @Query("SELECT r FROM Room r LEFT JOIN FETCH r.cinema c WHERE r.roomId = :id")
    Optional<Room> findByIdWithDetails(@Param("id") Long id);

    @Query("SELECT r FROM Room r WHERE r.roomName = :roomName AND r.cinema.cinemaId = :cinemaId")
    Room findByRoomNameAndCinemaCinemaId(
            @Param("roomName") String roomName,
            @Param("cinemaId") Long cinemaId
    );
}
