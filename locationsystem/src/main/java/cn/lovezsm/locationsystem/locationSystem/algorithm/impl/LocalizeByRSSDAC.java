package cn.lovezsm.locationsystem.locationSystem.algorithm.impl;


import cn.lovezsm.locationsystem.base.bean.FingerPrint;
import cn.lovezsm.locationsystem.base.bean.GridMap;
import cn.lovezsm.locationsystem.base.bean.LocationResult;
import cn.lovezsm.locationsystem.base.bean.Pos;
import cn.lovezsm.locationsystem.base.config.LocationConfig;
import cn.lovezsm.locationsystem.base.util.AlgorithmUtils;
import cn.lovezsm.locationsystem.locationSystem.algorithm.LocationAlgorithm;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.Arrays;


@Component("localizeByRSSDAC")
public class LocalizeByRSSDAC extends LocationAlgorithm {

    public LocalizeByRSSDAC() {
        algorithmName = "全排列定位算法";
        version = 1;
    }


    @Override
    public LocationResult locateSingleDev(double[] rssi, FingerPrint fingerPrint, GridMap gridMap, LocationConfig locationConfig) {
        LocationResult result = new LocationResult();

        int gridNum = gridMap.getGridNum();
        int apNum = rssi.length;
        double[] fFusion = AlgorithmUtils.getMatrix(1,gridNum,1)[0];

        double[][] avg = fingerPrint.getAvg();
        double[][] std = fingerPrint.getStd();

        for (int i=0;i<apNum-1;i++){
            for (int j=i+1;j<apNum;j++){
                double[] fFusionTemp = AlgorithmUtils.getMatrix(1,gridNum,0)[0];
                for (int n = 0;n<gridNum;n++){
                    if (rssi[i]==MIN_RSSI_VAL||rssi[j]==MIN_RSSI_VAL){
                        continue;
                    }
                    //fFusionTemp[ng] = (1/(Math.sqrt(2*Math.PI)*fingerPrint.getStd()[ng][i]))*Math.exp((rssi[i]-fingerPrint.getAvg()[ng][i])*(rssi[i]-fingerPrint.getAvg()[ng][i])/(-2*fingerPrint.getStd()[ng][i]*fingerPrint.getStd()[ng][i]));
                    ////累乘概率值，得到该设备在每一个晶格的似然值
                    //fFusion[ng] = fFusion[ng]*fFusionTemp[ng];
                    double t1 = std[n][i]+std[n][j];
                    double t2 = avg[n][i]-avg[n][j];
                    double t3 = rssi[i]-rssi[j];
                    fFusionTemp[n] = (1/(Math.sqrt(2*Math.PI)*t1))*Math.exp(Math.pow(t3-t2,2)/(-2*Math.pow(t1,2)));
                    fFusion[n] = fFusion[n]*fFusionTemp[n];
                }
            }
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

        int idxCandidate[] = Arrays.copyOf(sortedIndex,locationConfig.getK());
        double[] probCandidate = new double[locationConfig.getK()];
        for(int i=0;i<idxCandidate.length;i++){
            probCandidate[i] = fFusion[idxCandidate[i]];
        }
        result.setIdxCandidate(idxCandidate);
        result.setProbCandidate(probCandidate);
        result.setLocationTime(System.currentTimeMillis());
        result.setPos(new Pos(x,y));
        return result;
    }

}
