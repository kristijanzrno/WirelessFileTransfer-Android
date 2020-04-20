package com.example.wirelessfiletransfer.Network;

import android.os.AsyncTask;

import com.example.wirelessfiletransfer.Constants;
import com.example.wirelessfiletransfer.DiscoveryUtils;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

public class SignalBroadcaster extends AsyncTask<String, String, String> {

    private DatagramSocket socket;
    private DiscoveryUtils discoveryUtils;
    private int localReceiverPort;

    public SignalBroadcaster(DatagramSocket socket, int localReceiverPort, DiscoveryUtils discoveryUtils) {
        this.socket = socket;
        this.localReceiverPort = localReceiverPort;
        this.discoveryUtils = discoveryUtils;
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            socket.setBroadcast(true);
            while(true) {
                if(isCancelled()) {
                    System.out.println("bg cancelled");
                    break;
                }
                byte[] discoveryRequest = (Constants.DISCOVERY_BROADCAST+localReceiverPort).getBytes();
                DatagramPacket packet = new DatagramPacket(discoveryRequest, discoveryRequest.length, InetAddress.getByName("255.255.255.255"), 8899);
                socket.send(packet);
                Enumeration networkInterfaces = NetworkInterface.getNetworkInterfaces();
                while (networkInterfaces.hasMoreElements()) {
                    NetworkInterface networkInterface = (NetworkInterface) networkInterfaces.nextElement();
                    if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                        continue;
                    }
                    for (InterfaceAddress address : networkInterface.getInterfaceAddresses()) {
                        InetAddress broadcast = address.getBroadcast();
                        if (broadcast == null) {
                            continue;
                        }
                        try {
                            socket.send(packet);
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                }
                discoveryUtils.onNetworkScanned();
                Thread.sleep(Constants.BROADCAST_WAIT_TIME);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
}