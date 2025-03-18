package Se2.MovieTicket.dto;

import lombok.Data;

@Data
public class CinemaClusterDTO {
    private Long clusterId;
    private String clusterName;

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
}