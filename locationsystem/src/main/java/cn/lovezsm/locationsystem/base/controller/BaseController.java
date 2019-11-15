package cn.lovezsm.locationsystem.base.controller;

import cn.lovezsm.locationsystem.base.service.StatisticsService;
import lombok.extern.slf4j.XSlf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    public Map<String, StatisticsService.Info> getStatisticsInfo(){
        Map<String, StatisticsService.Info> data = statisticsService.getData();
        System.out.println(data);
        return data;
    }

    @GetMapping("stopStatistics")
    public void stop(){
        System.out.println("stop....");
        statisticsService.stop();
    }
}
