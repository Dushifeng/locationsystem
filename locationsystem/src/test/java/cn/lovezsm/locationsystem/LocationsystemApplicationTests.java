package cn.lovezsm.locationsystem;

import cn.lovezsm.locationsystem.base.bean.Message;
import cn.lovezsm.locationsystem.base.mapper.MessageMapper;
import cn.lovezsm.locationsystem.base.util.SpringUtils;
import com.alibaba.druid.util.DruidDataSourceUtils;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.mysql.cj.jdbc.MysqlDataSource;
import com.mysql.cj.jdbc.MysqlDataSourceFactory;
import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import javax.swing.*;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LocationsystemApplicationTests {

    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    MessageMapper mapper;

    @Test
    public void test(){

        try{
            DataSource dataSource = applicationContext.getBean(DataSource.class);
//            System.out.println(mapper.existTable("message"));
//            int locationsystem = mapper.createDB("locationsystem");

            System.out.println(mapper.showDB());

        }catch (Exception e){
            e.printStackTrace();
        }


//        //创建连接池
//        DataSource dataSource = new PooledDataSource("com.mysql.cj.jdbc.Driver",
//                "jdbc:mysql://localhost:3306/locationsystem", "root", "dsf123456");
//        //事务
//        TransactionFactory transactionFactory = new JdbcTransactionFactory();
//        //创建环境
//        Environment environment = new Environment("development", transactionFactory, dataSource);
//
//        //创建配置
//        Configuration configuration = new Configuration(environment);
//
//
//
//        //开启驼峰规则
//        configuration.setMapUnderscoreToCamelCase(true);
//
//        //加入资源（Mapper接口）
//        configuration.addMappers("cn.lovezsm.locationsystem.base.mapper");
//        //
//        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
//
//        SqlSession session = sqlSessionFactory.openSession();
//
//        try {
//
//            MessageMapper mapper = session.getMapper(MessageMapper.class);
//
//            int message = mapper.existTable("message");
//            System.out.println(message);
////            mapper.insert(new Message("123456",2,-50,"23456", Instant.now().toEpochMilli()));
//        } finally {
//            session.close();
//        }


    }

}
