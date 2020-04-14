package com.example.wirelessfiletransfer;

import com.example.wirelessfiletransfer.Model.Device;

public interface DiscoveryUtils {
    void onDeviceDiscovered(String ip, int port, String info);
    void onDeviceSelected(Device device);
}
