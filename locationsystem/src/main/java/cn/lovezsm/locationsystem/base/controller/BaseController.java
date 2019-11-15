package cn.lovezsm.locationsystem.base.controller;

import cn.lovezsm.locationsystem.base.service.StatisticsService;
import cn.lovezsm.locationsystem.base.web.bean.SingleDevWatchInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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


    @GetMapping("startStatistics")
    public void start(@RequestParam(required = false) Integer t){
        System.out.println("start......");
        if(t==null){
            t = 1;
        }

        statisticsService.start(t);
    }

    @GetMapping("getStatisticsInfo")
    public Map<String, Object> getStatisticsInfo(List<String> devMacs){
        Map<String, StatisticsService.Info> data = statisticsService.getData();
        System.out.println(data);
        Map<String,Object> ans = new HashMap<>();
        ans.put("all",data);
        for (String mac:devMacs){
            SingleDevWatchInfo singleDevWatchInfo = statisticsService.getSingleDevWatchInfo(mac);
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
