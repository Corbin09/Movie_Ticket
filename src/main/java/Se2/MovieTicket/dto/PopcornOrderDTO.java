package Se2.MovieTicket.dto;

import lombok.Data;

@Data
public class PopcornOrderDTO {
    private Long popcornOrderId;
    private Long orderId;
    private Long comboId;
    private Integer comboQuantity;

    public Long getPopcornOrderId() {
        return popcornOrderId;
    }

    public void setPopcornOrderId(Long popcornOrderId) {
        this.popcornOrderId = popcornOrderId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getComboId() {
        return comboId;
    }

    public void setComboId(Long comboId) {
        this.comboId = comboId;
    }

    public Integer getComboQuantity() {
        return comboQuantity;
    }

    public void setComboQuantity(Integer comboQuantity) {
        this.comboQuantity = comboQuantity;
    }
}