package com.example.wirelessfiletransfer.Model;

public class Device {
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
}