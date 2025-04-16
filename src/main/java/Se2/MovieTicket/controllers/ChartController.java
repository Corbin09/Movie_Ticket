package Se2.MovieTicket.controllers;//package Se2.MovieTicket.controllers;
//
//import Se2.MovieTicket.dto.ChartData;
//import Se2.MovieTicket.service.ChartService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.messaging.simp.SimpMessagingTemplate;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.GetMapping;
//
//import java.util.List;
//
//@Controller
//@RequiredArgsConstructor
//public class ChartController {
//    private final SimpMessagingTemplate messagingTemplate;
//    private final ChartService chartService;
//
//    // Đẩy dữ liệu mới mỗi 5 giây (hoặc trigger theo business logic)
//    @Scheduled(fixedRate = 5000)
//    public void sendChartData() {
//        List<ChartData> data = chartService.getChartData();
//        messagingTemplate.convertAndSend("/topic/chart-data", data);
//    }
//
//    @GetMapping("/chart.html")
//    public String getChartPage() {
//        return "chart";
//    }
//}