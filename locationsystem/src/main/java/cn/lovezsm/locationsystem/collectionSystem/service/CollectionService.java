package cn.lovezsm.locationsystem.collectionSystem.service;


import cn.lovezsm.locationsystem.base.bean.GridMap;
import cn.lovezsm.locationsystem.base.config.APConfig;
import cn.lovezsm.locationsystem.base.data.CollectionInfoCache;
import cn.lovezsm.locationsystem.base.data.CollectionResult;
import cn.lovezsm.locationsystem.base.service.CollectionPorter;
import cn.lovezsm.locationsystem.base.service.DataDirectCenter;
import cn.lovezsm.locationsystem.base.util.BaseUtils;
import cn.lovezsm.locationsystem.base.util.FileUtils;
import cn.lovezsm.locationsystem.base.web.bean.Dev;
import cn.lovezsm.locationsystem.base.web.bean.Result;
import cn.lovezsm.locationsystem.collectionSystem.bean.NewTask;
import cn.lovezsm.locationsystem.collectionSystem.bean.RegisterBean;
import cn.lovezsm.locationsystem.collectionSystem.config.CollectionConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.OutputStream;
import java.time.Instant;
import java.util.*;


@Service
public class CollectionService {

    private List<NewTask> taskList = new ArrayList<>();
    NewTask curNewTask;
    @Autowired
    CollectionInfoCache infoMap;
    @Autowired
    CollectionPorter collectionPorter;

    public NewTask getCurNewTask() {
        return curNewTask;
    }

    public boolean hasTask(){
        return curNewTask!=null;
    }
    public Map<String,Dev> getDevMap(){
        if (curNewTask == null){
            return null;
        }
        return curNewTask.getDevMap();
    }
    public Result genNewTask(List<Dev> devs, File gridmapFile1, File apListFile1, int innerNum){
        Result result = new Result();
        //检验参数
        Map<String, Dev> map = new HashMap<>();
        for (Dev dev:devs) {
            String mac = BaseUtils.transforMac(dev.getMac());
            if (mac==null){
                result.setStatus(100);
                result.setMessage("输入的Mac格式错误，请检查后重新提交");
                return result;
            }else {
                map.put(mac,dev);
            }
        }

        APConfig apConfig = null;
        try {
            apConfig = APConfig.buildByFile(apListFile1);
        } catch (Exception e) {
            e.printStackTrace();
            result.setMessage("100");
            result.setMessage("AP文件格式内容错误，请检查后重新提交");
        }

        GridMap gridMap = null;
        try {
            gridMap = GridMap.buildByFile(gridmapFile1, "");
        } catch (Exception e) {
            e.printStackTrace();
            result.setMessage("100");
            result.setMessage("晶格文件格式内容错误，请检查后重新提交");
        }
        //new NewTask
        NewTask task = new NewTask();
        task.setDevMap(map);
        CollectionConfig config = new CollectionConfig();
        config.setGridMap(gridMap);
        config.setApConfig(apConfig);
        config.setInZoomNum(innerNum);
        task.setConfig(config);
        task.setStartTime(Instant.now().getEpochSecond());
        if (curNewTask != null){
            result.setStatus(100);
            result.setMessage("尚有在进行的任务，无法新提交任务");
        }else {
            curNewTask = task;
            result.setStatus(200);
            result.setMessage("提交成功");
            System.out.println(curNewTask);
            infoMap.startCollection(curNewTask);
            DataDirectCenter.register(collectionPorter);
        }
        return result;
    }
    public void stopTask(OutputStream outputStream){
        Result result = new Result();
        DataDirectCenter.unregister(collectionPorter.name);
        infoMap.stopCollection();
        //获取in 和 out文件，压缩包返回前端下载
        File inFile = curNewTask.getInFile();
        File outFile = curNewTask.getOutFile();

        FileUtils.toZip(Arrays.asList(inFile,outFile),outputStream);

        inFile.delete();
        outFile.delete();
        curNewTask = null;
    }

    public List<Dev> getDevList() {
        List<Dev> ans = new ArrayList<>();

        if (curNewTask == null){
            return ans;
        }

        Map<String, Dev> devMap = curNewTask.getDevMap();
        ans.addAll(devMap.values());

        return ans;
    }

    public Result getGridMapXY(int index) {
        Result result = new Result();
        if (curNewTask == null){
            result.setStatus(100);
            result.setMessage("当前无任务");
            return result;
        }
        CollectionConfig config = curNewTask.getConfig();
        GridMap gridMap = config.getGridMap();
        if (index<1||index>gridMap.getGridNum()){
            result.setStatus(100);
            result.setMessage("index不在合理范围");
            return result;
        }

        result.setStatus(200);
        Map<String,Double> ans = new HashMap<>();
        ans.put("x",gridMap.getX()[index-1]);
        ans.put("y",gridMap.getY()[index-1]);
        result.setRefObject(ans);
        return result;
    }

    public Result registerTask(String uuid,String mac, double x, double y) {
        Result result = new Result();
        mac = BaseUtils.transforMac(mac);
        GridMap gridMap = curNewTask.getConfig().getGridMap();
        int pointIndex = gridMap.getPointIndexByXY(x, y);
        if (mac == null){
            result.setStatus(100);
            result.setMessage("mac地址错误");
            return result;
        }else if (pointIndex==-1){
            result.setStatus(100);
            result.setMessage("x，y坐标错误");
            return result;
        }
        if(!infoMap.register(new RegisterBean(mac,uuid,pointIndex,x,y,Instant.now().getEpochSecond()))){
            result.setStatus(100);
            result.setMessage("该设备Mac仍在采集中，无法添加新任务。");
            return result;
        }
        result.setStatus(200);
        return result;
    }

    public CollectionResult completeSingle(String mac) {
        RegisterBean bean = infoMap.getByMac(mac);
        CollectionResult result = bean.getResult();
        infoMap.stopSingleTask(mac);
        return result;
    }

    public RegisterBean searchLoginInfo(String uuid) {
        return infoMap.getByUUID(uuid);
    }

    public List<RegisterBean> getDoingTask(){
        List<RegisterBean> registerBeanList = infoMap.getRegisterBeanList();
        List<RegisterBean> ans = new ArrayList<>();

        for (RegisterBean bean:registerBeanList
             ) {
            if (bean.isDoing()){
                ans.add(bean);
            }
        }
        return ans;
    }

    public List<RegisterBean> getDoneTask(){
        return infoMap.getFinishedTaskList();
    }
}
