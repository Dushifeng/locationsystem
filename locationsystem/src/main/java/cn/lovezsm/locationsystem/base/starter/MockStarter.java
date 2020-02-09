package cn.lovezsm.locationsystem.base.starter;

import cn.lovezsm.locationsystem.base.bean.*;
import cn.lovezsm.locationsystem.base.config.APConfig;
import cn.lovezsm.locationsystem.base.config.LocationConfig;
import cn.lovezsm.locationsystem.base.service.DataDirectCenter;
import cn.lovezsm.locationsystem.base.util.DataParser;
import cn.lovezsm.locationsystem.base.util.SpringUtils;
import cn.lovezsm.locationsystem.locationSystem.algorithm.LocationAlgorithm;
import cn.lovezsm.locationsystem.locationSystem.algorithm.impl.LocalizeByRSSDAC;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.socket.DatagramPacket;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class MockStarter implements CommandLineRunner {

    ExecutorService service = Executors.newFixedThreadPool(5);


    List<Integer> start = Arrays.asList( 8,24,37,54,70,87,102,117,136,
            153,169,188,201,218,232,249,264,281,295,312,329,348,
            387,407,430,449,470,491,514,535,557,577,602);
    List<Integer> end =   Arrays.asList(19,35,48,68,82,99,113,130,147,
            166,181,199,212,230,243,261,274,292,305,324,342,361,
            401,423,444,463,484,505,529,550,570,593,616);


    @Override
    public void run(String... args)  {
        service.submit(()->{
            try {
                File rawFile = new File("C:\\Users\\61616\\Desktop\\实验数据\\双人.txt");
                LocationConfig locationConfig = SpringUtils.getBean(LocationConfig.class);

                FingerPrint fingerPrint = FingerPrintBuilder.build("",
                        new File("Fingerprint_avg_test.dat"),
                        new File("Fingerprint_std_test.dat"));
                GridMap gridMap = GridMap.buildByFile(new File("coordinate.txt"),"");
                APConfig apConfig = APConfig.buildByFile(new File("ap.txt"));

                LocationAlgorithm locationAlgorithm = new LocalizeByRSSDAC();

                BufferedReader reader = new BufferedReader(new FileReader(rawFile));
                List<Item> data = new ArrayList<Item>();
                String s;
                while ((s=reader.readLine())!=null){
                    String[] ss = s.split(":");
                    data.add(new Item(Long.parseLong(ss[0]),ss[1]));
                }

                Map<Integer, List<Item>> map = transform(data);

                BufferedWriter writer = new BufferedWriter(new FileWriter(new File("two-wifi-loc.txt")));
                for (Map.Entry<Integer,List<Item>> entry:map.entrySet()){

                    

                    List<Item> value = entry.getValue();
                    Collections.sort(value,Comparator.comparingLong(i->i.ts));

                    List<Message> ldata = new ArrayList<>();
                    for (Item item:value){
                        List<Message> messages = DataParser.parseRawData(item.ts,item.data);
                        ldata.addAll(messages);
                    }

                    //3.装填算法参数
                    Map<String,Object> locationParams = new HashMap<>();

                    locationParams.put("locationConfig",locationConfig);
                    locationParams.put("fingerPrint",fingerPrint);
                    locationParams.put("gridMap",gridMap);
                    locationParams.put("apConfig",apConfig);
                    locationParams.put("data",ldata);
                    List<LocationResult> results = locationAlgorithm.locate(locationParams);
                    for (LocationResult result:results){
                        writer.write((entry.getKey()+1)+","+result.getDevMac()+","+result.getPos().getX()+","+result.getPos().getY());
                        writer.newLine();
                    }
                }
                writer.flush();
                writer.close();
                System.out.println("finished....");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Data
    @AllArgsConstructor
    public static class Item{
        private long ts;
        private String data;
    }

    public static Map<Integer,List<Item>> transform(List<Item> items){
        long fts = items.get(0).ts;
        Map<Integer,List<Item>> map = new TreeMap<>();
        for (Item item:items){
            int sec = (int) ((item.ts - fts) / 1000);
            if (!map.containsKey(sec)){
                map.put(sec,new ArrayList<>());
            }
            List<Item> tl = map.get(sec);
            tl.add(item);
        }

        return map;
    }
}
