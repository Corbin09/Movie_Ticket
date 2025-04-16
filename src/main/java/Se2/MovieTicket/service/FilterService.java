package Se2.MovieTicket.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FilterService {
    @Autowired
    private ActorService actorService;
    @Autowired
    private FilmService filmService;
    @Autowired
    private CinemaService cinemaService;
    @Autowired
    private CinemaClusterService cinemaClusterService;
    @Autowired
    private RegionService regionService;
    @Autowired
    private RoomService roomService;

    public Map<String, List<?>> filterAll(String actorName, String filmName, String cinemaName, String clusterName, String regionName, String roomName) {
        Map<String, List<?>> results = new HashMap<>();
        results.put("actors", actorService.filterActors(actorName));
        results.put("films", filmService.filterFilms(filmName, null, null, null, null));
        results.put("cinemas", cinemaService.filterCinemas(cinemaName, null));
        results.put("clusters", cinemaClusterService.filterCinemaClusters(clusterName));
        results.put("regions", regionService.filterRegions(regionName));
        results.put("rooms", roomService.filterRooms(roomName));
        return results;
    }
}