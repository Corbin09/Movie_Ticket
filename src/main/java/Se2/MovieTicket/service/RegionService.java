package Se2.MovieTicket.service;

import Se2.MovieTicket.dto.RegionDTO;
import Se2.MovieTicket.model.Region;
import Se2.MovieTicket.repository.RegionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RegionService {
    @Autowired
    private RegionRepository regionRepository;

    @Autowired
    private EntityManager em;

    public List<Region> filterRegions(String name) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Region> cq = cb.createQuery(Region.class);
        Root<Region> region = cq.from(Region.class);

        List<Predicate> predicates = new ArrayList<>();
        if (name != null && !name.isEmpty()) {
            predicates.add(cb.like(region.get("regionName"), "%" + name + "%"));
        }

        cq.where(predicates.toArray(new Predicate[0]));
        return em.createQuery(cq).getResultList();
    }

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