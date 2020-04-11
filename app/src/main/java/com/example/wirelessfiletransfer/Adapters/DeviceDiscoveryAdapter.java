package com.example.wirelessfiletransfer.Adapters;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wirelessfiletransfer.Model.Device;

import java.util.ArrayList;

public class DeviceDiscoveryAdapter extends RecyclerView.Adapter<DeviceDiscoveryAdapter.ViewHolder> {

    ArrayList<Device> devices;

    public DeviceDiscoveryAdapter(ArrayList<Device> devices) {
        this.devices = devices;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
