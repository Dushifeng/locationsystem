package cn.lovezsm.locationsystem.base.data;


import cn.lovezsm.locationsystem.base.bean.Message;
import cn.lovezsm.locationsystem.base.util.DataParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileMessageDataBase {

    private Map<Long, List<Message>> messageMap;
    public FileMessageDataBase(File file) {
        Logger logger = LoggerFactory.getLogger(this.getClass());
        try {
            this.messageMap = new HashMap<>();

            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine())!=null){
                String[] split = line.split(":");
                Long timeStamp = Long.parseLong(split[0]);
                String rawData = split[1];
                if (rawData.length()<=58){
                    continue;
                }
                List<Message> messages = DataParser.parseRawData(timeStamp, rawData);
                if (!messageMap.containsKey(timeStamp)){
                    messageMap.put(timeStamp,new ArrayList<>());
                }
                messageMap.get(timeStamp).addAll(messages);
            }

        }catch (Exception e){
            e.printStackTrace();
            logger.error("构建失败");
        }

    }

    public Map<Long, List<Message>> getMessageMap() {
        return messageMap;
    }
}
