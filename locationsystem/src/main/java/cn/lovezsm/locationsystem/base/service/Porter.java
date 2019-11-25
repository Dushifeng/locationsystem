package cn.lovezsm.locationsystem.base.service;

import cn.lovezsm.locationsystem.base.bean.Message;

import java.util.List;

public abstract class Porter {

    public String name;

    public Porter(String name) {
        this.name = name;
    }

    abstract void port(List<Message> messages);

    public void register(){
        DataDirectCenter.register(this);
    }

    public void unregister(){
        DataDirectCenter.unregister(name);
    }

}
