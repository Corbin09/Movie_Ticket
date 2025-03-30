package Se2.MovieTicket.repository;

import Se2.MovieTicket.model.Room;
import Se2.MovieTicket.model.Cinema;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

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




    @Transactional
    @Modifying
    @Query("DELETE FROM Room r WHERE r.id IN :roomIds")
    int deleteByRoomIdIn(@Param("roomIds") List<Long> roomIds);

    @Query("SELECT r FROM Room r WHERE r.roomName = :roomName AND r.cinema.cinemaId = :cinemaId")
    Room findByRoomNameAndCinemaCinemaId(
            @Param("roomName") String roomName,
            @Param("cinemaId") Long cinemaId
    );
}
