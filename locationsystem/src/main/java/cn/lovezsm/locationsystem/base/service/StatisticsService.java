package cn.lovezsm.locationsystem.base.service;

import cn.lovezsm.locationsystem.base.bean.Message;
import cn.lovezsm.locationsystem.base.util.DataParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 每秒清理一次，只统计数据，不存message
 */
@Service
public class StatisticsService {

    private static ScheduledExecutorService swapExpiredPool = new ScheduledThreadPoolExecutor(1);

    private Map<String,Info> statisticsInfo = new ConcurrentHashMap<>();

    private Map<String,Info> lastSecondInfo = new HashMap<>();

    private ReentrantLock lock = new ReentrantLock();

    private static ScheduledFuture future;
    @Autowired
    private StatisticsPorter porter;

    public StatisticsService() {

    }

    public void start(){

        if (future!=null&&!future.isCancelled()){
            future.cancel(false);
        }

        DataDirectCenter.register(porter);

        CleanUpRunner cleanUpRunner = new CleanUpRunner();
        future = swapExpiredPool.scheduleWithFixedDelay(cleanUpRunner, 1, 1, TimeUnit.SECONDS);
    }

    public void stop(){
        DataDirectCenter.unregister(porter.name);
        future.cancel(false);
        future=null;
        lastSecondInfo.clear();
        statisticsInfo.clear();
    }




    public Map<String,Info> getData(){
        return lastSecondInfo;
    }

    public void readData(String rawData){
        String apMac = DataParser.getAPMacFromRawData(rawData);
        Info info;
        lock.lock();
        if (statisticsInfo.containsKey(apMac)){
            info = statisticsInfo.get(apMac);
        }else {
            info = new Info();
            statisticsInfo.put(apMac,info);
        }

        info.dataIncrement();
        List<Message> messages = DataParser.parseRawData(rawData);
        for (Message message:messages) {

            info.putDevMac(message.getDevMac());
            info.putFrequencyInfo(message.getFrequency());
            info.rssNumIncrement();
        }
        lock.unlock();
    }

    public static class Info{
        private AtomicInteger dataNum = new AtomicInteger(0);//数据包个数
        private Map<String,Integer>  devMap = new ConcurrentHashMap<>();
        private Map<Integer,Integer> frequencyNum = new ConcurrentHashMap<>();
        private AtomicInteger rssNum = new AtomicInteger(0);//rss个数
        public void putFrequencyInfo(int frequency){
            Integer n = frequencyNum.getOrDefault(frequency, 0);
            frequencyNum.putIfAbsent(frequency,n+1);
        }

        public void putDevMac(String devMac){
            Integer n = devMap.getOrDefault(devMac, 0);
            devMap.putIfAbsent(devMac,n+1);
        }

        public void dataIncrement(){
            dataNum.incrementAndGet();
        }

        public void rssNumIncrement(){
            rssNum.incrementAndGet();
        }

        public int getUniqueDevMacNum(){
            return devMap.size();
        }

        public void cleanUp(){
            dataNum.set(0);
            devMap.clear();
            frequencyNum.clear();
        }
    }




    private class CleanUpRunner implements Runnable{
        @Override
        public void run() {
            lock.lock();
            lastSecondInfo.putAll(statisticsInfo);
            for(Map.Entry<String,Info> entry:statisticsInfo.entrySet()){
                entry.getValue().cleanUp();
            }
            lock.unlock();
        }
    }

}
