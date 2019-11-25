package cn.lovezsm.locationsystem.base.service;

import cn.lovezsm.locationsystem.base.bean.Message;
import cn.lovezsm.locationsystem.base.config.LocationConfig;
import cn.lovezsm.locationsystem.base.data.CollectionInfoCache;
import cn.lovezsm.locationsystem.base.data.DataCache;
import cn.lovezsm.locationsystem.base.util.DataParser;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LocationPorter extends Porter {

    @Autowired
    private DataCache cache;

    @Autowired
    private LocationConfig config;

    public LocationPorter() {
        super("LocationPorter");
    }

    public LocationPorter(String name){
        super(name);
        this.name = name;
    }

    @Override
    public void port(List<Message> messages) {
        cache.addAll(messages,config.getSlidingStep()*1000);
    }
}
