package cn.lovezsm.locationsystem.collectionSystem.bean;


import cn.lovezsm.locationsystem.base.data.CollectionResult;
import lombok.Data;
@Data
public class RegisterBean {
    private String mac;
    private boolean doing =false;
    private String uuid;
    private int gridmapIndex;
    private double gridX;
    private double gridY;
    private Long startTime;
    private Long endTime;
    private CollectionResult result = new CollectionResult();

    public RegisterBean(String mac, String uuid, int gridmapIndex, double gridX, double gridY, Long startTime) {
        this.mac = mac;
        this.uuid = uuid;
        this.gridmapIndex = gridmapIndex;
        this.gridX = gridX;
        this.gridY = gridY;
        this.startTime = startTime;
    }
}
