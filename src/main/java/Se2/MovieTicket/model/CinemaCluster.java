package Se2.MovieTicket.model;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Set;

@Entity
@Table(name = "cinema_clusters")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@ToString(exclude = {"cinemas"})
//@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "clusterId")
public class CinemaCluster {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cluster_id")
    private Long clusterId;

    @Column(name = "cluster_name", nullable = false)
    private String clusterName;

    @OneToMany(mappedBy = "cinemaCluster", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private Set<Cinema> cinemas;

    public Long getClusterId() {
        return clusterId;
    }

    public void setClusterId(Long clusterId) {
        this.clusterId = clusterId;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public Set<Cinema> getCinemas() {
        return cinemas;
    }

    public void setCinemas(Set<Cinema> cinemas) {
        this.cinemas = cinemas;
    }
}