package cn.lovezsm.locationsystem.base.controller;

import cn.lovezsm.locationsystem.base.service.StatisticsService;
import cn.lovezsm.locationsystem.base.util.DataParser;
import cn.lovezsm.locationsystem.base.web.bean.SingleDevWatchInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 提供基础服务 如统计功能
 */
@RestController
@RequestMapping("api")
public class BaseController {

    @Autowired
    StatisticsService statisticsService;

    @RequestMapping("host")
    public String host() throws UnknownHostException {
        return InetAddress.getLocalHost().getHostAddress();
    }


    @GetMapping("startStatistics")
    public void start(@RequestParam(required = false) Integer t){
        System.out.println("start......"+t);
        if(t==null){
            t = 1;
        }

        statisticsService.start(t);
    }

    @PostMapping("getStatisticsInfo")
    public Map<String, Object> getStatisticsInfo(@RequestBody ArrayList<String> devMacs){
        Map<String, StatisticsService.Info> data = statisticsService.getData();
        Map<String,Object> ans = new HashMap<>();
        ans.put("all",data);
        for (String mac:devMacs){
            mac = DataParser.parseMac(mac);
            if (mac==null){
                continue;
            }
            SingleDevWatchInfo singleDevWatchInfo = statisticsService.getSingleDevWatchInfo(mac);
            System.out.println(singleDevWatchInfo);

            if (singleDevWatchInfo!=null){
                ans.put(mac,singleDevWatchInfo);
            }
        }
        return ans;
    }

    @GetMapping("getSingleDevWatchInfo")
    public SingleDevWatchInfo getSingleDevWatchInfo(String mac){
        return statisticsService.getSingleDevWatchInfo(mac);
    }

    @GetMapping("stopStatistics")
    public void stop(){
        System.out.println("stop....");
        statisticsService.stop();
    }
}
