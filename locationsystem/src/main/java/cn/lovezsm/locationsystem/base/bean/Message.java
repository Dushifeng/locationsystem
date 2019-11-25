package cn.lovezsm.locationsystem.base.bean;

import lombok.AllArgsConstructor;
import lombok.Data;


/**
 * message 不可改，只读
 */
@Data
@AllArgsConstructor
public class Message implements Cloneable{
    private final String devMac;
    private final int frequency;
    private final int rssi;
    private final String apMac;
    private final Long timestamp;
}
