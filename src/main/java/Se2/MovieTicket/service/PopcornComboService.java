package Se2.MovieTicket.service;

import Se2.MovieTicket.dto.PopcornComboDTO;
import Se2.MovieTicket.model.PopcornCombo;
import Se2.MovieTicket.repository.PopcornComboRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PopcornComboService {
    @Autowired
    private PopcornComboRepository popcornComboRepository;

    public List<PopcornCombo> getAllPopcornCombos() {
        return popcornComboRepository.findAll();
    }

    public Optional<PopcornCombo> getPopcornComboById(Long id) {
        return popcornComboRepository.findById(id);
    }

    public PopcornCombo createPopcornCombo(PopcornComboDTO popcornComboDTO) {
        PopcornCombo popcornCombo = new PopcornCombo();
        popcornCombo.setComboName(popcornComboDTO.getComboName());
        popcornCombo.setComboPrice(popcornComboDTO.getComboPrice());
        return popcornComboRepository.save(popcornCombo);
    }

    public PopcornCombo updatePopcornCombo(Long id, PopcornComboDTO popcornComboDTO) {
        Optional<PopcornCombo> popcornComboData = popcornComboRepository.findById(id);
        if (popcornComboData.isPresent()) {
            PopcornCombo popcornCombo = popcornComboData.get();
            popcornCombo.setComboName(popcornComboDTO.getComboName());
            popcornCombo.setComboPrice(popcornComboDTO.getComboPrice());
            return popcornComboRepository.save(popcornCombo);
        }
        return null;
    }

    public void deletePopcornCombo(Long id) {
        popcornComboRepository.deleteById(id);
    }
}