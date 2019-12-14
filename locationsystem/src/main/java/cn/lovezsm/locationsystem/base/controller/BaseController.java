package cn.lovezsm.locationsystem.base.controller;

import cn.lovezsm.locationsystem.base.bean.Message;
import cn.lovezsm.locationsystem.base.service.MessageServiceImpl;
import cn.lovezsm.locationsystem.base.service.StatisticsService;
import cn.lovezsm.locationsystem.base.util.DataParser;
import cn.lovezsm.locationsystem.base.web.bean.SingleDevWatchInfo;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.segments.MergeSegments;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

/**
 * 提供基础服务 如统计功能
 */
@RestController
@RequestMapping("api")
public class BaseController {

    @Autowired
    StatisticsService statisticsService;

    @Autowired
    MessageServiceImpl messageService;

    @RequestMapping("searchMessage")
    public Map<String,Object> searchMessage(@RequestParam(required = false,name = "start") Date start,
                                            @RequestParam(required = false,name = "end") Date end,
                                            @RequestParam(required = false,name = "devMac") String devMac,
                                            @RequestParam(required = false,name = "apMac") String apMac,
                                            @RequestParam(required = false,name = "frequency") Integer frequency,
                                            @RequestParam(required = false,name = "pageNo") Integer pageNo,
                                            @RequestParam(required = false,name = "pageSize") Integer pageSize){

        System.out.println(start+"  "+end+"  "+devMac+"  "+apMac+"  "+frequency+"  "+pageNo+"  "+pageSize);

        Map<String,Object> ans = new HashMap<>();
        if (pageNo == null||pageNo<1){
            pageNo = 1;
        }

        if (pageSize == null||pageSize<1){
            pageSize = Integer.MAX_VALUE;
            pageNo = 1;
        }

        Page<Message> page = new Page<>(pageNo,pageSize);



        QueryWrapper<Message> query = new QueryWrapper<>();
        Map<String,Object> params = new HashMap<>();
        params.put("dev_mac",devMac);
        params.put("ap_mac",apMac);
        params.put("frequency",frequency);

        query.allEq(params,false);
        query.between(start!=null&&end!=null,"timestamp",start,end);


        IPage<Message> messageIPage;

        messageIPage = messageService.page(page, query);
        ans.put("records",messageIPage.getRecords());
        ans.put("num",page.getTotal());
        ans.put("pageSize",page.getSize());
        ans.put("pageNo",page.getCurrent());


        return ans;

    }


    @RequestMapping("host")
    public String host() throws UnknownHostException {
        return InetAddress.getLocalHost().getHostAddress();
    }

    @RequestMapping("testPost")
    public String test(@RequestBody String json){
        System.out.println(json);

        return "success.....";
    }


    @GetMapping("startStatistics")
    public void start(@RequestParam(required = false) Integer t){
        System.out.println("start......"+t);
        if(t==null){
            t = 1;
        }

        statisticsService.start(t);
    }

    @PostMapping("getStatisticsInfo")
    public Map<String, Object> getStatisticsInfo(@RequestBody ArrayList<String> devMacs){
        Map<String, StatisticsService.Info> data = statisticsService.getData();
        Map<String,Object> ans = new HashMap<>();
        ans.put("all",data);
        for (String mac:devMacs){
            mac = DataParser.parseMac(mac);
            if (mac==null){
                continue;
            }
            SingleDevWatchInfo singleDevWatchInfo = statisticsService.getSingleDevWatchInfo(mac);
            System.out.println(singleDevWatchInfo);

            if (singleDevWatchInfo!=null){
                ans.put(mac,singleDevWatchInfo);
            }
        }
        return ans;
    }

    @GetMapping("getSingleDevWatchInfo")
    public SingleDevWatchInfo getSingleDevWatchInfo(String mac){
        return statisticsService.getSingleDevWatchInfo(mac);
    }

    @GetMapping("stopStatistics")
    public void stop(){
        System.out.println("stop....");
        statisticsService.stop();
    }
}
