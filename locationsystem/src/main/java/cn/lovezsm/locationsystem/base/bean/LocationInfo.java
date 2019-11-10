package cn.lovezsm.locationsystem.base.bean;

import java.util.HashMap;
import java.util.Map;

public class LocationInfo {
    private Map<String, LocationResult> locationResultMap = new HashMap<>();
    private DeviceDensity deviceDensity;
    public Map<String, LocationResult> getLocationResultMap() {
        return locationResultMap;
    }

    public void setLocationResultMap(Map<String, LocationResult> locationResultMap) {
        this.locationResultMap = locationResultMap;
    }

    public void setDeviceDensity(DeviceDensity deviceDensity) {
        this.deviceDensity = deviceDensity;
    }

    public DeviceDensity getDeviceDensity() {
        return deviceDensity;
    }
}

