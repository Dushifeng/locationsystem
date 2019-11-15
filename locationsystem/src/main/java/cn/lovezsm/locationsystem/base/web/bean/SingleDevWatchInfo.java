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
    private LocationAlgorithm.RSSi rssAvg;

    public SingleDevWatchInfo(String devMac,int apNum) {
        this.devMac = devMac;
        rssAvg = new LocationAlgorithm.RSSi(apNum);
    }

    public void pushRss(int apIndex,float rss){
        this.rssAvg.put(apIndex,rss);
    }
}
