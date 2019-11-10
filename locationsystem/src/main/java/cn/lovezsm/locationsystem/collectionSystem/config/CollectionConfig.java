package cn.lovezsm.locationsystem.collectionSystem.config;

import cn.lovezsm.locationsystem.base.bean.GridMap;
import cn.lovezsm.locationsystem.base.config.APConfig;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class CollectionConfig {
    private APConfig apConfig;
    private GridMap gridMap;
    private int inZoomNum = 170;
}
