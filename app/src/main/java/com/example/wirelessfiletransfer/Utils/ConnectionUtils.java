package com.example.wirelessfiletransfer.Utils;

import android.app.Activity;
import android.net.Uri;

import com.example.wirelessfiletransfer.ConnectionHandler;
import com.example.wirelessfiletransfer.Constants;

import java.util.ArrayList;

public class ConnectionUtils {

    private String ip;
    private int port;
    private Activity parent;

    private ConnectionHandler connectionHandler;

    public ConnectionUtils(String ip, int port, Activity parent) {
        this.ip = ip;
        this.port = port;
        this.parent = parent;
        connectionHandler = new ConnectionHandler(ip, port, parent);
        connectionHandler.execute();
    }

    public void transferFiles(ArrayList<Uri> uris){
        // Preparing the server to receive n files
        connectionHandler.sendMessage(Constants.FILE_SEND_MESSAGE + Constants.DATA_SEPARATOR + uris.size());
        for(Uri uri : uris){
            String filename = FileUtils.getFileName(uri, parent);
            long fileSize = FileUtils.getFileSize(uri, parent);
            connectionHandler.sendFile(Constants.FILE_NAME_MESSAGE + Constants.DATA_SEPARATOR + filename + Constants.DATA_SEPARATOR + fileSize, uri);
        }
    }

}
