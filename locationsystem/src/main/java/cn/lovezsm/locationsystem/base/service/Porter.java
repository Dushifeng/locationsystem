package cn.lovezsm.locationsystem.base.service;

import cn.lovezsm.locationsystem.base.bean.Message;

import java.util.List;

public abstract class Porter<T> {

    public String name;

    public Porter(String name) {
        this.name = name;
    }

    abstract void port(T messages);

    public void register(){
        DataDirectCenter.register(this);
    }

    public void unregister(){
        DataDirectCenter.unregister(name);
    }

}
