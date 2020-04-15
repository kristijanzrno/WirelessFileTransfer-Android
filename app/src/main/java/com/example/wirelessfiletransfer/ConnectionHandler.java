package com.example.wirelessfiletransfer;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;

import com.example.wirelessfiletransfer.Model.Action;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

public class ConnectionHandler extends AsyncTask<String, Void, Void> {
    private Socket socket;
    private DataInputStream input;
    private DataOutputStream output;
    private String message = null;
    //private String action = "";
    private boolean isRunning = true;
    private SendToActivity sendToActivity;
    private Activity activity;
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
                if (input == null)
                    input = new DataInputStream(socket.getInputStream());
                if (output == null)
                    output = new DataOutputStream(socket.getOutputStream());
            }catch(Exception e){

            }
            //todo receive
            //String message = receiveMessage();
            try {
                Action action = actions.poll();
                if(action != null) {
                    switch (action.getAction()) {
                        case "send_message":
                            writeMessage(action.getMessage());
                            break;
                        case "send_file":
                            System.out.println("send file...");
                            writeMessage(action.getMessage());
                            writeFile(action.getUri());
                            break;
                    }
                }else{
                    output.writeUTF("");
                }
            } catch (IOException e) {
                e.printStackTrace();
                sendToActivity.sendMessage(e.getMessage());
            }

        }
        return null;
    }


    // Wrapper
    public void sendMessage(String message){
        actions.add(new Action("send_message", message, null));
    }

    public void sendFile(String message, Uri uri){
        actions.add(new Action("send_file", message, uri));
    }

    private void writeMessage(String message) throws IOException {
        output.writeUTF(message);
        //action = "";
    }

    private void writeFile(Uri fileUri){
        System.out.println("sending...");
        try {
            InputStream inputStream = activity.getContentResolver().openInputStream(fileUri);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(socket.getOutputStream());
            output.flush();
            byte[] buffer = new byte[8192];
            int count;
            while((count = bufferedInputStream.read(buffer)) > 0){
                bufferedOutputStream.write(buffer, 0, count);
            }
            bufferedOutputStream.flush();
            bufferedInputStream.close();
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("done...");
    }

    private String receiveMessage() {
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
