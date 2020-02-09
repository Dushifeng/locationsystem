package cn.lovezsm.locationsystem.base.mapper;

import cn.lovezsm.locationsystem.base.bean.Message;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface MessageMapper  extends BaseMapper<Message> {


    @Select("select count(*) from information_schema.TABLES where LCASE(table_name)=#{tableName} ")
    boolean existTable(String tableName);

    @Update("create dat abase ${connectionname}")
    int createDB(@Param("connectionname")String db);

    @Update("create table message (" +
            "id int primary key auto_increment," +
            "dev_mac varchar(45) not null," +
            "frequency int not null," +
            "rssi int not null," +
            "ap_mac varchar(45) not null," +
            "timestamp DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP" +
            ") character set utf8 collate utf8_bin;")
    int createTable();


    @Select("show databases;")
    List<String> showDB();
//    int batchInsert(@Param("messageList") List<Message> messageList);



}
