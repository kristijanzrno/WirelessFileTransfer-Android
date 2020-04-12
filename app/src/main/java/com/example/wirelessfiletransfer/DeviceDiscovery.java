package com.example.wirelessfiletransfer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.AsyncTask;
import android.os.Bundle;

import com.example.wirelessfiletransfer.Adapters.DeviceDiscoveryAdapter;
import com.example.wirelessfiletransfer.Model.Device;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DeviceDiscovery extends AppCompatActivity implements DiscoveryUtils{

    @BindView(R.id.discoveryRecyclerView)
    RecyclerView discoveryRecyclerView;

    private ArrayList<Device> devices = new ArrayList<>();
    private DeviceDiscoveryAdapter adapter;

    private SignalBroadcaster broadcaster;
    private SignalReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_discovery);
        setUI();
        setupDiscovery();
    }

    private void setupDiscovery(){
        try {
            DiscoveryService discoveryService = new DiscoveryService(this);
            discoveryService.start();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void setUI(){
        ButterKnife.bind(this);
        adapter = new DeviceDiscoveryAdapter(devices);
        discoveryRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        discoveryRecyclerView.setAdapter(adapter);
    }
    @Override
    public void onDeviceDiscovered(java.lang.String ip, java.lang.String port, java.lang.String info) {
        // Will have to change it to test ip, cause device might change port
        // todo
        Device discoveredDevice = new Device("test", ip, port, "availableTest", "additionalInfo");
        if(!(devices.stream().anyMatch(o -> o.getIp().equals(ip)))) {
            devices.add(discoveredDevice);
        }
        runOnUiThread(() -> adapter.notifyDataSetChanged());
    }


}
