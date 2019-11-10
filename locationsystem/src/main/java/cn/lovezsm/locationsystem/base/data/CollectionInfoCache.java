package cn.lovezsm.locationsystem.base.data;

import cn.lovezsm.locationsystem.base.bean.Message;
import cn.lovezsm.locationsystem.base.config.APConfig;
import cn.lovezsm.locationsystem.collectionSystem.bean.NewTask;
import cn.lovezsm.locationsystem.collectionSystem.bean.RegisterBean;
import cn.lovezsm.locationsystem.collectionSystem.config.CollectionConfig;
import cn.lovezsm.locationsystem.collectionSystem.service.CollectionService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@Component
public class CollectionInfoCache {
    private volatile Map<String, RegisterBean> map = new ConcurrentHashMap<>();
    private List<RegisterBean> finishedList = new CopyOnWriteArrayList<>();
    private BlockingQueue<Message> messageBlockingQueue = new LinkedBlockingQueue<>();
    private NewTask task;
    private static int threadNum = 10;
    private static ExecutorService threadPoolExecutor;
    private List<WriterContent> contentList = new CopyOnWriteArrayList<>();
    private BufferedWriter inFileWriter;
    private BufferedWriter outFileWriter;

    public void startCollection(NewTask task){

        this.task = task;
        try {
            inFileWriter = Files.newBufferedWriter(task.getInFile().toPath());
            outFileWriter = Files.newBufferedWriter(task.getOutFile().toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        threadPoolExecutor = Executors.newFixedThreadPool(threadNum);
        for (int i = 0; i < threadNum; i++) {
            threadPoolExecutor.execute(new CollectionInfoWriter());
        }

    }
    public void stopCollection(){

        while (messageBlockingQueue.size()>0){}
        threadPoolExecutor.shutdownNow();
        threadPoolExecutor = null;
        //写文件
        try {
            int inZoomNum = task.getConfig().getInZoomNum();
            for (WriterContent content:contentList) {
                if (content==null){
                    continue;
                }
                if (content.gridMapIndex>inZoomNum){
                    outFileWriter.append(content.toString());
                    outFileWriter.newLine();
                }else {
                    inFileWriter.append(content.toString());
                    inFileWriter.newLine();
                }
            }
            outFileWriter.flush();
            inFileWriter.flush();
        }catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                inFileWriter.close();
                outFileWriter.close();
                inFileWriter = null;
                outFileWriter = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        task = null;
        messageBlockingQueue.clear();
    }
    private class CollectionInfoWriter implements Runnable{

        @Override
        public void run() {
            if (task==null){
                return;
            }
            CollectionConfig config = task.getConfig();
            APConfig apConfig = config.getApConfig();
            while (true){
                if (!CollectionService.isCollectionMode){
                    return;
                }
                try {
                    Message message = messageBlockingQueue.take();
                    RegisterBean registerBean = map.get(message.getDevMac());
                    if (registerBean == null||!registerBean.isDoing()){
                        continue;
                    }
                    synchronized (message){
                        if (message.getFrequency()<15){
                            registerBean.getResult().add2GNum();
                        }else {
                            registerBean.getResult().add5GNum();
                        }

                        WriterContent content = new WriterContent(message.getTimestamp()
                                ,message.getApMac(),message.getDevMac(),
                                registerBean.getGridmapIndex(),apConfig.getAPIndex(message.getApMac()),message.getFrequency(),message.getRssi());

                        contentList.add(content);
                    }
                } catch (InterruptedException e) {
                }
            }
        }
    }
    public boolean put(RegisterBean bean){
        for (Map.Entry<String, RegisterBean> e:map.entrySet()
             ) {
            RegisterBean value = e.getValue();
            if (value.getUuid().equals(bean.getUuid())&&value.isDoing()){
                return false;
            }
        }
        RegisterBean registerBean = getByMac(bean.getMac());
        if (registerBean!=null&&registerBean.isDoing()){
            return false;
        }
        map.put(bean.getMac(),bean);
        bean.setDoing(true);
        System.out.println(bean);
        return true;
    }
    public void putAll(List<Message> messages){
        messageBlockingQueue.addAll(messages);
    }
    public List<RegisterBean> getFinishedTaskList(){
        return finishedList;
    }

    public List<RegisterBean> getRegisterBeanList(){
        List<RegisterBean> registerBeans = new ArrayList<>();
        registerBeans.addAll(map.values());
        return registerBeans;
    }

    public RegisterBean getByMac(String mac){
        return map.get(mac);
    }

    public RegisterBean getByUUID(String uuid){
        for (Map.Entry<String,RegisterBean> entry:map.entrySet()
             ) {
            if (uuid.equals(entry.getValue().getUuid())){
                return entry.getValue();
            }
        }
        return null;
    }
    public void stopSingleTask(String mac){
        RegisterBean registerBean = map.get(mac);
        if (registerBean!=null){
            registerBean.setDoing(false);
            registerBean.setEndTime(Instant.now().getEpochSecond());
            finishedList.add(registerBean);
        }
    }

    @AllArgsConstructor
    private class WriterContent{
        //1565847609591,70476001a302,e4b2fbe618cf,96,2,155,-54
        private long ts;
        private String apMac;
        private String devMac;
        private int gridMapIndex;
        private int apIndex;
        private int frequency;
        private int rssi;

        @Override
        public String toString() {
            return ts+","+apMac+","+devMac+","+gridMapIndex+","+apIndex+","+frequency+","+rssi;
        }
    }
}
