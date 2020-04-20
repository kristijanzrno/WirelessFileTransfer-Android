package com.example.wirelessfiletransfer.Network;


import android.os.AsyncTask;

import com.example.wirelessfiletransfer.DiscoveryUtils;

import java.net.DatagramSocket;

public class DiscoveryService {

    private DiscoveryUtils discoveryUtils;
    private DatagramSocket broadcastSocket, receiveSocket;
    private SignalBroadcaster broadcaster;
    private SignalReceiver receiver;

    public DiscoveryService(DiscoveryUtils discoveryUtils) throws Exception{
        this.discoveryUtils = discoveryUtils;
        broadcastSocket = new DatagramSocket();
        receiveSocket = new DatagramSocket();
    }

    public void start() throws Exception{
        if(broadcaster == null && receiver == null) {
            broadcaster = new SignalBroadcaster(broadcastSocket, receiveSocket.getLocalPort(), discoveryUtils);
            receiver = new SignalReceiver(discoveryUtils, receiveSocket);
            broadcaster.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
            receiver.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
        }
    }

    public void stop(){
        if(!receiver.isCancelled())
            receiver.cancel(false);
        if(!broadcaster.isCancelled())
            broadcaster.cancel(false);

        broadcaster = null;
        receiver = null;
    }

    //todo methods to stop/restart

}
