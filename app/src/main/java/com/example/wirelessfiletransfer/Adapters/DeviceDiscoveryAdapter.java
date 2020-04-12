package com.example.wirelessfiletransfer.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wirelessfiletransfer.Model.Device;
import com.example.wirelessfiletransfer.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DeviceDiscoveryAdapter extends RecyclerView.Adapter<DeviceDiscoveryAdapter.ViewHolder> {

    private ArrayList<Device> devices;

    public DeviceDiscoveryAdapter(ArrayList<Device> devices) {
        this.devices = devices;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.device, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.deviceIP.setText(devices.get(position).getIp());
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.device_ip)
        TextView deviceIP;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
