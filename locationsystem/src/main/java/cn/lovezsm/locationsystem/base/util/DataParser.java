package cn.lovezsm.locationsystem.base.util;


import cn.lovezsm.locationsystem.base.bean.Message;
import cn.lovezsm.locationsystem.base.config.APConfig;
import cn.lovezsm.locationsystem.base.config.MessageConfig;

import java.util.ArrayList;
import java.util.List;

public class DataParser {
    public static List<Message> parseRawData(Long timeStamp, String rawData){
        if (rawData == null||rawData.length()<58){
            return null;
        }
        if(timeStamp == null){
            timeStamp = System.currentTimeMillis();
        }
        List<Message> messages = new ArrayList<>();

        String apMac = rawData.substring(46,58);
        int index = 58;
        while (index < rawData.length()-1){
            String tag = rawData.substring(index, index + 2);
            if (!tag.equals("00")) {
                int len = Integer.parseInt(rawData.substring(index + 2, index + 6), 16);
                index += (len * 2 + 6);
                System.out.println("出现了一条异常信息:"+tag);
                continue;
            }
            index += 6;
            String devMac = rawData.substring(index, index + 12);
            int frequency = Integer.parseInt(rawData.substring(index + 16, index + 18), 16);
            index += 32;
            int rssi = Integer.parseInt(rawData.substring(index, index + 2), 16) - 256;
            index += 2;
            Message message = new Message(devMac,frequency,rssi,apMac,timeStamp);
            messages.add(message);
        }

        return messages;
    }

    public static List<Message> messageFilter(MessageConfig messageConfig, APConfig apConfig, List<Message> messageList) {
        List<Message> ans = new ArrayList<>();
        for (Message message:messageList){
            String devMac = message.getDevMac();
            String apMac = message.getApMac();
            if (apConfig.getAPIndex(apMac)<0){
                continue;
            }
            if (messageConfig.getMacAllow().size()>0&&messageConfig.getMacAllow().contains(devMac)){
               ans.add(message);
               continue;
            }
            else if (messageConfig.getMacAllow().isEmpty()&&!messageConfig.getMacRefuse().contains(devMac)){
                ans.add(message);
            }
        }
        return ans;
    }



    public static List<Message> parseRawData(String rawData){
        return parseRawData(null,rawData);
    }

    public static String parseMac(String mac){
        String[] split = mac.split(":");
        StringBuffer buffer = new StringBuffer();
        for (String s:split){
            buffer.append(s.toLowerCase());
        }
        return buffer.toString();
    }

}
