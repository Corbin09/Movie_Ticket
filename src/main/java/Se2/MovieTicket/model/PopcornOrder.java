package Se2.MovieTicket.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "popcorn_orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PopcornOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "popcorn_order_id")
    private Long popcornOrderId;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    @JsonIgnore
    private Order order;

    @ManyToOne
    @JoinColumn(name = "combo_id", nullable = false)
    @JsonIgnore
    private PopcornCombo popcornCombo;

    @Column(name = "combo_quantity")
    private Integer comboQuantity;

    public Long getPopcornOrderId() {
        return popcornOrderId;
    }

    public void setPopcornOrderId(Long popcornOrderId) {
        this.popcornOrderId = popcornOrderId;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public PopcornCombo getPopcornCombo() {
        return popcornCombo;
    }

    public void setPopcornCombo(PopcornCombo popcornCombo) {
        this.popcornCombo = popcornCombo;
    }

    public Integer getComboQuantity() {
        return comboQuantity;
    }

    public void setComboQuantity(Integer comboQuantity) {
        this.comboQuantity = comboQuantity;
    }

    public void setOrderId(Long orderId) {
        if (this.order == null) {
            this.order = new Order();
        }
        this.order.setOrderId(orderId);
    }

    public void setComboId(Long comboId) {
        if (this.popcornCombo == null) {
            this.popcornCombo = new PopcornCombo();
        }
        this.popcornCombo.setComboId(comboId);
    }
}