package com.example.wirelessfiletransfer;

import com.example.wirelessfiletransfer.Model.Device;

public interface DiscoveryUtils {
    void onDeviceDiscovered(Device device);
    void onNetworkScanned();
    void onDeviceSelected(Device device);
}
