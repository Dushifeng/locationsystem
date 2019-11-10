package cn.lovezsm.locationsystem.base.bean;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Message {
    private String devMac;
    private int frequency;
    private int rssi;
    private String apMac;
    private Long timestamp;

}
