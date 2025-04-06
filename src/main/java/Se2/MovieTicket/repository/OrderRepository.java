package Se2.MovieTicket.repository;

import Se2.MovieTicket.dto.*;
import Se2.MovieTicket.model.Order;
import Se2.MovieTicket.model.User;
import Se2.MovieTicket.model.Showtime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("SELECT o FROM Order o WHERE o.user = :user")
    List<Order> findByUser (@Param("user") User user);

    @Query("SELECT o FROM Order o WHERE o.showtime = :showtime")
    List<Order> findByShowtime(@Param("showtime") Showtime showtime);
    @Query("SELECT o FROM Order o " +
            "JOIN FETCH o.showtime s " +
            "JOIN FETCH s.film f " +
            "JOIN FETCH s.cinema c " +
            "JOIN FETCH s.room r " +
            "JOIN FETCH o.user u")
    List<Order> findAllWithDetails();

    @Query("SELECT o FROM Order o WHERE o.orderDate BETWEEN :start AND :end")
    List<Order> findByOrderDateBetween(@Param("start") Date start, @Param("end") Date end);

    /**
     * Find all orders for a specific user by user ID
     *
     * @param userId the ID of the user
     * @return list of orders associated with the user
     */
    List<Order> findByUserUserId(Long userId);

    @Query("SELECT new Se2.MovieTicket.dto.RevenueChartDTO(f.filmName, DATE(o.orderDate), SUM(o.totalPrice)) " +
            "FROM Order o " +
            "JOIN o.showtime s " +
            "JOIN s.film f " +
            "WHERE f.filmName IS NOT NULL AND o.orderDate IS NOT NULL " +
            "GROUP BY f.filmName, DATE(o.orderDate) " +
            "ORDER BY DATE(o.orderDate)")
    List<RevenueChartDTO> getFilmRevenueStats();



//    @Query("SELECT new Se2.MovieTicket.dto.UserSpendingDTO(u.username, SUM(o.totalPrice)) " +
//            "FROM Order o JOIN o.user u " +
//            "GROUP BY u.userId, u.username " +
//            "ORDER BY SUM(o.totalPrice) DESC")
//    List<UserSpendingDTO> getTopUsersBySpending(Pageable pageable);
//
//
//    @Query("SELECT new Se2.MovieTicket.dto.RevenueChartDTO(f.filmName, DATE(o.orderDate), SUM(o.totalPrice)) " +
//            "FROM Order o " +
//            "JOIN o.showtime s " +
//            "JOIN s.film f " +
//            "GROUP BY f.filmName, DATE(o.orderDate) " +
//            "ORDER BY DATE(o.orderDate)")
//    List<RevenueChartDTO> fetchRevenueChartData();

    @Query("SELECT new Se2.MovieTicket.dto.MonthlyRevenueDTO(FUNCTION('MONTHNAME', o.orderDate), SUM(o.totalPrice)) " +
            "FROM Order o GROUP BY FUNCTION('MONTH', o.orderDate)")
    List<MonthlyRevenueDTO> getMonthlyRevenue();

    @Query("SELECT new Se2.MovieTicket.dto.FilmRatingDTO(f.filmId, COALESCE(fr.filmRate, 0.0), COALESCE(fr.sumRate, 0), COALESCE(fr.sumStar, 0)) " +
            "FROM Film f LEFT JOIN f.filmRating fr " +
            "WHERE f.filmName IS NOT NULL")
    List<FilmRatingDTO> getAverageRatingByFilm();

//    @Query("SELECT new Se2.MovieTicket.dto.DashboardSummaryDTO(SUM(t.ticketPrice), COUNT(t.ticketId), COUNT(DISTINCT o.showtime.film.filmId), COUNT(DISTINCT o.user.userId)) " +
//            "FROM Order o JOIN o.tickets t")
//    DashboardSummaryDTO fetchDashboardSummary();



//    @Query("SELECT new Se2.MovieTicket.dto.FilmRevenueDTO(f.filmName, SUM(t.ticketPrice), COUNT(DISTINCT o.orderId), " +
//            "COALESCE((SELECT AVG(ur.star) FROM UserReview ur WHERE ur.film = f), 0)) " +
//            "FROM Order o JOIN o.tickets t JOIN o.showtime s JOIN s.film f " +
//            "WHERE (o.orderDate IS NULL OR CAST(o.orderDate AS LocalDate) BETWEEN :start AND :end) " +
//            "GROUP BY f.filmId, f.filmName " +
//            "ORDER BY SUM(t.ticketPrice) DESC")
//    List<FilmRevenueDTO> fetchFilmRevenueDetails(@Param("start") LocalDate start, @Param("end") LocalDate end);

    // Revenue Chart Query - Add date parameters
    @Query("SELECT new Se2.MovieTicket.dto.RevenueChartDTO(f.filmName, CAST(o.orderDate AS date), SUM(o.totalPrice)) " +
            "FROM Order o " +
            "JOIN o.showtime s " +
            "JOIN s.film f " +
            "WHERE o.orderDate IS NOT NULL " +
            "AND CAST(o.orderDate AS date) BETWEEN CAST(:start AS date) AND CAST(:end AS date) " +
            "GROUP BY f.filmName, CAST(o.orderDate AS date) " +
            "ORDER BY CAST(o.orderDate AS date)")
    List<RevenueChartDTO> fetchRevenueChartData(@Param("start") LocalDate start, @Param("end") LocalDate end);

    // Top Users Query - Add date parameters
    @Query("SELECT new Se2.MovieTicket.dto.UserSpendingDTO(u.username, SUM(o.totalPrice)) " +
            "FROM Order o JOIN o.user u " +
            "WHERE CAST(o.orderDate AS date) BETWEEN CAST(:start AS date) AND CAST(:end AS date) " +
            "GROUP BY u.userId, u.username " +
            "ORDER BY SUM(o.totalPrice) DESC")
    List<UserSpendingDTO> getTopUsersBySpending(Pageable pageable, @Param("start") LocalDate start, @Param("end") LocalDate end);

    // Dashboard Summary - Add date parameters
    @Query("SELECT new Se2.MovieTicket.dto.DashboardSummaryDTO(SUM(t.ticketPrice), COUNT(t.ticketId), COUNT(DISTINCT o.showtime.film.filmId), COUNT(DISTINCT o.user.userId)) " +
            "FROM Order o JOIN o.tickets t " +
            "WHERE CAST(o.orderDate AS date) BETWEEN CAST(:start AS date) AND CAST(:end AS date)")
    DashboardSummaryDTO fetchDashboardSummary(@Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query("SELECT new Se2.MovieTicket.dto.FilmRevenueDTO(f.filmName, SUM(t.ticketPrice), COUNT(t.ticketId), " +
            "COALESCE((SELECT AVG(ur.star) FROM UserReview ur WHERE ur.film = f), 0)) " +
            "FROM Order o JOIN o.tickets t JOIN o.showtime s JOIN s.film f " +
            "WHERE (o.orderDate IS NULL OR CAST(o.orderDate AS date) BETWEEN CAST(:start AS date) AND CAST(:end AS date)) " +
            "GROUP BY f.filmId, f.filmName " +
            "ORDER BY SUM(t.ticketPrice) DESC")
    List<FilmRevenueDTO> fetchFilmRevenueDetails(@Param("start") LocalDate start, @Param("end") LocalDate end);

    // Chuyển đổi các phương thức phân trang thành JPQL Query
    @Query("SELECT o FROM Order o WHERE o.orderId = :orderId")
    Page<Order> findByOrderId(@Param("orderId") Long orderId, Pageable pageable);

    @Query("SELECT o FROM Order o JOIN o.user u WHERE LOWER(u.username) LIKE LOWER(CONCAT('%', :username, '%'))")
    Page<Order> findByUserUsernameContainingIgnoreCase(@Param("username") String username, Pageable pageable);

    @Query("SELECT o FROM Order o JOIN o.showtime s JOIN s.film f WHERE LOWER(f.filmName) LIKE LOWER(CONCAT('%', :filmName, '%'))")
    Page<Order> findByShowtimeFilmFilmNameContainingIgnoreCase(@Param("filmName") String filmName, Pageable pageable);

    @Query("SELECT o FROM Order o JOIN o.showtime s JOIN s.cinema c WHERE LOWER(c.cinemaName) LIKE LOWER(CONCAT('%', :cinemaName, '%'))")
    Page<Order> findByShowtimeCinemaCinemaNameContainingIgnoreCase(@Param("cinemaName") String cinemaName, Pageable pageable);

    @Query("SELECT o FROM Order o JOIN o.showtime s JOIN s.room r WHERE LOWER(r.roomName) LIKE LOWER(CONCAT('%', :roomName, '%'))")
    Page<Order> findByShowtimeRoomRoomNameContainingIgnoreCase(@Param("roomName") String roomName, Pageable pageable);

    @Query("SELECT o FROM Order o JOIN o.showtime s WHERE s.showDate = :showDate")
    Page<Order> findByShowtimeShowDate(@Param("showDate") Date showDate, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.totalPrice = :totalPrice")
    Page<Order> findByTotalPrice(@Param("totalPrice") double totalPrice, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.orderDate = :orderDate")
    Page<Order> findByOrderDate(@Param("orderDate") Date orderDate, Pageable pageable);
}
