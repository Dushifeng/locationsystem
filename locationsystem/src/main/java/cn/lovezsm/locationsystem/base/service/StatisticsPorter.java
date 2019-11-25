package cn.lovezsm.locationsystem.base.service;

import cn.lovezsm.locationsystem.base.bean.Message;
import cn.lovezsm.locationsystem.base.controller.StatisticsController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class StatisticsPorter extends Porter{


    List<StatisticsController> statisticsControllers = new CopyOnWriteArrayList<>();

    public StatisticsPorter() {
        super("StatisticsPorter");
        DataDirectCenter.register(this);
    }


    public StatisticsPorter(String name) {
        super(name);
        this.name = name;
    }

    @Override
    void port(List<Message> messages) {
        for (StatisticsController controller:statisticsControllers) {
            controller.read(messages);
        }
    }

    public void registerController(StatisticsController controller){
        statisticsControllers.add(controller);
    }

    public void unregisterController(StatisticsController controller){
        statisticsControllers.remove(controller);
    }
}
