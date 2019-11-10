package cn.lovezsm.locationsystem.base.data;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.LongAdder;

@Data
public class CollectionResult {
    private volatile LongAdder infoNum2G = new LongAdder();
    private volatile LongAdder infoNum5G = new LongAdder();

    private Map<Integer,Integer> numOfBandInformationMap = new HashMap<>();
    public synchronized void add2GNum(){
        infoNum2G.increment();
    }
    public synchronized void add5GNum(){
        infoNum5G.increment();
    }

    public int getInfoNum2G() {
        return infoNum2G.intValue();
    }

    public int getInfoNum5G() {
        return infoNum5G.intValue();
    }
}
