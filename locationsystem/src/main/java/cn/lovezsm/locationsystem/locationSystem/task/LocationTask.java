package cn.lovezsm.locationsystem.locationSystem.task;


import cn.lovezsm.locationsystem.base.bean.FingerPrint;
import cn.lovezsm.locationsystem.base.bean.GridMap;
import cn.lovezsm.locationsystem.base.bean.LocationResult;
import cn.lovezsm.locationsystem.base.bean.Message;
import cn.lovezsm.locationsystem.base.config.APConfig;
import cn.lovezsm.locationsystem.base.config.LocationConfig;
import cn.lovezsm.locationsystem.base.config.MessageConfig;
import cn.lovezsm.locationsystem.base.util.DataParser;
import cn.lovezsm.locationsystem.base.util.SpringUtils;
import cn.lovezsm.locationsystem.base.data.DataCache;
import cn.lovezsm.locationsystem.base.data.LocationResultCache;
import cn.lovezsm.locationsystem.locationSystem.algorithm.LocationAlgorithm;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class LocationTask extends QuartzJobBean {

    @Autowired
    DataCache dataCache;

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        //1.读取配置参数
        LocationConfig locationConfig = SpringUtils.getBean(LocationConfig.class);
        APConfig apConfig = SpringUtils.getBean(APConfig.class);

        MessageConfig messageConfig = SpringUtils.getBean(MessageConfig.class);
        FingerPrint fingerPrint = SpringUtils.getBean(FingerPrint.class);
        GridMap gridMap = SpringUtils.getBean(GridMap.class);

        DataCache dataCache = SpringUtils.getBean(DataCache.class);

        //2.获得计算数据
        List<Message> data = dataCache.getAll();
        data = DataParser.messageFilter(messageConfig,apConfig, data);

        if (data.size()==0){
            return;
        }


        String locationAlgorithmName = locationConfig.getLocationAlgorithmName();
        LocationAlgorithm algorithm = null;
        try {
            try {
                algorithm = (LocationAlgorithm) LocationConfig.locationAlgorithmMap.get(locationAlgorithmName).getDeclaredConstructor().newInstance();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        //3.装填算法参数
        Map<String,Object> locationParams = new HashMap<>();
        locationParams.put("locationConfig",locationConfig);
        locationParams.put("fingerPrint",fingerPrint);
        locationParams.put("gridMap",gridMap);
        locationParams.put("apConfig",apConfig);
        locationParams.put("data",data);
        //4.执行算法
        List<LocationResult> locationResults = algorithm.locate(locationParams);
        //5.结果缓存在数据
        LocationResultCache resultCache = SpringUtils.getBean(LocationResultCache.class);
        for (LocationResult result:locationResults){
            resultCache.set(result.getDevMac(),result,locationConfig.getResultExpireQueueTime()*1000);
            System.out.println(result);
        }
    }
}
