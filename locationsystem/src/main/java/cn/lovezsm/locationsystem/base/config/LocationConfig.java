package cn.lovezsm.locationsystem.base.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@Component
public class LocationConfig{
    private int k = 6;
    private volatile int minNotNullRssNum = 3;
    private int slidingWindow = 10;
    private String locationAlgorithmName="localizeByRSSDAC";
    private int resultExpireQueueTime = 2;
    public static Map<String, Class> locationAlgorithmMap;
    private int slidingStep = 1;
    static {
        locationAlgorithmMap = new HashMap<>();
    }

    public LocationConfig() {

    }

    public static LocationConfig getDefault(){
        return new LocationConfig(6,3,10,"localizeByRSSDAC",2,1);
    }

}
