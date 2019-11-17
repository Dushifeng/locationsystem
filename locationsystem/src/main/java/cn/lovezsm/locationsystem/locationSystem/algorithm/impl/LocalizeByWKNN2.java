package cn.lovezsm.locationsystem.locationSystem.algorithm.impl;


import cn.lovezsm.locationsystem.base.bean.FingerPrint;
import cn.lovezsm.locationsystem.base.bean.GridMap;
import cn.lovezsm.locationsystem.base.bean.LocationResult;
import cn.lovezsm.locationsystem.base.bean.Pos;
import cn.lovezsm.locationsystem.base.config.LocationConfig;
import cn.lovezsm.locationsystem.base.util.AlgorithmUtils;
import cn.lovezsm.locationsystem.locationSystem.algorithm.LocationAlgorithm;
import org.springframework.stereotype.Component;
@Component("localizeByWKNN2")
public class LocalizeByWKNN2 extends LocationAlgorithm {

    public LocalizeByWKNN2() {
        algorithmName="localizeByWKNN2";
        version = 1;
    }


    @Override
    public LocationResult locateSingleDev(double[] rssi, FingerPrint fingerPrint, GridMap gridMap, LocationConfig locationConfig) {
        LocationResult result = new LocationResult();
        int gridNum = gridMap.getGridNum();
        int apNum = rssi.length;
        double[][] avg = fingerPrint.getAvg();
        double[][] std = fingerPrint.getStd();

        double[] fFusion = AlgorithmUtils.getMatrix(1,gridNum,1)[0];
//        double[] offset = AlgorithmUtils.getMatrix(1, gridNum, 0)[0];
        for(int i=0;i<gridNum;i++){
            double a = 0;
            double b = 0;
            double c = 0;
//            double[] fFusionTemp = AlgorithmUtils.getMatrix(1, apNum, 0)[0];
            for (int j = 0;j<apNum;j++){
                if (rssi[j] == MIN_RSSI_VAL){
                    continue;
                }
                a += 1/2/Math.pow(std[i][j],2);
                b += (avg[i][j] - rssi[j])/Math.pow(std[i][j],2);
                c += Math.pow(avg[i][j]-rssi[j],2)/2/Math.pow(std[i][j],2)+Math.log(std[i][j]);
            }
            fFusion[i] = Math.exp(-(4*a*c-Math.pow(b,2))/a/4);
//            offset[i] = -b/2/a;
        }

        //根据似然值排序
        int[] sortedIndex = AlgorithmUtils.getSortedIndex(fFusion);
        //除了似然值最大的k个以外，其余均置为0
        for(int i=locationConfig.getK();i<sortedIndex.length;i++){
            fFusion[sortedIndex[i]] = 0;
        }
        //根据晶格面积修正似然值
        for(int i = 0;i<gridNum;i++){
            fFusion[i]=fFusion[i]*gridMap.getHeight()[i]*gridMap.getWidth()[i];
        }
        //似然值归一化
        AlgorithmUtils.normalized(fFusion);
        //使用WKNN算法，计算位置估计
        double x = AlgorithmUtils.multiplication(gridMap.getX(),fFusion);
        double y = AlgorithmUtils.multiplication(gridMap.getY(),fFusion);

        result.setLocationTime(System.currentTimeMillis());
        result.setPos(new Pos(x,y));
        return result;
    }

}
