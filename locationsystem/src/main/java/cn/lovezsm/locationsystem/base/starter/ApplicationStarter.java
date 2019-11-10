package cn.lovezsm.locationsystem.base.starter;


import cn.lovezsm.locationsystem.base.config.LocationConfig;
import cn.lovezsm.locationsystem.base.util.ClassUtils;
import cn.lovezsm.locationsystem.locationSystem.algorithm.LocationAlgorithm;
import cn.lovezsm.locationsystem.udpServer.server.netty.NettyUDPServer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.List;

@Component
public class ApplicationStarter implements CommandLineRunner {
    @Override
    public void run(String... args) throws Exception {
        List<Class> sonClass = ClassUtils.getSonClass(LocationAlgorithm.class);
        for (Class clazz:sonClass){
            Field algorithmName = clazz.getField("algorithmName");
            String name = (String) algorithmName.get(clazz.getDeclaredConstructor().newInstance());
            LocationConfig.locationAlgorithmMap.put(name,clazz);
        }
        NettyUDPServer server = NettyUDPServer.getINSTANCE();
        server.start();
    }
}
