package com.example.wirelessfiletransfer;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;

import com.example.wirelessfiletransfer.Model.Action;
import com.example.wirelessfiletransfer.Utils.FileHandler;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

public class ConnectionHandler extends AsyncTask<String, Void, Void> {
    private Activity activity;
    private Socket socket;

    private boolean isRunning = true;
    private SendToActivity sendToActivity;
    private Queue<Action> actions;

    private String ip;
    private int port;

    public ConnectionHandler(String ip, int port, Activity activity){
        this.ip = ip;
        this.port = port;
        this.activity = activity;
        this.actions = new LinkedList<>();
        sendToActivity = (SendToActivity) activity;
    }

    @Override
    protected Void doInBackground(String... strings) {
        DataInputStream input = null;
        DataOutputStream output = null;
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
        System.out.println("Connected...");
        while(isRunning) {
            try {
                Action action = actions.poll();
                if(action != null) {
                    writeMessage(action.getMessage(), output);
                    switch (action.getAction()) {
                        case "send_message":
                            break;
                        case "send_file":
                            FileHandler.readFile(activity, action.getUri(), output);
                            break;
                    }
                }else{
                    writeMessage("", output);
                }
            } catch (IOException e) {
                e.printStackTrace();
                sendToActivity.sendMessage(e.getMessage());
            }
            // RECEIVING
            String receivedMessage = receiveMessage(input);
            String[] message = receivedMessage.split(Constants.DATA_SEPARATOR);
            switch (message[0]) {
                case Constants.FILE_SEND_MESSAGE:
                    System.out.println("Preparing to receive data. Receiving " + message[1] + " items.");
                    break;
                case Constants.FILE_NAME_MESSAGE:
                    String filename = message[1];
                    long fileSize = Long.parseLong(message[2]);
                    System.out.println("Receiving " + filename + "...");
                    FileHandler.writeFile(activity, filename, fileSize, input);
                    break;
                case Constants.FILE_SEND_COMPLETE_MESSAGE:
                    break;
            }

        }
        return null;
    }


    // Wrappers
    public void sendMessage(String message){
        actions.add(new Action("send_message", message, null));
    }

    public void sendFile(String message, Uri uri){
        actions.add(new Action("send_file", message, uri));
    }

    private void writeMessage(String message, DataOutputStream output) throws IOException {
        output.writeUTF(message);
    }

    private String receiveMessage(DataInputStream input) {
        if (input != null) {
            try {
                return input.readUTF();
            } catch (IOException e) {
                e.printStackTrace();
                return "Could not read data";
            }
        }
        return "";
    }

    public void endConnection() throws IOException {
        socket.close();
    }

}
