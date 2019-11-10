package cn.lovezsm.locationsystem.base.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
public class FingerPrint{
    private String name;
    private double[][] avg;
    private double[][] std;
}
