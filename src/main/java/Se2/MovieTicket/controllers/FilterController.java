package Se2.MovieTicket.controllers;

import Se2.MovieTicket.service.FilterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/filter")
public class FilterController {
    @Autowired
    private FilterService filterService;

    @GetMapping
    public ResponseEntity<Map<String, List<?>>> filterAll(@RequestParam(required = false) String actorName,
                                                          @RequestParam(required = false) String filmName,
                                                          @RequestParam(required = false) String cinemaName,
                                                          @RequestParam(required = false) String clusterName,
                                                          @RequestParam(required = false) String regionName,
                                                          @RequestParam(required = false) String roomName) {
        Map<String, List<?>> results = filterService.filterAll(actorName, filmName, cinemaName, clusterName, regionName, roomName);
        return new ResponseEntity<>(results, HttpStatus.OK);
    }
}