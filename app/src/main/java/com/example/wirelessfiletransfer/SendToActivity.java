package com.example.wirelessfiletransfer;

public interface SendToActivity {
    void sendMessage(String message);
    void onDeviceConnected();
    void onConnectionRefused();
    void onConnectionTerminated();
    void onReceivingFiles(int noOfFiles);
    void onFileReceived(String file);
}
