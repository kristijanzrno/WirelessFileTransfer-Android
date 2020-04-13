package com.example.wirelessfiletransfer;

import android.os.AsyncTask;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class SignalReceiver extends AsyncTask<String, String, String> {

    private DiscoveryUtils discoveryUtils;
    private DatagramSocket socket;

    public SignalReceiver(DiscoveryUtils discoveryUtils, DatagramSocket socket){
        this.discoveryUtils = discoveryUtils;
        this.socket = socket;
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            while(true) {
                if(isCancelled())
                    break;
                byte[] receiveBuffer = new byte[30000];
                DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                socket.receive(receivePacket);
                String message = new String(receivePacket.getData()).trim();
                if (message.startsWith("disc_")) {
                    message = message.replace("disc_", "");
                    String ip = receivePacket.getSocketAddress().toString().split(":")[0];
                    String port = message.split("::")[1];
                    System.out.println("IP: " + ip + "\n" + "Port: " + port);
                    discoveryUtils.onDeviceDiscovered(ip, port, message);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
