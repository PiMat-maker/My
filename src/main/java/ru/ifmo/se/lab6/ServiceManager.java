package ru.ifmo.se.lab6;

import java.io.Serializable;

public class ServiceManager implements Serializable {
    private final ServiceCommand code;
    private String msg;

    public ServiceManager(ServiceCommand code) {
        this(code, "");
    }

    public ServiceManager(ServiceCommand code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public ServiceCommand getCode() {
        return code;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public void clearMsg() {
        msg = "";
    }

    public void appendMsg(String append) {
        msg += append;
    }

    public static void main(String[] args) {
//        ServiceManager serviceManager = new ServiceManager(ServiceCommand.INPUT);
//        serviceManager.getMsg();
    }
}
