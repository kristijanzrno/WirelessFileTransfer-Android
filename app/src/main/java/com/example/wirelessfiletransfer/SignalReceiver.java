package com.example.wirelessfiletransfer;

import android.os.AsyncTask;

import com.example.wirelessfiletransfer.Model.Device;

import java.io.Serializable;
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
                    String[] data = message.split("::");
                    String ip = receivePacket.getSocketAddress().toString().split(":")[0];
                    String port = data[1];
                    ip = ip.replaceAll("[^\\d.]", "");
                    port = port.replaceAll("[^\\d.]", "");
                    Device device = new Device(data[0], ip, Integer.parseInt(port), data[2], data[3]);
                    discoveryUtils.onDeviceDiscovered(device);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
