package cn.lovezsm.locationsystem.base.web.bean;

import cn.lovezsm.locationsystem.base.bean.Message;
import cn.lovezsm.locationsystem.locationSystem.algorithm.LocationAlgorithm;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Data
public class SingleDevWatchInfo {

    private String devMac;
    private List<Message> messages = new ArrayList<>();
    private Map<String, LocationAlgorithm.RSSi> rssAvgMap = new HashMap<>();

    public SingleDevWatchInfo(String devMac) {
        this.devMac = devMac;
    }

    public void pushRss(String apMac,float rss){
        LocationAlgorithm.RSSi rs;
        if (rssAvgMap.containsKey(apMac)){
            rs = rssAvgMap.get(apMac);
        }else {
            rs = new LocationAlgorithm.RSSi(1);
            rssAvgMap.put(apMac,rs);
        }
        rs.put(0,rss);
    }
}
