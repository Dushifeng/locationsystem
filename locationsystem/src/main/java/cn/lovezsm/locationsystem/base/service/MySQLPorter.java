package cn.lovezsm.locationsystem.base.service;

import cn.lovezsm.locationsystem.base.bean.Message;
import cn.lovezsm.locationsystem.base.config.DataSourceConfig;
import cn.lovezsm.locationsystem.base.mapper.MessageMapper;
import cn.lovezsm.locationsystem.base.util.SpringUtils;
import com.mysql.cj.jdbc.MysqlDataSource;
import com.mysql.cj.jdbc.MysqlDataSourceFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Driver;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Component
@Slf4j
public class MySQLPorter extends Porter implements InitializingBean {

    @Autowired
    MessageServiceImpl messageService;

    @Autowired
    ApplicationContext context;



    public MySQLPorter() {
        super("MySQLPorter");
    }

    @Override
    void port(List<Message> messages) {
        messageService.saveBatch(messages);
    }



    @Override
    public void afterPropertiesSet() throws Exception {
        try {
            if ("SUCCESS".equals(DataSourceConfig.connectionStatus)){
                DataDirectCenter.register(this);
                log.info("mysql 分发器注册成功");

                log.info(messageService.list().toString());
            }else {
                log.error("MySQL 初始化失败，无法进行持久化,错误信息为："+DataSourceConfig.connectionStatus);
            }


        }catch (Exception e){
            log.error("MySQL 初始化失败，无法进行持久化");
            log.error(e.getMessage());
        }

    }



}
