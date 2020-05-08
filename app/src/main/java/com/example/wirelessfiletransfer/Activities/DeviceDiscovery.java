package com.example.wirelessfiletransfer.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.wirelessfiletransfer.Adapters.DeviceDiscoveryAdapter;
import com.example.wirelessfiletransfer.Constants;
import com.example.wirelessfiletransfer.CustomViews.CardDialog;
import com.example.wirelessfiletransfer.Model.Message;
import com.example.wirelessfiletransfer.Network.DiscoveryService;
import com.example.wirelessfiletransfer.DiscoveryUtils;
import com.example.wirelessfiletransfer.Model.Device;
import com.example.wirelessfiletransfer.R;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.skyfishjy.library.RippleBackground;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            //todo
        }

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

    @OnClick(R.id.settingsButton)
    public void openSettings(View v){
        Intent i = new Intent(this, Settings.class);
        startActivity(i);
    }


    @OnClick(R.id.qrButton)
    public void openQRScanner(View v){
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setCameraId(0);
        integrator.setBeepEnabled(false);
        integrator.setBarcodeImageEnabled(false);
        integrator.initiateScan();
    }

    @Override
    public void onDeviceDiscovered(Device device) {
        Device existingDevice = devices.stream().filter(o -> o.getIp().equals(device.getIp())).findFirst().orElse(null);
        if(existingDevice == null){
            devices.add(device);
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
            startActivityForResult(i, Constants.FILE_TRANSFER_ACTIVITY_REQUEST);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Constants.FILE_TRANSFER_ACTIVITY_REQUEST && resultCode == RESULT_CANCELED) {
            CardDialog.showAlertDialog(DeviceDiscovery.this, "Lost Connection", "The connection between devices has been interrupted.");
        }else if(resultCode == RESULT_OK){
            Toast.makeText(this, "result!=null", Toast.LENGTH_SHORT).show();
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if(result != null && result.getContents() != null){
                Toast.makeText(this, result.getContents(), Toast.LENGTH_SHORT).show();
                System.out.println(result.getContents());
                String response[] = Message.decodeMessage(result.getContents());
                Device device = new Device(response[0], response[1], Integer.parseInt(response[2]), response[3], response[4]);
                onDeviceSelected(device);
            }
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
