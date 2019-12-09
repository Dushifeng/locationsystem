package cn.lovezsm.locationsystem.base.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;


/**
 * message 不可改，只读
 */
@Data
@TableName("message")
@NoArgsConstructor
public class Message implements Cloneable, Serializable {
    @TableId(value = "id",type = IdType.AUTO)
    private int id;

    @TableField("dev_mac")
    private String devMac;

    @TableField("frequency")
    private int frequency;

    @TableField("rssi")
    private int rssi;

    @TableField("ap_mac")
    private String apMac;

    @TableField("timestamp")
    private Date timestamp;


    public Message(String devMac, int frequency, int rssi, String apMac, Date timestamp) {
        this.devMac = devMac;
        this.frequency = frequency;
        this.rssi = rssi;
        this.apMac = apMac;
        this.timestamp = timestamp;
    }
}
