package cn.lovezsm.locationsystem.base.controller;

import cn.lovezsm.locationsystem.base.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
    public void start(){
        statisticsService.start();
    }

    @GetMapping("getStatisticsInfo")
    public Map<String, StatisticsService.Info> getStatisticsInfo(){

        return statisticsService.getData();
    }

    @GetMapping("stopStatistics")
    public void stop(){
        statisticsService.stop();
    }
}
