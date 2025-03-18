package Se2.MovieTicket.controllers;

import Se2.MovieTicket.dto.RegionDTO;
import Se2.MovieTicket.model.Region;
import Se2.MovieTicket.service.RegionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/regions")
public class RegionController {
    @Autowired
    private RegionService regionService;

    @GetMapping
    public ResponseEntity<List<Region>> getAllRegions() {
        List<Region> regions = regionService.getAllRegions();
        return regions.isEmpty() ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(regions, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Region> getRegionById(@PathVariable("id") Long id) {
        return regionService.getRegionById(id)
                .map(region -> new ResponseEntity<>(region, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<Region> createRegion(@RequestBody RegionDTO regionDTO) {
        Region newRegion = regionService.createRegion(regionDTO);
        return new ResponseEntity<>(newRegion, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Region> updateRegion(@PathVariable("id") Long id, @RequestBody RegionDTO regionDTO) {
        Region updatedRegion = regionService.updateRegion(id, regionDTO);
        return updatedRegion != null ? new ResponseEntity<>(updatedRegion, HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteRegion(@PathVariable("id") Long id) {
        regionService.deleteRegion(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}