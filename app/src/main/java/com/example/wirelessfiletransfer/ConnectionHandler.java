package com.example.wirelessfiletransfer;

import android.app.Activity;
import android.os.AsyncTask;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ConnectionHandler extends AsyncTask<String, Void, Void> {
    private Socket socket;
    private DataInputStream input;
    private DataOutputStream output;
    private String message = null;
    private boolean isRunning = true;
    private SendToActivity sendToActivity;

    private String ip;
    private int port;

    public ConnectionHandler(String ip, int port, Activity activity){
        this.ip = ip;
        this.port = port;
        sendToActivity = (SendToActivity) activity;
    }

    @Override
    protected Void doInBackground(String... strings) {
        try {
            socket = new Socket(ip, port);
            if(socket != null) {
                input = new DataInputStream(socket.getInputStream());
                output = new DataOutputStream(socket.getOutputStream());
            }
        } catch (IOException e) {
            e.printStackTrace();
            sendToActivity.sendMessage(e.getMessage());
            isRunning = false;
        }
        sendToActivity.sendMessage("Connected...");
        while(isRunning) {
            try {
                if (message != null) {
                    output.writeUTF(message);
                    message = null;
                }else
                    output.writeUTF("");
            } catch (IOException e) {
                sendToActivity.sendMessage(e.getMessage());
            }
        }
        return null;
    }

    public void send(String message){
        this.message = message;
    }

    public void endConnection() throws IOException {
        socket.close();
    }

}
