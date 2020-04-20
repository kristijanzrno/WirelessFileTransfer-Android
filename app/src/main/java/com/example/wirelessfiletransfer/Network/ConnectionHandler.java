package com.example.wirelessfiletransfer.Network;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;

import com.example.wirelessfiletransfer.Constants;
import com.example.wirelessfiletransfer.Model.Action;
import com.example.wirelessfiletransfer.Model.Device;
import com.example.wirelessfiletransfer.Model.Message;
import com.example.wirelessfiletransfer.R;
import com.example.wirelessfiletransfer.SendToActivity;
import com.example.wirelessfiletransfer.Utils.FileHandler;
import com.example.wirelessfiletransfer.Utils.FileUtils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.LinkedList;
import java.util.Queue;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

public class ConnectionHandler extends AsyncTask<String, Void, Void> {

    private Activity activity;
    private SSLSocket socket;
    private boolean isRunning = false;
    private SendToActivity sendToActivity;
    private Queue<Action> actions;

    private Device device;

    public ConnectionHandler(Device device, Activity activity){
        this.device = device;
        this.activity = activity;
        this.actions = new LinkedList<>();
        sendToActivity = (SendToActivity) activity;
    }

    @Override
    protected Void doInBackground(String... strings) {
        DataInputStream input = null;
        DataOutputStream output = null;
        try {
            SSLSocketFactory sslSocketFactory = importCertificate();
            socket = (SSLSocket) sslSocketFactory.createSocket(device.getIp(), device.getPort());
            socket.startHandshake();
            if(socket != null) {
                input = new DataInputStream(socket.getInputStream());
                output = new DataOutputStream(socket.getOutputStream());
                isRunning = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Connected...");

        while(isRunning) {
            Action action = actions.poll();

            try {
                if(action != null) {
                    writeMessage(action.getMessage(), output);
                    switch (action.getAction()) {
                        case "send_message":
                            break;
                        case "send_file":
                            if(!FileHandler.readFile(activity, action.getUri(), output)) {
                                sendMessage(Constants.FILE_TRANSFER_ERROR);
                                sendToActivity.onFileTransferFailed(FileUtils.getFileName(action.getUri(), activity));
                            }
                            break;
                    }
                }else{
                    writeMessage("", output);
                }
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
            // RECEIVING
            Message receivedMessage;
            try {
                receivedMessage = receiveMessage(input);
            }catch (Exception e){
                e.printStackTrace();
                break;
            }
            if(receivedMessage != null) {
                switch (receivedMessage.getAction()) {
                    case Constants.FILE_SEND_MESSAGE:
                        System.out.println("Preparing to receive data. Receiving " + receivedMessage.paramAt(0) + " items.");
                        sendToActivity.onReceivingFiles(Integer.parseInt(receivedMessage.paramAt(0)));
                        break;
                    case Constants.FILE_NAME_MESSAGE:
                        String filename = receivedMessage.paramAt(0);
                        long fileSize = Long.parseLong(receivedMessage.paramAt(1));
                        if (FileHandler.writeFile(activity, filename, fileSize, input))
                            sendToActivity.onFileReceived();
                        else{
                            sendMessage(new Message.Builder().add(Constants.FILE_TRANSFER_ERROR).add(filename).build());
                            sendToActivity.onFileTransferFailed(filename);
                        }
                        break;
                    case Constants.FILE_RECEIVED:
                        sendToActivity.onFileTransferred();
                        break;
                    case Constants.FILE_TRANSFER_ERROR:
                        sendToActivity.onFileTransferFailed(receivedMessage.paramAt(0));
                        break;
                    case Constants.CONNECTION_TERMINATOR:
                        isRunning = false;
                        break;
                    case Constants.CONNECTION_ACCEPTED:
                        sendToActivity.onDeviceConnected();
                        break;
                    case Constants.CONNECTION_REFUSED:
                        sendToActivity.onConnectionRefused();
                        break;
                }
            }

        }
        terminateConnection(isRunning);
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

    private Message receiveMessage(DataInputStream input) throws IOException {
        if (input != null) {
                return new Message(input.readUTF());
        }
        return null;
    }

    private void terminateConnection(boolean wasRunning){
        try {
            if (socket != null && !socket.isClosed())
                socket.close();
            if(wasRunning)
                sendToActivity.onConnectionTerminated();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void endConnection() {
        isRunning = false;
        sendMessage(Constants.CONNECTION_TERMINATOR);
    }

    public void connect(Device device){
        sendMessage(new Message.Builder().add(Constants.CONNECTION_REQUEST).add(device.getName()).build());
    }

    private SSLSocketFactory importCertificate(){
        try {
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            InputStream key = activity.getResources().openRawResource(R.raw.client);
            keyStore.load(key, "client".toCharArray());
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("X509");
            keyManagerFactory.init(keyStore, "client".toCharArray());
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), new SecureRandom());
            return context.getSocketFactory();
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

}
