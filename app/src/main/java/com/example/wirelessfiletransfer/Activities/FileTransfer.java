package com.example.wirelessfiletransfer.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wirelessfiletransfer.Constants;
import com.example.wirelessfiletransfer.CustomViews.CardDialog;
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

    @BindView(R.id.file_transfer_emptyState)
    RelativeLayout emptyStateView;

    @BindView(R.id.file_transfer_statusView)
    RelativeLayout statusView;

    @BindView(R.id.file_transfer_connectingSpinKit)
    SpinKitView connectingSpinKit;

    @BindView(R.id.file_transfer_statusText)
    TextView statusTextView;

    private Device device;
    private ConnectionUtils connectionUtils;

    private int noOfFiles = 0;
    private int finished = 0;
    private boolean hadErrors = false;

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

    private void switchView(boolean working){
        runOnUiThread(() -> {
            if(working){
                statusView.setVisibility(View.VISIBLE);
                emptyStateView.setVisibility(View.INVISIBLE);
            }else{
                statusView.setVisibility(View.INVISIBLE);
                emptyStateView.setVisibility(View.VISIBLE);
            }
        });
    }

    private void updateStatus(boolean increase){
        if(increase)
            finished++;
        runOnUiThread(() -> {
            statusTextView.setText(finished + "/" + noOfFiles + " Files Transferred...");
            if(noOfFiles == finished){
                if(!hadErrors)
                    Toast.makeText(FileTransfer.this, "Successfully transferred " + noOfFiles + " files!", Toast.LENGTH_SHORT).show();
                else Toast.makeText(this, "Could not transfer some files...", Toast.LENGTH_SHORT).show();
                noOfFiles = 0;
                finished = 0;
                switchView(false);
            }
        });
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
        runOnUiThread(() -> Toast.makeText(FileTransfer.this, "Connection Refused!", Toast.LENGTH_SHORT).show());
        interruptedConnection(Activity.RESULT_CANCELED);
    }

    @Override
    public void onConnectionTerminated() {
        interruptedConnection(Activity.RESULT_CANCELED);
    }

    @Override
    public void onFileTransferStarted(int noOfFiles) {
        this.hadErrors = false;
        this.noOfFiles = noOfFiles;
        switchView(true);
        updateStatus(false);
    }

    @Override
    public void onFileTransferred() {
        updateStatus(true);
    }

    @Override
    public void onFileTransferFailed(String filename) {
        runOnUiThread(() -> CardDialog.showAlertDialog(FileTransfer.this, "Error", "Could not transfer " + filename));
        this.hadErrors = true;
        updateStatus(true);
    }

    @Override
    public void onReceivingFiles(int noOfFiles) {
        this.noOfFiles = noOfFiles;
        switchView(true);
        updateStatus(false);
    }

    @Override
    public void onFileReceived() {
        updateStatus(true);
    }

    private void transferFiles(ArrayList<Uri> uris){
        assert uris != null : "Invalid files";
        System.out.println("Transferring " + uris.size() + " items");
        onFileTransferStarted(uris.size());
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

    private void interruptedConnection(int resultCode){
        Intent i = new Intent();
        setResult(resultCode, i);
        finish();
    }

    @Override
    public void onBackPressed() {
        connectionUtils.terminateConnection();
        interruptedConnection(Activity.RESULT_OK);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        connectionUtils.terminateConnection();
    }
}
