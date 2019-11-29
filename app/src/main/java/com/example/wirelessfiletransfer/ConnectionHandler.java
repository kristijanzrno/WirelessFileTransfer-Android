package com.example.wirelessfiletransfer;

import android.os.AsyncTask;
import android.widget.EditText;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class ConnectionHandler extends AsyncTask<String, Void, Void> {
    private Socket socket;
    private PrintWriter writer;
    private String message = null;
    private boolean isRunning = true;

    private String ip;
    private int port;

    public ConnectionHandler(String ip, int port){
        this.ip = ip;
        this.port = port;
    }

    @Override
    protected Void doInBackground(String... strings) {
        try {
            socket = new Socket(ip, port);
            if(socket != null)
                writer = new PrintWriter(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
            isRunning = false;
        }

        while(isRunning) {
            if (message != null) {
                writer.write(message);
                writer.flush();
                message = null;
            }
        }
        return null;
    }

    public void send(String message){
        this.message = message;
    }

    public void endConnection() throws IOException {
        writer.close();
        socket.close();
    }

}
