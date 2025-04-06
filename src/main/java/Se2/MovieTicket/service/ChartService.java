package Se2.MovieTicket.service;

import Se2.MovieTicket.dto.ChartData;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChartService {
    public List<ChartData> getChartData() {
        return List.of(
                new ChartData("Movie A", 120),
                new ChartData("Movie B", 90),
                new ChartData("Movie C", 150)
        );
    }
}