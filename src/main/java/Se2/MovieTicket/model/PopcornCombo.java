package Se2.MovieTicket.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Table(name = "popcorn_combos")
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "popcornOrders")
@EqualsAndHashCode(exclude = "popcornOrders")
public class PopcornCombo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "combo_id")
    private Long comboId;

    @Column(name = "combo_name", nullable = false)
    private String comboName;

    @Column(name = "combo_price")
    private Double comboPrice;

    @Column(name = "combo_image")
    private String comboImg; // Thêm trường ảnh

    @OneToMany(mappedBy = "popcornCombo", cascade = CascadeType.ALL)
    @JsonManagedReference
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

    public String getComboImg() {
        return comboImg;
    }

    public void setComboImg(String comboImg) {
        this.comboImg = comboImg;
    }
}