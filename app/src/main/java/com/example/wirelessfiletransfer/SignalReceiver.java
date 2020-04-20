package com.example.wirelessfiletransfer;

import android.os.AsyncTask;

import com.example.wirelessfiletransfer.Model.Device;
import com.example.wirelessfiletransfer.Model.Message;

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
                String data = new String(receivePacket.getData()).trim();
                Message message = new Message(data);
                if(message.getAction().equals(Constants.DEVICE_DISCOVERED)){
                    //message = message.replace("disc_", "");
                    //String[] data = message.split("::");
                    String ip = receivePacket.getSocketAddress().toString().split(":")[0];
                    String port = message.paramAt(1);
                    ip = ip.replaceAll("[^\\d.]", "");
                    port = port.replaceAll("[^\\d.]", "");
                    Device device = new Device(message.paramAt(0), ip, Integer.parseInt(port), message.paramAt(2), message.paramAt(3));
                    discoveryUtils.onDeviceDiscovered(device);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
