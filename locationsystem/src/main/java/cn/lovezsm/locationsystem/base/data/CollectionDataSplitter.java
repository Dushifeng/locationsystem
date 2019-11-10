package cn.lovezsm.locationsystem.base.data;

import cn.lovezsm.locationsystem.base.bean.Message;
import cn.lovezsm.locationsystem.base.config.APConfig;
import cn.lovezsm.locationsystem.collectionSystem.config.CollectionConfig;
import cn.lovezsm.locationsystem.base.util.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class CollectionDataSplitter {

    private ReentrantLock lock = new ReentrantLock();

    private static ThreadPoolExecutor pool = new ThreadPoolExecutor(30,60,10, TimeUnit.SECONDS, new LinkedBlockingQueue(10));

    private ConcurrentHashMap<String,Node> cache = new ConcurrentHashMap<>(32);

    private BufferedWriter writerIn;
    private BufferedWriter writerOut;
    private File fileIn;
    private File fileOut;

    @Autowired
    private CollectionConfig collectionConfig;

    public void setCollectionConfig(CollectionConfig collectionConfig) {
        this.collectionConfig = collectionConfig;
    }

    public boolean startCollection(){
        try {
            fileIn = File.createTempFile(WebUtils.UUIDGenerator.getUUID(),"in_fingerprintdata.dat");
            fileOut = File.createTempFile(WebUtils.UUIDGenerator.getUUID(),"out_fingerprintdata.dat");
            writerIn = new BufferedWriter(new FileWriter(fileIn,true));
            writerOut = new BufferedWriter(new FileWriter(fileOut,true));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public File[] finishCollection(){
        try {
            writerIn.close();
            writerOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        File[] ans = new File[2];
        ans[0] = fileIn;
        ans[1] = fileOut;
        fileIn = null;
        fileOut = null;
        return ans;
    }

    public void registered(String id, String devMac, int gridMapIndex){
        cache.put(devMac,new Node(devMac,gridMapIndex,id));
    }

    public Node finish(String devMac){
        Node node = null;
        Node n = cache.get(devMac);
        cache.remove(devMac);
        pool.execute(new Runnable() {
            @Override
            public void run() {
                List<Message> messages = new ArrayList<>(n.messageList);

                String mac = n.devMac;
                int gridMapIndex = n.gridMapIndex;
                APConfig apConfig = collectionConfig.getApConfig();
                lock.lock();
                try {
                    BufferedWriter writer;
                    if (gridMapIndex>collectionConfig.getInZoomNum()){
                        writer = writerOut;
                    }else {
                        writer = writerIn;
                    }
                    for (Message message:messages){
                        //1565847609591,70476001a302,e4b2fbe618cf,96,2,155,-54
                        try {
                            writer.write(message.getTimestamp()+","+message.getApMac()+","
                                    +message.getDevMac()+","+gridMapIndex+","+apConfig.getAPIndex(message.getApMac())
                                    +","+message.getFrequency()+","+message.getRssi());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    messages = null;

                }finally {
                    lock.unlock();
                }
            }
        });
        return node;
    }
    public static class Node{
        public Node(String devMac, int gridMapIndex, String id) {
            this.devMac = devMac;
            this.gridMapIndex = gridMapIndex;
            this.id = id;
        }

        public List<Message> messageList = new ArrayList<>();
        public int numOf24;
        public int numOf58;
        public String devMac;
        public int gridMapIndex = -1;
        public String id = "";
    }

}
