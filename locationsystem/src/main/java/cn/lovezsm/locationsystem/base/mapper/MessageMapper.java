package cn.lovezsm.locationsystem.base.mapper;

import cn.lovezsm.locationsystem.base.bean.Message;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MessageMapper {
    @Insert("insert into message(devmac,frequency,rssi,apmac,timestamp) values(#{devMac},#{frequency},#{rssi},#{apMac},#{timestamp})")
    int insert(Message message);


    int batchInsert(@Param("messageList") List<Message> messageList);
}
