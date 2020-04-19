package com.example.wirelessfiletransfer;

public interface SendToActivity {
    void onDeviceConnected();
    void onConnectionRefused();
    void onConnectionTerminated();
    void onFileTransferStarted(int noOfFiles);
    void onFileTransferred();
    void onReceivingFiles(int noOfFiles);
    void onFileReceived();
}
