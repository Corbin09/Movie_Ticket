package Se2.MovieTicket.service;

import Se2.MovieTicket.dto.PopcornOrderDTO;
import Se2.MovieTicket.model.PopcornOrder;
import Se2.MovieTicket.repository.PopcornOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PopcornOrderService {
    @Autowired
    private PopcornOrderRepository popcornOrderRepository;

    public List<PopcornOrder> getAllPopcornOrders() {
        return popcornOrderRepository.findAll();
    }

    public Optional<PopcornOrder> getPopcornOrderById(Long id) {
        return popcornOrderRepository.findById(id);
    }

    public PopcornOrder createPopcornOrder(PopcornOrderDTO popcornOrderDTO) {
        PopcornOrder popcornOrder = new PopcornOrder();
        popcornOrder.setOrderId(popcornOrderDTO.getOrderId());
        popcornOrder.setComboId(popcornOrderDTO.getComboId());
        popcornOrder.setComboQuantity(popcornOrderDTO.getComboQuantity());
        return popcornOrderRepository.save(popcornOrder);
    }

    public PopcornOrder updatePopcornOrder(Long id, PopcornOrderDTO popcornOrderDTO) {
        Optional<PopcornOrder> popcornOrderData = popcornOrderRepository.findById(id);
        if (popcornOrderData.isPresent()) {
            PopcornOrder popcornOrder = popcornOrderData.get();
            popcornOrder.setOrderId(popcornOrderDTO.getOrderId());
            popcornOrder.setComboId(popcornOrderDTO.getComboId());
            popcornOrder.setComboQuantity(popcornOrderDTO.getComboQuantity());
            return popcornOrderRepository.save(popcornOrder);
        }
        return null;
    }

    public void deletePopcornOrder(Long id) {
        popcornOrderRepository.deleteById(id);
    }
}