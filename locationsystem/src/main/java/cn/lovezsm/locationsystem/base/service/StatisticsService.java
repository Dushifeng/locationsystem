package cn.lovezsm.locationsystem.base.service;

import cn.lovezsm.locationsystem.base.bean.AP;
import cn.lovezsm.locationsystem.base.bean.Message;
import cn.lovezsm.locationsystem.base.config.APConfig;
import cn.lovezsm.locationsystem.base.util.DataParser;
import cn.lovezsm.locationsystem.base.web.bean.SingleDevWatchInfo;
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

    private Map<String, SingleDevWatchInfo> devWatchInfoMap = new ConcurrentHashMap<>();

    private ReentrantLock lock = new ReentrantLock();

    private static ScheduledFuture future;
    @Autowired
    private StatisticsPorter porter;

    @Autowired
    APConfig apConfig;

    public StatisticsService() {
    }

    public void start(int second){
        if (future!=null&&!future.isCancelled()){
            future.cancel(false);
        }
        Set<AP> apList = apConfig.getApList();
        for (AP ap:apList){
            statisticsInfo.put(ap.getMac(),new Info());
        }
        DataDirectCenter.register(porter);
        CleanUpRunner cleanUpRunner = new CleanUpRunner();
        future = swapExpiredPool.scheduleWithFixedDelay(cleanUpRunner, second, second, TimeUnit.SECONDS);
    }

    public void stop(){
        DataDirectCenter.unregister(porter.name);
        lastSecondInfo.clear();
        statisticsInfo.clear();
        devWatchInfoMap.clear();
        if (future==null){
            return;
        }
        future.cancel(false);
        future=null;

    }

    public SingleDevWatchInfo getSingleDevWatchInfo(String devMac){
        System.out.println(devWatchInfoMap);
        return devWatchInfoMap.get(devMac);
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

            //dev:
            SingleDevWatchInfo watchInfo;
            if (!devWatchInfoMap.containsKey(message.getDevMac())){
                watchInfo = new SingleDevWatchInfo(message.getDevMac());
                devWatchInfoMap.put(message.getDevMac(),watchInfo);
            }else {
                watchInfo = devWatchInfoMap.get(message.getDevMac());
            }

            watchInfo.getMessages().add(message);
            watchInfo.pushRss(message.getApMac(),message.getRssi());
        }
        lock.unlock();
    }

    public static class Info{
        private AtomicInteger dataNum = new AtomicInteger(0);//数据包个数
        private Map<String,Integer>  devMap = new ConcurrentHashMap<>();
        private Map<String,Integer> frequencyNum = new ConcurrentHashMap<>();
        private AtomicInteger rssNum = new AtomicInteger(0);//rss个数

        public void putFrequencyInfo(Integer frequency){
            if (!frequencyNum.containsKey(frequency+"")){
                frequencyNum.put(frequency+"",1);
            }else {
                frequencyNum.put(frequency+"",frequencyNum.get(frequency+"")+1);
            }
        }

        public void putDevMac(String devMac){
            if (!devMap.containsKey(devMac)){
                devMap.put(devMac,1);
            }else {
                devMap.put(devMac,devMap.get(devMac)+1);
            }
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

        public int getDataNum() {
            return dataNum.get();
        }

        public int getRssNum() {
            return rssNum.get();
        }

        public Map<String, Integer> getFrequencyNum() {
            return frequencyNum;
        }

        public Map<String, Integer> getDevMap() {
            return devMap;
        }

        public void cleanUp(){
            dataNum.set(0);
            devMap.clear();
            rssNum.set(0);
            frequencyNum.clear();

        }
    }

    private class CleanUpRunner implements Runnable{
        @Override
        public void run() {
            lock.lock();
            lastSecondInfo.putAll(statisticsInfo);
            statisticsInfo.clear();
            devWatchInfoMap.clear();
            lock.unlock();
        }
    }

}
