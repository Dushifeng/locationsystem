package cn.lovezsm.locationsystem.locationSystem.algorithm;


import cn.lovezsm.locationsystem.base.bean.*;
import cn.lovezsm.locationsystem.base.config.APConfig;
import cn.lovezsm.locationsystem.base.config.LocationConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 如果算法需要对2.4G 5.8G信段进行分开处理，并组合成rssi，如[2.4G，5.8GL，5.8GH],可以重写父类（LocationAlgorithm）的dataPreprocessing方法，定制化
 */

/**
 * public List<LocationResult> locate(Map<String, Object> params)
 * public abstract LocationResult locateSingleDev(double[] rssi, FingerPrint fingerPrint, GridMap gridMap, Map<String,Object> otherParams);
 *
 * 根据算法流程来重写这两个函数，其中locate给出了默认实现。
 */
public abstract class LocationAlgorithm {
    public int version = 1;
    public String algorithmName = "";
    public static double MIN_RSSI_VAL = -100;
    /**
     *
     * @param params 必要的参数包括 定位数据--data,WKNN方法的k值--k,指纹库--fingerPrint,晶格信息--gridMap
     * @return
     */
    public List<LocationResult> locate(Map<String, Object> params) {
        List<LocationResult> ans = new ArrayList<>();
        //读参数
        List<Message> messages = (List<Message>) params.get("data");
        LocationConfig locationConfig = (LocationConfig) params.get("locationConfig");
        FingerPrint fingerPrint = (FingerPrint) params.get("fingerPrint");
        GridMap gridMap = (GridMap) params.get("gridMap");
        APConfig apConfig = (APConfig) params.get("apConfig");
        //数据预处理  {devMac,rssi}
        Map<String, RSSi> rssiMap = dataPreprocessing(messages, apConfig);
        //执行定位算法
        for (Map.Entry<String,RSSi> entry:rssiMap.entrySet()){
            //判断rssi非Null个数
            String devMac = entry.getKey();
            RSSi rssi = entry.getValue();
            double[] rssiVal = rssi.getRssi();
            int notNullNum = 0;
            for (int i=0;i<rssiVal.length;i++){
                if (rssiVal[i] > MIN_RSSI_VAL){
                    notNullNum++;
                }
            }
            if (notNullNum<locationConfig.getMinNotNullRssNum()){
                continue;
            }
            LocationResult result = locateSingleDev(rssiVal, fingerPrint, gridMap, locationConfig);
            result.setGridMapNum(gridMap.getGridNum());
            result.setDevMac(devMac);
            ans.add(result);
        }

        return ans;
    }

    public abstract LocationResult locateSingleDev(double[] rssi, FingerPrint fingerPrint, GridMap gridMap, LocationConfig locationConfig);

    public Map<String, RSSi> dataPreprocessing(List<Message> messages, APConfig apConfig) {
        Map<String, RSSi> ans = new HashMap<>();
        for (Message message : messages) {
            if (!ans.containsKey(message.getDevMac())) {
                ans.put(message.getDevMac(), new RSSi(apConfig.getAPSize()));
            }
            ans.get(message.getDevMac()).put(apConfig.getAPIndex(message.getApMac()), message.getRssi());
        }
        return ans;
    }

    public static class RSSi {
        double[] rssi;
        int apNum;
        int[] num;

        public void setRssi(double[] rssi) {
            this.rssi = rssi;
        }

        public double[] getRssi() {
            for (int i = 0; i < apNum; i++) {
                if (num[i] > 0) {
                    rssi[i] = rssi[i] / num[i];
                    num[i] = 1;
                }
            }
            return rssi;
        }

        public RSSi(int apNum) {
            rssi = new double[apNum];
            this.apNum = apNum;
            num = new int[apNum];

            for (int i = 0; i < apNum; i++) {
                rssi[i] = MIN_RSSI_VAL;
            }
        }

        public void put(int index, double val) {

            if (rssi[index] == MIN_RSSI_VAL && num[index] == 0) {
                rssi[index] = 0;
            }

            rssi[index] += val;
            num[index]++;
        }
    }


    public static DeviceDensity calculateDeviceDensity(List<LocationResult> records){
        if (records.size()==0){
            return null;
        }

        int size = records.get(0).getGridMapNum();
        double[] deviceDensity = new double[size];
        double[] newDensity = new double[size];
        for(LocationResult lr:records){
            if(lr == null){
                continue;
            }
            List<Integer> idxCandidate = lr.getIdxCandidate();
            List<Double> probCandidate = lr.getProbCandidate();
            for(int i=0;i<idxCandidate.size();i++){
                int idx = idxCandidate.get(i);
                deviceDensity[idx] = deviceDensity[idx]+probCandidate.get(i);
            }
        }
        DeviceDensity dd = new DeviceDensity();
        dd.setDeviceDensity(deviceDensity);
        dd.setNewDensity(newDensity);
        return dd;
    }
}
