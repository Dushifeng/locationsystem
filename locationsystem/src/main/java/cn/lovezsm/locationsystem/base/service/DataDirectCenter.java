package cn.lovezsm.locationsystem.base.service;

import cn.lovezsm.locationsystem.base.bean.Message;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
public class DataDirectCenter {

    private  volatile static Map<String, Porter> porterMap = new ConcurrentHashMap<>();

    static ExecutorService poolExecutor = Executors.newCachedThreadPool();


    public static void register(Porter porter){

        if (porterMap.containsKey(porter.name)){
            log.error("注册失败，已包含名字为:"+porter.name);
        }

        log.info(porter.name+"   已经注册");
        porterMap.put(porter.name,porter);
    }

    public static void unregister(String name){
        porterMap.remove(name);
    }

    public static void putData(String rawData){
        for (Map.Entry<String,Porter> entry:porterMap.entrySet()) {
            Porter porter = entry.getValue();

            poolExecutor.execute(()->{
               porter.port(rawData);
            });
        }
    }

}
