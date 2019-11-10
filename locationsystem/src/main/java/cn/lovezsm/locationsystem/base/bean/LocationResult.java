package cn.lovezsm.locationsystem.base.bean;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;


@Data
public class LocationResult {
    private Pos pos;
    private Long locationTime;
    private String devMac;
    private List<Integer> idxCandidate = new ArrayList<>();
    private List<Double> probCandidate = new ArrayList<>();
    private int gridMapNum;

    public void setIdxCandidate(int[] idxCandidate) {
        for(int a:idxCandidate){
            this.idxCandidate.add(a);
        }
    }

    public void setProbCandidate(double[] probCandidate) {
        for(double a:probCandidate){
            this.probCandidate.add(a);
        }
    }
}
