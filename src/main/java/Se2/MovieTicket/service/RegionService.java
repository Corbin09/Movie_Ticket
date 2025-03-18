package Se2.MovieTicket.service;

import Se2.MovieTicket.dto.RegionDTO;
import Se2.MovieTicket.model.Region;
import Se2.MovieTicket.repository.RegionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RegionService {
    @Autowired
    private RegionRepository regionRepository;

    public List<Region> getAllRegions() {
        return regionRepository.findAll();
    }

    public Optional<Region> getRegionById(Long id) {
        return regionRepository.findById(id);
    }

    public Region createRegion(RegionDTO regionDTO) {
        Region region = new Region();
        region.setRegionName(regionDTO.getRegionName());
        return regionRepository.save(region);
    }

    public Region updateRegion(Long id, RegionDTO regionDTO) {
        Optional<Region> regionData = regionRepository.findById(id);
        if (regionData.isPresent()) {
            Region region = regionData.get();
            region.setRegionName(regionDTO.getRegionName());
            return regionRepository.save(region);
        }
        return null;
    }

    public void deleteRegion(Long id) {
        regionRepository.deleteById(id);
    }
}