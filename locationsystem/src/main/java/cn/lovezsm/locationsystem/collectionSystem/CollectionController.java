package cn.lovezsm.locationsystem.collectionSystem;

import cn.lovezsm.locationsystem.base.data.CollectionResult;
import cn.lovezsm.locationsystem.base.util.WebUtils;
import cn.lovezsm.locationsystem.base.web.bean.Dev;
import cn.lovezsm.locationsystem.base.web.bean.Result;
import cn.lovezsm.locationsystem.collectionSystem.bean.RegisterBean;
import cn.lovezsm.locationsystem.collectionSystem.service.CollectionService;
import com.alibaba.fastjson.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/collectionSys")
public class CollectionController {
    @Autowired
    CollectionService service;



    @GetMapping("login")
    public Result login(HttpServletResponse response,@CookieValue(value = "token" ,required = false) String token){
        Result result = new Result();
        if (token == null){
            WebUtils.writeCookie(response,"token",WebUtils.UUIDGenerator.getUUID());
            System.out.println("第一次登陆");
            result.setStatus(100);
            return result;
        }else {
            System.out.println(token);
            RegisterBean registerBean = service.searchLoginInfo(token);
            result.setStatus(200);
            result.setRefObject(registerBean);
            return result;
        }

    }

    @PostMapping("submitNewTask")
    public Result submitTask(@RequestParam("devMapList") String devMapListStr,
                             @RequestParam("gridmapFile") MultipartFile gridmapFile,
                             @RequestParam("apListFile") MultipartFile apListFile,
                             @RequestParam("innerNum") int innerNum)  {

        List<Dev> devs = JSONArray.parseArray(devMapListStr, Dev.class);
        Result result = new Result();
        //读取文件
        File gridmapFile1 = null;
        File apListFile1 = null;
        try {
            gridmapFile1 = WebUtils.transMultipartFile(gridmapFile);
            apListFile1 = WebUtils.transMultipartFile(apListFile);
        } catch (IOException e) {
            e.printStackTrace();
            result.setStatus(100);
            result.setMessage("服务器生成临时文件出错，请联系管理员解决");
            return result;
        }

        //-->service
        result = service.genNewTask(devs, gridmapFile1, apListFile1, innerNum);
        return result;
    }
    @GetMapping("getDevList")
    public Result getDevList(){
        Result result = new Result();
        if (!service.hasTask()){
            result.setStatus(100);
            result.setMessage("当前无采集任务，请联系管理员");
        }else {
            List<Dev> devList = service.getDevList();
            result.setStatus(200);
            result.setRefObject(devList);
        }

        return result;
    }

    @GetMapping("getGridMapXY")
    public Result getGridMapXY(@RequestParam("index") int index){
        return service.getGridMapXY(index);
    }
    @PostMapping("registerTask")
    public Result registerTask(@CookieValue(value = "token" ,required = true) String token,@RequestParam("mac") String mac,@RequestParam("x") double x,@RequestParam("y") double y){
        return service.registerTask(token,mac,x,y);
    }
    @PostMapping("completeSingle")
    public Result completeSingle(@RequestParam("mac") String mac){
        CollectionResult collectionResult = service.completeSingle(mac);
        Result result = new Result();
        result.setStatus(200);
        result.setRefObject(collectionResult);
        return result;
    }
    @GetMapping("getTaskList")
    public Map<String,Object> getTaskList(){
        Map<String,Object> resultMap = new HashMap<>();
        List<RegisterBean> doingTask = service.getDoingTask();
        List<RegisterBean> doneTask = service.getDoneTask();

        resultMap.put("doing",doingTask);
        resultMap.put("done",doneTask);
        resultMap.put("devMap",service.getDevMap());
        return resultMap;
    }
    @GetMapping("recoverTask")
    public Result recoverTask(){
        Result result = new Result();
        if (!service.hasTask()){
            result.setStatus(100);
            return result;
        }
        result.setStatus(200);
        result.setRefObject(service.getCurNewTask());
        return result;
    }
    @GetMapping("finishAllTask")
    public void finishAllTask(HttpServletResponse response) throws IOException {
        service.stopTask(response.getOutputStream());
    }
}
