package com.example.wirelessfiletransfer.Model;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Device implements Serializable {
    private String name;
    private String ip;
    private String port;
    private String available;
    private String info;

    public Device(){}

    public Device(String name, String ip, String port, String available, String info) {
        this.name = name;
        this.ip = ip;
        this.port = port;
        this.available = available;
        this.info = info;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getAvailable() {
        return available;
    }

    public void setAvailable(String available) {
        this.available = available;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public boolean isAvailable(){
        return available.equals("Available");
    }


    @NonNull
    @Override
    public String toString() {
        return "Device [name="+name+", ip="+ip+", port="+port+", available="+available+", info="+info+"]";
    }
}