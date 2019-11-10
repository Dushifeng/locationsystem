package cn.lovezsm.locationsystem.base.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AP {
    private String mac;
    private double x;
    private double y;
    private int index;
}
