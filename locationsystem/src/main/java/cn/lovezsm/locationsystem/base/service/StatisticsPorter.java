package cn.lovezsm.locationsystem.base.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StatisticsPorter extends Porter<String>{



    @Autowired
    StatisticsService statisticsService;

    public StatisticsPorter() {
        super("StatisticsPorter");
    }


    public StatisticsPorter(String name) {
        super(name);
        this.name = name;
    }

    @Override
    void port(String rawData) {
        statisticsService.readData(rawData);
    }
}
