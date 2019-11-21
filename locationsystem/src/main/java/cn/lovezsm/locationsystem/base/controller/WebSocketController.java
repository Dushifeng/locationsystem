package cn.lovezsm.locationsystem.base.controller;


import cn.lovezsm.locationsystem.base.service.StatisticsService;
import cn.lovezsm.locationsystem.base.util.DataParser;
import cn.lovezsm.locationsystem.base.web.bean.SingleDevWatchInfo;

import com.alibaba.fastjson.JSON;
import jdk.nashorn.internal.parser.JSONParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import java.util.HashMap;

import java.util.Map;
import java.util.concurrent.*;

@Component
@ServerEndpoint("/ws/statistics")
@Slf4j
public class WebSocketController {

    private Session session;

    private static StatisticsService statisticsService;

    @Autowired
    public  void setStatisticsService(StatisticsService statisticsService) {
        WebSocketController.statisticsService = statisticsService;
    }

    ScheduledFuture infoTask = null;

    volatile ConcurrentHashMap<String,String> devMacs = new ConcurrentHashMap<>();

    private static ScheduledExecutorService poolExecutor = new ScheduledThreadPoolExecutor(1);


    public void startStatistics(int second){
        statisticsService.start(second);
    }


    public void stopStatistics(){

        if (infoTask!=null||(infoTask!=null&&!infoTask.isCancelled())){
            infoTask.cancel(true);
            infoTask = null;
        }
        statisticsService.stop();
    }

    public void getStatisticsInfo(){
        log.info("getStatisticsInfo----1");
        if (infoTask!=null||(infoTask!=null&&!infoTask.isCancelled())){
            infoTask.cancel(false);
            infoTask = null;
        }
        log.info("getStatisticsInfo----2");
        infoTask =poolExecutor.scheduleAtFixedRate(()->{
                Map<String, StatisticsService.Info> data = statisticsService.getData();
                Map<String,Object> ans = new HashMap<>();
                ans.put("all",data);

                log.info("data:---"+data);

                for (Map.Entry<String,String> entry:devMacs.entrySet()){
                    String mac = entry.getKey();
                    mac = DataParser.parseMac(mac);
                    if (mac==null){
                        continue;
                    }
                    SingleDevWatchInfo singleDevWatchInfo = statisticsService.getSingleDevWatchInfo(mac);
                    System.out.println(singleDevWatchInfo);

                    if (singleDevWatchInfo!=null){
                        ans.put(mac,singleDevWatchInfo);
                    }
                }
                sendTextMessage(JSON.toJSONString(ans));
        },1,1,TimeUnit.SECONDS);
    }


    @OnOpen
    public void onOpen(Session session){
        this.session = session;
        System.out.println(statisticsService);
        log.info("有新的WS连接。。。。");
    }

    @OnClose
    public void onClose() {
        log.info("【websocket消息】连接断开");
        stopStatistics();
    }

    @OnMessage
    public void onMessage(String message) {
        log.info("【websocket消息】收到客户端消息:"+message);

        String[] params = message.split("#");
        if (params[0].equals("startStatistics")){
            //#t
            startStatistics(Integer.parseInt(params[1]));
            getStatisticsInfo();
        }else if (params[0].equals("stopStatistics")){
            stopStatistics();
        }else if (params[0].equals("addSingleDevWatch")){
            //#devMac
            devMacs.put(params[1],"");
        }else if(params[0].equals("removeWatchMac")){
            devMacs.remove(params[1]);
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

    // 此为单点消息 (发送对象)
    public void sendObjMessage(Object message) {
        if (session != null) {
            try {
                session.getAsyncRemote().sendObject(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
