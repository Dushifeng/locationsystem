package cn.lovezsm.locationsystem.base.web.bean;

import lombok.Data;

@Data
public class Result {
    private int status;
    private String message;
    private Object refObject;
}
