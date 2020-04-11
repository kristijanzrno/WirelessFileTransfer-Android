package com.example.wirelessfiletransfer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.wirelessfiletransfer.Adapters.DeviceDiscoveryAdapter;
import com.example.wirelessfiletransfer.Model.Device;

import java.util.ArrayList;

import butterknife.BindView;

public class DeviceDiscovery extends AppCompatActivity implements DiscoveryUtils{

    @BindView(R.id.discoveryRecyclerView)
    RecyclerView discoveryRecyclerView;

    private ArrayList<Device> devices;
    private DeviceDiscoveryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_discovery);
        Discovery discovery = new Discovery(this);
        discovery.execute();
    }

    @Override
    public void onDeviceDiscovered(java.lang.String ip, java.lang.String port, java.lang.String info) {
        // Will have to change it to test ip, cause device might change port
        // todo
        Device discoveredDevice = new Device("test", ip, port, "availableTest", "additionalInfo");
        if(!devices.contains(discoveredDevice))
            devices.add(discoveredDevice);
    }


}
