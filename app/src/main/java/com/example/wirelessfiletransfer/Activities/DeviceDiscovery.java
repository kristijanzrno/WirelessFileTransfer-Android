package com.example.wirelessfiletransfer.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.wirelessfiletransfer.Adapters.DeviceDiscoveryAdapter;
import com.example.wirelessfiletransfer.DiscoveryService;
import com.example.wirelessfiletransfer.DiscoveryUtils;
import com.example.wirelessfiletransfer.Model.Device;
import com.example.wirelessfiletransfer.R;
import com.skyfishjy.library.RippleBackground;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DeviceDiscovery extends AppCompatActivity implements DiscoveryUtils {

    @BindView(R.id.discoveryRecyclerView)
    RecyclerView discoveryRecyclerView;
    @BindView(R.id.discovery_scan_icon)
    RippleBackground discoveryScanIcon;

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
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        ButterKnife.bind(this);
        adapter = new DeviceDiscoveryAdapter(devices, this);
        discoveryRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        discoveryRecyclerView.setAdapter(adapter);
    }
    @Override
    public void onDeviceDiscovered(java.lang.String ip, int port, java.lang.String info) {
        Device discoveredDevice = new Device("test", ip, port, "Available", "additionalInfo");
        Device existingDevice = devices.stream().filter(o -> o.getIp().equals(ip)).findFirst().orElse(null);
        if(existingDevice == null){
            devices.add(discoveredDevice);
        }else{
            existingDevice.setDiscovered(0);
        }
        runOnUiThread(() -> adapter.notifyDataSetChanged());
    }

    @Override
    public void onNetworkScanned() {
        // Checking for disconnected devices
        for(int i = devices.size()-1; i>=0; i--){
            devices.get(i).setDiscovered(devices.get(i).getDiscovered()+1);
            if(devices.get(i).getDiscovered() == 3)
                devices.remove(i);
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
        discoveryScanIcon.startRippleAnimation();
    }

    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("pausing...");
        if(discoveryService != null)
            discoveryService.stop();
        discoveryScanIcon.stopRippleAnimation();
    }
}
