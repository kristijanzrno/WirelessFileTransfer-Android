package com.example.wirelessfiletransfer;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;

import com.example.wirelessfiletransfer.Model.Action;
import com.example.wirelessfiletransfer.Utils.FileHandler;
import com.example.wirelessfiletransfer.Utils.FileUtils;
import com.example.wirelessfiletransfer.Utils.PathFinder;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
                    switch (action.getAction()) {
                        case "send_message":
                            writeMessage(action.getMessage(), output);
                            break;
                        case "send_file":
                            writeMessage(action.getMessage(), output);
                            FileHandler.readFile(activity, action.getUri(), output);
                            //writeFile(action.getUri(), output);
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
                    //receiveFile(filename, fileSize, input);
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
        //action = "";
    }

    private void writeFile(Uri fileUri, DataOutputStream output){
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

    private boolean receiveFile(String filename, long fileSize, InputStream inputStream){
        //todo file organiser
        File file = new File(FileUtils.getStoragePath(activity)+"/Pictures/"+filename);
        file.getParentFile().mkdirs();
        try {
            file.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);

            byte[] buffer = new byte[8192];
            int count = 0;
            while (fileSize > 0 && (count = inputStream.read(buffer, 0, (int) Math.min(buffer.length, fileSize))) > 0) {
                bufferedOutputStream.write(buffer, 0, count);
                fileSize -= count;
            }
            bufferedOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        System.out.println("done = " + file.getAbsolutePath());
        return true;
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
