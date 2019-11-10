package cn.lovezsm.locationsystem.collectionSystem.bean;

import cn.lovezsm.locationsystem.base.web.bean.Dev;
import cn.lovezsm.locationsystem.collectionSystem.config.CollectionConfig;
import lombok.Data;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Data
public class NewTask {
    private int id;
    private File inFile;
    private File outFile;
    private Long startTime;
    private Long stopTime;

    private Map<String, Dev> devMap = new HashMap<>();
    private CollectionConfig config;

    public NewTask() {
        long timeStamp = Instant.now().toEpochMilli();
        inFile = new File("in_fingerprintdata"+timeStamp+".dat");
        outFile = new File("out_fingerprintdata"+timeStamp+".dat");
        try {
            inFile.createNewFile();
            outFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
