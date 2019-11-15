package cn.lovezsm.locationsystem.locationSystem.service;

import cn.lovezsm.locationsystem.base.bean.AP;
import cn.lovezsm.locationsystem.base.bean.FingerPrint;
import cn.lovezsm.locationsystem.base.bean.FingerPrintBuilder;
import cn.lovezsm.locationsystem.base.bean.GridMap;
import cn.lovezsm.locationsystem.base.config.APConfig;
import cn.lovezsm.locationsystem.base.config.LocationConfig;
import cn.lovezsm.locationsystem.base.config.MessageConfig;
import cn.lovezsm.locationsystem.base.service.DataDirectCenter;
import cn.lovezsm.locationsystem.base.service.LocationPorter;
import cn.lovezsm.locationsystem.base.util.BaseUtils;
import cn.lovezsm.locationsystem.base.util.SpringUtils;
import cn.lovezsm.locationsystem.locationSystem.task.LocationTask;
import cn.lovezsm.locationsystem.udpServer.server.UDPServer;
import cn.lovezsm.locationsystem.udpServer.server.netty.NettyUDPServer;
import org.quartz.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.*;

import static org.quartz.DateBuilder.futureDate;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

@Service
public class LocationSystemService {

    @Autowired
    @Qualifier("Scheduler")
    private Scheduler scheduler;

    @Autowired
    private LocationPorter porter;

    public void startUDPServer(int port){
        UDPServer server = NettyUDPServer.getINSTANCE();
        server.start();
    }
    /**
     * 格式devMac-0（白名单）或者devMac-1(黑名单)
     * @param inputStream
     * @return
     */
    public MessageConfig getMessageConfigByFile(InputStream inputStream){
        MessageConfig messageConfig = new MessageConfig();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        try {
            while ((line = reader.readLine())!=null){
                String[] split = line.split("-");
                String mac = BaseUtils.transforMac(split[0].trim());
                if ("0".equals(split[1].trim())){
                    messageConfig.getMacAllow().add(mac);
                }else if ("1".equals(split[1].trim())){
                    messageConfig.getMacRefuse().add(mac);
                }else if ("f".equals(split[1])){

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return messageConfig;
    }
    /**
     * 格式index-mac-x-y
     * @param inputStream
     * @return
     */
    public APConfig getAPConfigByFile(InputStream inputStream){
        APConfig apConfig = new APConfig();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        try {
            while ((line = reader.readLine())!=null){
                String[] split = line.split("-");
                int index = Integer.parseInt(split[0].trim());
                String mac = split[1];
                double x = Double.parseDouble(split[2].trim());
                double y = Double.parseDouble(split[3].trim());
                apConfig.getApList().add(new AP(mac,x,y,index));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return apConfig;
    }


    //设置参数{apconfig,locationConfig,messageConfig,fingerprint,gridmap,算法}
    public void setConfig(APConfig config){
        APConfig apConfig = SpringUtils.getBean(APConfig.class);
        BeanUtils.copyProperties(config,apConfig);
    }

    public void setConfig(MessageConfig newMessageConfig){
        MessageConfig config = SpringUtils.getBean(MessageConfig.class);
        BeanUtils.copyProperties(newMessageConfig,config);
    }

    public void setConfig(LocationConfig newConfig){
        LocationConfig config = SpringUtils.getBean(LocationConfig.class);
        BeanUtils.copyProperties(newConfig,config);
    }

    public void setConfig(File gridMapFile,String name){
        GridMap gridMap = SpringUtils.getBean(GridMap.class);
        GridMap g = null;
        try {
            g = GridMap.buildByFile(gridMapFile,name);
        } catch (Exception e) {
            e.printStackTrace();
        }
        BeanUtils.copyProperties(g,gridMap);
    }

    public void setConfig(File avgFile,File stdFile,String name){
        FingerPrint fingerPrint = SpringUtils.getBean(FingerPrint.class);
        FingerPrint newfingerPrint = FingerPrintBuilder.build(name,avgFile,stdFile);
        BeanUtils.copyProperties(newfingerPrint,fingerPrint);
    }

    public boolean startLocation(){

        DataDirectCenter.register(porter);

        LocationConfig config = SpringUtils.getBean(LocationConfig.class);

        try {
            Trigger triggerProcessRawDataTask = newTrigger()
                    .withIdentity("LocationTask")
                    .startAt(futureDate(1, DateBuilder.IntervalUnit.SECOND))
                    .withSchedule(simpleSchedule()
                            .withIntervalInSeconds(config.getSlidingStep())
                            .repeatForever())
                    .build();
            scheduler.scheduleJob(newJob(LocationTask.class).withIdentity("LocationTask").build(), triggerProcessRawDataTask);
        }catch (Exception e){
            e.printStackTrace();
        }
        return true;
    }

    public void stopLocation(){
        DataDirectCenter.unregister(porter.name);
        try {
            scheduler.deleteJob(new JobKey("LocationTask"));
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }


}
