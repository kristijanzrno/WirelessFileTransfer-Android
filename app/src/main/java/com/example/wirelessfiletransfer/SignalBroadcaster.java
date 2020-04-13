package com.example.wirelessfiletransfer;

import android.os.AsyncTask;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

public class SignalBroadcaster extends AsyncTask<String, String, String> {

    private static final int WAIT_TIME = 5000;
    private DatagramSocket socket;
    private int localReceiverPort;

    public SignalBroadcaster(DatagramSocket socket, int localReceiverPort) {
        this.socket = socket;
        this.localReceiverPort = localReceiverPort;
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
                byte[] discoveryRequest = ("discovery@"+localReceiverPort).getBytes();
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
                Thread.sleep(WAIT_TIME);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
