package com.example.wirelessfiletransfer.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.example.wirelessfiletransfer.Adapters.DeviceDiscoveryAdapter;
import com.example.wirelessfiletransfer.DiscoveryService;
import com.example.wirelessfiletransfer.DiscoveryUtils;
import com.example.wirelessfiletransfer.Model.Device;
import com.example.wirelessfiletransfer.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DeviceDiscovery extends AppCompatActivity implements DiscoveryUtils {

    @BindView(R.id.discoveryRecyclerView)
    RecyclerView discoveryRecyclerView;

    private ArrayList<Device> devices = new ArrayList<>();
    private DeviceDiscoveryAdapter adapter;

    private DiscoveryService discoveryService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_discovery);
        setUI();
        startDiscovery();
    }

    private void startDiscovery(){
        try {
            if(discoveryService == null)
                discoveryService = new DiscoveryService(this);
            discoveryService.start();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setUI(){
        ButterKnife.bind(this);
        adapter = new DeviceDiscoveryAdapter(devices, this);
        discoveryRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        discoveryRecyclerView.setAdapter(adapter);
    }
    @Override
    public void onDeviceDiscovered(java.lang.String ip, java.lang.String port, java.lang.String info) {
        // Will have to change it to test ip, cause device might change port
        // todo
        final String formattedIP = ip.replaceAll("[^\\d.]", "");
        final String formattedPort = port.replaceAll("[^\\d.]", "");
        Device discoveredDevice = new Device("test", formattedIP, formattedPort, "Available", "additionalInfo");
        if(!(devices.stream().anyMatch(o -> o.getIp().equals(formattedIP)))) {
            devices.add(discoveredDevice);
        }
        runOnUiThread(() -> adapter.notifyDataSetChanged());
    }

    @Override
    public void onDeviceSelected(Device device) {
        if(device.isAvailable()) {
            Intent i = new Intent(DeviceDiscovery.this, FileTransfer.class);
            i.putExtra("device", device);
            startActivity(i);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startDiscovery();
    }

    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("pausing...");
        if(discoveryService != null)
            discoveryService.stop();
    }
}
