package Se2.MovieTicket.service;

import Se2.MovieTicket.dto.*;
import Se2.MovieTicket.repository.OrderRepository;
import Se2.MovieTicket.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class DashboardService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    public List<UserGenderStatsDTO> getUserGenderStats() {
        return userRepository.countUsersBySex();
    }

    public List<FilmRatingDTO> getAverageRatings() {
        return orderRepository.getAverageRatingByFilm();
    }

    public List<MonthlyRevenueDTO> getMonthlyRevenue() {
        return orderRepository.getMonthlyRevenue();
    }

    public List<FilmRevenueDTO> getFilmRevenueDetails(LocalDate startDate, LocalDate endDate) {
        return orderRepository.fetchFilmRevenueDetails(startDate, endDate);
    }

    public List<RevenueChartDTO> getRevenueChartData(LocalDate startDate, LocalDate endDate) {
        return orderRepository.fetchRevenueChartData(startDate, endDate);
    }

    public List<UserSpendingDTO> getTopUserSpending(int limit, LocalDate startDate, LocalDate endDate) {
        return orderRepository.getTopUsersBySpending(PageRequest.of(0, limit), startDate, endDate);
    }

    public DashboardSummaryDTO getDashboardSummary(LocalDate startDate, LocalDate endDate) {
        return orderRepository.fetchDashboardSummary(startDate, endDate);
    }
}