package Se2.MovieTicket.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Entity
@Table(name = "popcorn_combos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PopcornCombo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "combo_id")
    private Long comboId;

    @Column(name = "combo_name", nullable = false)
    private String comboName;

    @Column(name = "combo_price")
    private Double comboPrice;

    @OneToMany(mappedBy = "popcornCombo", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<PopcornOrder> popcornOrders;

    public Long getComboId() {
        return comboId;
    }

    public void setComboId(Long comboId) {
        this.comboId = comboId;
    }

    public String getComboName() {
        return comboName;
    }

    public void setComboName(String comboName) {
        this.comboName = comboName;
    }

    public Double getComboPrice() {
        return comboPrice;
    }

    public void setComboPrice(Double comboPrice) {
        this.comboPrice = comboPrice;
    }

    public Set<PopcornOrder> getPopcornOrders() {
        return popcornOrders;
    }

    public void setPopcornOrders(Set<PopcornOrder> popcornOrders) {
        this.popcornOrders = popcornOrders;
    }
}