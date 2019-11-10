package cn.lovezsm.locationsystem.base.bean;

import cn.lovezsm.locationsystem.base.util.FileUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.*;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
public class GridMap{
    private String name;
    private double[] width;
    private double[] height;
    private double[] x;
    private double[] y;
    private int gridNum;

    public static GridMap buildByFile(File gridFile, String name) throws Exception{
        GridMap gridMap = null;
        Logger logger = LoggerFactory.getLogger(GridMap.class);

        BufferedReader reader = new BufferedReader(new FileReader(gridFile));

        int gridNum = FileUtils.getTotalLines(gridFile);

        double[] posW = new double[gridNum];
        double[] posH = new double[gridNum];
        double[] posX = new double[gridNum];
        double[] posY = new double[gridNum];

        for(int i =0;i<gridNum;i++){
            String line = reader.readLine();
            if(line == null) {
                continue;
            }
            String[] split = line.trim().split("_");
            if(split.length<5){
                logger.error("文件数据有误，行参数不足5个");
                return null;
            }
            int index = Integer.parseInt(split[0])-1;
            posX[index] = Double.parseDouble(split[1]);
            posY[index] = Double.parseDouble(split[2]);
            posW[index] = Double.parseDouble(split[4]);
            posH[index] = Double.parseDouble(split[3]);
        }
        gridMap = new GridMap(name,posW,posH,posX,posY,gridNum);
        return gridMap;
    }
    public int getPointIndexByXY(double x1,double y1){
        for (int i = 0; i < getGridNum(); i++) {
            if (x1==x[i]&&y1==y[i]){
                return i+1;
            }
        }
        return -1;
    }

}
