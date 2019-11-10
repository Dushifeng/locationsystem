package cn.lovezsm.locationsystem.base.config;

import cn.lovezsm.locationsystem.base.bean.AP;
import cn.lovezsm.locationsystem.base.util.DataParser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

@Data
@Component
@AllArgsConstructor
@NoArgsConstructor
public class APConfig{

    private Set<AP> apList = new HashSet<>();

    public int getAPSize(){
        return apList.size();
    }
    /**
     * @param apMac
     * @return 找到后返回ap的序号，否则返回-1
     */
    public int getAPIndex(String apMac){
        apMac = DataParser.parseMac(apMac);
        for (AP ap:apList){
            if(ap.getMac().equals(apMac)){
                return ap.getIndex();
            }
        }
        return -1;
    }

    public static APConfig buildByFile(File file) throws Exception {
        APConfig apConfig = new APConfig();
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line = "";

        while ((line = reader.readLine())!=null){
            String[] split = line.split("-");
            int index = Integer.parseInt(split[0].trim());
            String mac = split[1];
            double x = Double.parseDouble(split[2].trim());
            double y = Double.parseDouble(split[3].trim());
            apConfig.getApList().add(new AP(mac,x,y,index));
        }

        return apConfig;
    }

}
