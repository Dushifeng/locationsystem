package cn.lovezsm.locationsystem.base.starter;


import cn.lovezsm.locationsystem.base.config.LocationConfig;
import cn.lovezsm.locationsystem.base.mapper.MessageMapper;
import cn.lovezsm.locationsystem.base.util.ClassUtils;
import cn.lovezsm.locationsystem.base.util.SpringUtils;
import cn.lovezsm.locationsystem.locationSystem.algorithm.LocationAlgorithm;
import cn.lovezsm.locationsystem.udpServer.server.netty.NettyUDPServer;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.mysql.cj.jdbc.MysqlDataSource;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.sql.Driver;
import java.util.List;

@Component
public class ApplicationStarter implements CommandLineRunner {

    @Autowired
    ApplicationContext applicationContext;


    @Override
    public void run(String... args) throws Exception {

//        System.out.println(applicationContext.getBean(DataSource.class));

        NettyUDPServer server = NettyUDPServer.getINSTANCE();
        server.start();
    }
}
