package com.example.wirelessfiletransfer;

import android.os.AsyncTask;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class Discovery extends AsyncTask<String, String, String> {

    DiscoveryUtils discoveryUtils;

    public Discovery(DiscoveryUtils discoveryUtils){
        this.discoveryUtils = discoveryUtils;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            System.out.println("here");
            DatagramSocket socket = new DatagramSocket();
            socket.setBroadcast(true);
            byte[] discoveryRequest = "discovery".getBytes();
            DatagramPacket packet = new DatagramPacket(discoveryRequest, discoveryRequest.length, InetAddress.getByName("255.255.255.255"), 8888);
            socket.send(packet);
            try {
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
                            DatagramPacket sendData = new DatagramPacket(discoveryRequest, discoveryRequest.length, InetAddress.getByName("255.255.255.255"), 8888);
                            socket.send(sendData);
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                        System.out.println(getClass().getName() + ">>> Request packet sent to: " + broadcast.getHostAddress() + "; Interface: " + networkInterface.getDisplayName());
                    }
                }
                System.out.println(getClass().getName() + ">>> Done looping over all network interfaces. Now waiting for a reply!");
                while(true) {
                    byte[] receiveBuffer = new byte[15000];
                    DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                    socket.receive(receivePacket);
                    System.out.println("response: \n");
                    String message = new String(receivePacket.getData()).trim();
                    if (message.startsWith("disc_")) {
                        message = message.replace("disc_", "");
                        String ip = receivePacket.getSocketAddress().toString();
                        String port = message.split("::")[1];
                        System.out.println("IP: " + ip + "\n" + "Port: " + port);
                        discoveryUtils.onDeviceDiscovered(ip, port, message);
                    }
                }
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("here2");
        return null;
    }


    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }



}
