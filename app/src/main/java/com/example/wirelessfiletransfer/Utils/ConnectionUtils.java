package com.example.wirelessfiletransfer.Utils;

import android.app.Activity;
import android.net.Uri;
import android.os.Build;

import com.example.wirelessfiletransfer.Network.ConnectionHandler;
import com.example.wirelessfiletransfer.Constants;
import com.example.wirelessfiletransfer.Model.Device;
import com.example.wirelessfiletransfer.Model.Message;

import java.util.ArrayList;

public class ConnectionUtils {

    private Activity parent;
    private Device device;

    private ConnectionHandler connectionHandler;

    public ConnectionUtils(Device device, Activity parent) {
        this.device = device;
        this.parent = parent;
        connectionHandler = new ConnectionHandler(device, parent);
        connectionHandler.execute();
    }

    public void transferFiles(ArrayList<Uri> uris){
        // Preparing the server to receive n files
        //connectionHandler.sendMessage(Constants.FILE_SEND_MESSAGE + Constants.DATA_SEPARATOR + uris.size());
        String[] message = {Constants.FILE_SEND_MESSAGE, uris.size() + ""};
        connectionHandler.sendMessage(new Message.Builder().add(Constants.FILE_SEND_MESSAGE).add(uris.size()+"").build());
        for(Uri uri : uris){
            String filename = FileUtils.getFileName(uri, parent);
            long fileSize = FileUtils.getFileSize(uri, parent);
            connectionHandler.sendFile(Constants.FILE_NAME_MESSAGE + Constants.DATA_SEPARATOR + filename + Constants.DATA_SEPARATOR + fileSize, uri);
        }
    }

    public void connect(){
        Device android = new Device();
        android.setName(getDeviceName());
        connectionHandler.connect(android);
    }

    public void terminateConnection(){
        connectionHandler.endConnection();
    }

    public String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.toLowerCase().startsWith(manufacturer.toLowerCase())) {
            return model;
        } else {
            return manufacturer + " " + model;
        }
    }


}
