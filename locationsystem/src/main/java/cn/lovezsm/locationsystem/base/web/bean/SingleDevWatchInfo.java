package cn.lovezsm.locationsystem.base.web.bean;

import cn.lovezsm.locationsystem.base.bean.Message;
import cn.lovezsm.locationsystem.locationSystem.algorithm.LocationAlgorithm;
import lombok.Data;
import lombok.val;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SingleDevWatchInfo {

    private String devMac;

    private Map<String, LocationAlgorithm.RSSi> rssAvgMap = new HashMap<>();
    private Map<String,Integer> messageNumMap = new HashMap<>();

    public Map<String, Integer> getMessageNumMap() {
        return messageNumMap;
    }

    public void addMessageNum(String apMac, int val){
        if(!messageNumMap.containsKey(apMac)){
            messageNumMap.put(apMac,0);
        }
        Integer v = messageNumMap.get(apMac);
        messageNumMap.put(apMac,v+val);
    }

    public Map<String, LocationAlgorithm.RSSi> getRssAvgMap() {
        return rssAvgMap;
    }

    public String getDevMac() {
        return devMac;
    }

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

    public void cleanUp(){
        messageNumMap.clear();
        rssAvgMap.clear();
    }
}
