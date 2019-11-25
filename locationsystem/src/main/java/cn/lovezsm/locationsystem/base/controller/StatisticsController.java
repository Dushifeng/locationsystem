package cn.lovezsm.locationsystem.base.controller;


import cn.lovezsm.locationsystem.base.bean.AP;
import cn.lovezsm.locationsystem.base.bean.Message;
import cn.lovezsm.locationsystem.base.config.APConfig;
import cn.lovezsm.locationsystem.base.service.DataDirectCenter;
import cn.lovezsm.locationsystem.base.service.StatisticsPorter;
import cn.lovezsm.locationsystem.base.util.DataParser;
import cn.lovezsm.locationsystem.base.util.SpringUtils;
import cn.lovezsm.locationsystem.base.web.bean.SingleDevWatchInfo;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import java.util.*;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 最多支持20个人同时调用统计功能
 * second不宜设置的太大
 */
@Component
@ServerEndpoint("/ws/statistics")
@Slf4j
public class StatisticsController {

    private Session session;
    private List<Message> messageList = new CopyOnWriteArrayList<>();
    private ScheduledFuture infoTask = null;
    private ReentrantLock lock = new ReentrantLock();
    private Map<String, Info> statisticsInfo = new ConcurrentHashMap<>();
    private Map<String, SingleDevWatchInfo> devWatchInfoMap = new ConcurrentHashMap<>();
    private volatile int second = 1;
    private volatile int timer = 0;
    private static APConfig apConfig;
    private static StatisticsPorter porter;
    @Autowired
    public void setApConfig(APConfig apConfig) {
        StatisticsController.apConfig = apConfig;
    }
    @Autowired
    public void setPorter(StatisticsPorter porter) {
        StatisticsController.porter = porter;
    }

    public void read(List<Message> messages){
        if (messages == null||messages.size()==0){
            return;
        }
        String apMac = messages.get(0).getApMac();
        if (!statisticsInfo.containsKey(apMac)){
           statisticsInfo.put(apMac,new Info());
        }
        Info info = statisticsInfo.get(apMac);
        info.dataIncrement();
        messageList.addAll(messages);
    }

    private static ScheduledExecutorService poolExecutor = new ScheduledThreadPoolExecutor(20);

    public void startStatistics(int second){
        this.second = second;
        porter.registerController(this);
        for (AP ap:apConfig.getApList()){
            statisticsInfo.put(ap.getMac(),new Info());
        }
    }

    public void stopStatistics(){
        if (infoTask!=null||(infoTask!=null&&!infoTask.isCancelled())){
            infoTask.cancel(true);
            infoTask = null;
        }

        //清理数据
        cleanUp();
        porter.unregisterController(this);
    }

    public void getStatisticsInfo(){
        if (infoTask!=null||(infoTask!=null&&!infoTask.isCancelled())){
            infoTask.cancel(false);
            infoTask = null;
        }

        infoTask =poolExecutor.scheduleAtFixedRate(()->{
            timer++;
            if (timer < second){
                return;
            }
            Map<String, Object> ans = new HashMap<>();
            Set<String> devMacSet = new HashSet<>();
            //分析数据
            lock.lock();
            try {
                for (Message message:messageList){
                    String apMac = message.getApMac();
                    if (!statisticsInfo.containsKey(apMac)){
                        statisticsInfo.put(apMac,new Info());
                    }
                    Info info = statisticsInfo.get(apMac);
                    info.putDevMac(message.getDevMac());
                    info.putFrequencyInfo(message.getFrequency());
                    info.rssNumIncrement();
                    String devMac = message.getDevMac();
                    devMacSet.add(devMac);
                    if (!devWatchInfoMap.containsKey(devMac)){
                        continue;
                    }
                    SingleDevWatchInfo singleDevWatchInfo = devWatchInfoMap.get(devMac);
                    singleDevWatchInfo.addMessageNum(apMac,1);
                    singleDevWatchInfo.pushRss(apMac,message.getRssi());
                }
                ans.put("all",statisticsInfo);
                ans.put("devMacNum",devMacSet.size());
                sendTextMessage(JSON.toJSONString(ans));

                for(Map.Entry<String,SingleDevWatchInfo> entry:devWatchInfoMap.entrySet()){
                    ans = new HashMap<>();
                    ans.put(entry.getKey(),entry.getValue());
                    sendTextMessage(JSON.toJSONString(ans));
                }
            }catch (Exception e){
                log.error(e.toString());
            }finally {
                lock.unlock();
                cleanUp();
                timer = 0;
            }
        },1,1,TimeUnit.SECONDS);
    }


    public void cleanUp(){
        //删除数据
        messageList.clear();
        statisticsInfo.clear();
        for (Map.Entry<String,SingleDevWatchInfo> entry:devWatchInfoMap.entrySet()){
            entry.getValue().cleanUp();
        }
        for (AP ap:apConfig.getApList()){
            statisticsInfo.put(ap.getMac(),new Info());
        }

    }

    @OnOpen
    public void onOpen(Session session){
        this.session = session;
        log.info("有新的WS连接。。。。");
    }

    @OnClose
    public void onClose() {
        log.info("【websocket消息】连接断开");
        stopStatistics();
    }

    @OnMessage
    public void onMessage(String message) {
        String[] params = message.split("#");
        if (params[0].equals("startStatistics")){
            //#t
            startStatistics(Integer.parseInt(params[1]));
            getStatisticsInfo();
        }else if (params[0].equals("stopStatistics")){
            stopStatistics();
        }else if (params[0].equals("addSingleDevWatch")){
            //#devMac
            String mac = DataParser.parseMac(params[1]);
            devWatchInfoMap.put(mac,new SingleDevWatchInfo(mac));
        }else if(params[0].equals("removeWatchMac")){
            devWatchInfoMap.remove(DataParser.parseMac(params[1]));
        }
    }

    // 此为单点消息 (发送文本)
    public void sendTextMessage(String message) {
        if (session != null) {
            try {
                session.getBasicRemote().sendText(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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



}
