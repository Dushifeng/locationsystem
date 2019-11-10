package cn.lovezsm.locationsystem.locationSystem;

import cn.lovezsm.locationsystem.base.bean.*;
import cn.lovezsm.locationsystem.base.config.APConfig;
import cn.lovezsm.locationsystem.base.config.LocationConfig;
import cn.lovezsm.locationsystem.base.config.MessageConfig;
import cn.lovezsm.locationsystem.base.data.LocationResultCache;
import cn.lovezsm.locationsystem.base.util.SpringUtils;
import cn.lovezsm.locationsystem.base.util.WebUtils;
import cn.lovezsm.locationsystem.locationSystem.algorithm.LocationAlgorithm;
import cn.lovezsm.locationsystem.locationSystem.service.LocationSystemService;
import cn.lovezsm.locationsystem.udpServer.server.netty.NettyUDPServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("api/locationSys")
public class LocationSystemController {
    @Autowired
    LocationSystemService service;
    @Autowired
    LocationResultCache locationResultCache;
    @GetMapping("test")
    public String test(){
        return "hello";
    }

    @GetMapping("getGridMapData")
    public GridMap getGridMapData(){
        return SpringUtils.getBean(GridMap.class);
    }
    @GetMapping("startLocation")
    public boolean startLocation(){
        return service.startLocation();
    }
    @GetMapping("stopLocation")
    public void stopLocation(){
        service.stopLocation();
    }
    @GetMapping("getLocationInfo")
    public LocationInfo getLocationInfo(){
        LocationInfo locationInfo = new LocationInfo();
        Map<String, Object> all = locationResultCache.getAll();
        List<LocationResult> results = new ArrayList<>();
        for (Map.Entry<String,Object> entry:all.entrySet()){
            LocationResult value = (LocationResult) entry.getValue();
            locationInfo.getLocationResultMap().put(entry.getKey(),value);
            results.add(value);
        }
        DeviceDensity deviceDensity = LocationAlgorithm.calculateDeviceDensity(results);
        locationInfo.setDeviceDensity(deviceDensity);
        return locationInfo;
}
    @GetMapping("getLocationConfig")
    public Map getLocationConfig(){
        LocationConfig locationConfig = SpringUtils.getBean(LocationConfig.class);
        Map<String,Object> ans = new HashMap<>();
        ans.put("config",locationConfig);
        List<String> nameList = new ArrayList<>();
        for (Map.Entry<String,Class> entry: LocationConfig.locationAlgorithmMap.entrySet()){
            nameList.add(entry.getKey());
        }
        ans.put("nameList",nameList);
        return ans;
    }
    @PostMapping("submitLocationConfig")
    public String submitLocationConfig(@RequestBody LocationConfig locationConfig){
        service.setConfig(locationConfig);
        return "SUCCESS";
    }
    @PostMapping("changeMessageConfigByFile")
    public MessageConfig changeMessageConfigByFile(@RequestBody MultipartFile file) throws IOException {
        MessageConfig config = service.getMessageConfigByFile(file.getInputStream());
        return config;
    }
    @GetMapping("getMessageConfig")
    public MessageConfig getMessageConfig(){
        MessageConfig messageConfig = SpringUtils.getBean(MessageConfig.class);
        return messageConfig;
    }
    @PostMapping("changeAPConfig")
    public String changeAPConfig(@RequestBody Set<AP> apList){
        service.setConfig(new APConfig(apList));
        return "SUCCESS";
    }
    @PostMapping("changeAPConfigByFile")
    public APConfig changeAPConfigByFile(@RequestBody MultipartFile file) throws IOException {
        try {
            APConfig config = service.getAPConfigByFile(file.getInputStream());
            return config;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
    @GetMapping("getAPConfig")
    public APConfig getAPConfig(){
        APConfig apConfig = SpringUtils.getBean(APConfig.class);
        return apConfig;
    }
    @PostMapping("changeMessageConfig")
    public String changeMessageConfig(@RequestBody MessageConfig messageConfig){
        service.setConfig(messageConfig);
        return "SUCCESS";
    }
    @PostMapping("a")
    public String changeUDPServerPort(int port){
        service.startUDPServer(port);
        return "SUCCESS";
    }
    @PostMapping("upLoadFingerprintFile")
    public String upLoadFingerprintFile(@RequestParam("avg") MultipartFile avgFile,@RequestParam("std") MultipartFile stdFile, String name){
        try {
            File avg = WebUtils.transMultipartFile(avgFile);
            File std = WebUtils.transMultipartFile(stdFile);
            service.setConfig(avg,std,name);
            WebUtils.deleteFile(avg,std);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return name;
    }
    @PostMapping("submitUDPState")
    public String submitUDPState(@RequestParam("open") boolean isOpen,@RequestParam("port") int port){
        NettyUDPServer server = NettyUDPServer.getINSTANCE();
        if (server.port == port){
            if (isOpen){
                server.start();
            }else {
                server.stop();
            }
        }else if (server.isOpen()&&!isOpen){
            server.stop();
            server.port = port;
        }else if (server.isOpen()&&isOpen){
            server.stop();
            server.port = port;
            server.start();
        }else if (!server.isOpen()&&!isOpen){
            server.port = port;
        }else{
            server.port = port;
            server.start();
        }
        return "SUCCESS";
    }
    @GetMapping("getUDPState")
    public Map<String,Object> getUDPState(){
        Map<String,Object> ans = new HashMap<>();
        NettyUDPServer udpServer = NettyUDPServer.getINSTANCE();
        ans.put("switch",udpServer.isOpen());
        ans.put("port",udpServer.port);
        return ans;
    }
    @GetMapping("getFingerprintConfig")
    public String getFingerprintConfig(){
        FingerPrint fingerPrint = SpringUtils.getBean(FingerPrint.class);
        return fingerPrint.getName();
    }
    @GetMapping("getGridMapConfig")
    public String getGridMapConfig(){
        GridMap gridMap = SpringUtils.getBean(GridMap.class);
        return gridMap.getName();
    }
    @PostMapping("upLoadGridMap")
    public String upLoadGridMap(@RequestParam("file") MultipartFile file, @RequestParam("name") String name){
        try {
            File gridMap = WebUtils.transMultipartFile(file);
            service.setConfig(gridMap,name);
            WebUtils.deleteFile(gridMap);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return name;
    }
}
