package cn.lovezsm.locationsystem.base.data;


import cn.lovezsm.locationsystem.base.bean.Message;

import java.util.List;

public interface MessageDao {
    void add(List<Message> messages);
    Long size();
    List<Message> getAll();
}
