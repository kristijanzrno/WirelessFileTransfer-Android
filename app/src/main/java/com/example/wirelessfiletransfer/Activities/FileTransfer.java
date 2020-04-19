package com.example.wirelessfiletransfer.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wirelessfiletransfer.Constants;
import com.example.wirelessfiletransfer.Model.Device;
import com.example.wirelessfiletransfer.R;
import com.example.wirelessfiletransfer.SendToActivity;
import com.example.wirelessfiletransfer.Utils.ConnectionUtils;
import com.github.ybq.android.spinkit.SpinKitView;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.BindView;
import butterknife.OnClick;

public class FileTransfer extends AppCompatActivity implements SendToActivity {

    @BindView(R.id.file_transfer_device)
    TextView deviceName;

    @BindView(R.id.file_transfer_connectingOverlay)
    RelativeLayout connectingOverlay;

    @BindView(R.id.file_transfer_rootView)
    RelativeLayout rootView;

    @BindView(R.id.file_transfer_connectingSpinKit)
    SpinKitView connectingSpinKit;

    private Device device;
    private ConnectionUtils connectionUtils;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_transfer);
        device = (Device) getIntent().getSerializableExtra("device");
        assert device != null : "Invalid Device";
        setUI();
        // Establish connection
        connectionUtils = new ConnectionUtils(device, this);
        connectionUtils.connect();
    }


    @OnClick(R.id.file_transfer_select)
    public void selectFiles(){
        Intent filePicker = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        filePicker.addCategory(Intent.CATEGORY_OPENABLE);
        filePicker.setType("*/*");
        filePicker.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        filePicker.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        startActivityForResult(filePicker, Constants.FILE_PICKER_REQUEST);
    }

    private void setUI(){
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        ButterKnife.bind(this);
        rootView.bringChildToFront(connectingOverlay);
        deviceName.setText(device.getName());

    }

    @Override
    public void sendMessage(String message) {

    }

    @Override
    public void onDeviceConnected() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(FileTransfer.this, "Connected!", Toast.LENGTH_SHORT).show();
                rootView.removeView(connectingOverlay);
            }
        });
    }

    @Override
    public void onConnectionRefused() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(FileTransfer.this, "Connection Refused!", Toast.LENGTH_SHORT).show();
            }
        });
        finish();
    }

    @Override
    public void onConnectionTerminated() {

    }

    @Override
    public void onReceivingFiles(int noOfFiles) {

    }

    @Override
    public void onFileReceived(String file) {

    }

    private void transferFiles(ArrayList<Uri> uris){
        assert uris != null : "Invalid files";
        System.out.println("Transferring " + uris.size() + " items");
        connectionUtils.transferFiles(uris);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && data != null){
            switch(requestCode){
                case Constants.FILE_PICKER_REQUEST:
                    ArrayList<Uri> uris = new ArrayList<>();
                    ClipData selectedData = data.getClipData();
                    if(selectedData != null){
                        for(int i = 0; i<selectedData.getItemCount(); i++){
                            ClipData.Item item = selectedData.getItemAt(i);
                            uris.add(item.getUri());
                        }
                    }else{
                        Uri uri = data.getData();
                        if(uri != null) {
                            uris.add(uri);
                        }
                    }
                    if(!uris.isEmpty())
                        transferFiles(uris);

                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        connectionUtils.terminateConnection();
    }
}
