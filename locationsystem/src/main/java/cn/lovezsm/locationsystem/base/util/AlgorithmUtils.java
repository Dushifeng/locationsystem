package cn.lovezsm.locationsystem.base.util;

import java.util.Arrays;

public class AlgorithmUtils {

    public static double euclideanDistance(double x1,double y1,double x2,double y2){
        return Math.sqrt((x1-x2)*(x1-x2)+(y1-y2)*(y1-y2));
    }

    public static double getVectorAvgVal(double[] vals){
        double avg = 0d;
        int i=0;
        for (double f:vals){
            avg+=f;
            i++;
        }
        if(i==0){
            return 0;
        }
        return avg/i;
    }


    public static double getVectorScalingVal(double[] vals){
        double scalingVal = 0d;
        double avg= getVectorAvgVal(vals);
        double sum = 0d;
        int i = 0;
        for(Double f:vals){
            if(f!=null){
                sum += Math.pow(f-avg,2);
                i++;
            }
        }
        if(i==0){
            return 0;
        }
        scalingVal = Math.sqrt(sum/i);
        return scalingVal;
    }

    public static void VectorStandardization(double[] vals){
        double avgVal = getVectorAvgVal(vals);
        double scalingVal = getVectorScalingVal(vals);

        for (int i = 0;i<vals.length;i++){
            vals[i] = (vals[i]-avgVal)/scalingVal;
        }
    }

    public static double multiplication(double[] pos, double[] fFusion) {
        double ans = 0;
        for(int i =0;i<pos.length;i++){
            ans+=pos[i]*fFusion[i];
        }
        return ans;
    }

    public static void normalized(double[] fFusion) {
        double sum = 0;
        for (double a:fFusion){
            sum+=a;
        }
        for(int i=0;i<fFusion.length;i++){
            fFusion[i] = fFusion[i]/sum;
        }
    }

    public static int[] getSortedIndex(double[] fFusion) {
        int[] idx = new int[fFusion.length];
        double[] copy = Arrays.copyOf(fFusion,fFusion.length);
        double temp;
        int t;
        for(int i=0;i<fFusion.length;i++) {
            idx[i]=i;
        }
        for(int i=0;i<copy.length;i++) {
            for(int j=0;j<copy.length-i-1;j++) {
                if(copy[j]<copy[j+1])
                {
                    temp = copy[j];
                    copy[j] = copy[j+1];
                    copy[j+1] = temp;

                    t=idx[j];
                    idx[j] = idx[j+1];
                    idx[j+1] = t;
                }
            }
        }

        return idx;
    }
    /**
     * 生成M行N列的数组，数组元素初始化为data
     * @param m
     * @param n
     * @param data
     * @return
     */
    public static double[][] getMatrix(int m,int n,double data) {
        double[][] ans = new double[m][n];
        for(int i=0;i<m;i++){
            for (int j =0;j<n;j++){
                ans[i][j] = data;
            }
        }
        return ans;
    }

}
