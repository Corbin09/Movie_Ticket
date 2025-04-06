//package Se2.MovieTicket.controllers;
//
//
//import Se2.MovieTicket.dto.ChartData;
//import Se2.MovieTicket.service.ChartService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/chart")
//@RequiredArgsConstructor
//public class ChartRestController {
//    private final ChartService chartService;
//
//    @GetMapping("/init")
//    public List<ChartData> getInitialData() {
//        return chartService.getChartData();
//    }
//}
