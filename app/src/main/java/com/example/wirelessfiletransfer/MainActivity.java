package com.example.wirelessfiletransfer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.wirelessfiletransfer.Model.Device;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements SendToActivity {
   @BindView(R.id.conversation)
   EditText conversation;
   @BindView(R.id.serverIP)
   EditText desktopIP;
   @BindView(R.id.message)
   EditText messageText;

   ConnectionHandler connectionHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Device device = (Device) getIntent().getSerializableExtra("device");

        desktopIP.setText(device.getIp());
        establishConnection(device.getIp(), device.getPort());
    }


    public void connect(View v){
        //establishConnection();
    }

    public void establishConnection(String ip, int port){
        connectionHandler = new ConnectionHandler(desktopIP.getText().toString(), 9270, this);
        connectionHandler.execute();
    }

    public void send(View v){
        if(connectionHandler != null) {
            String message = messageText.getText().toString();
            connectionHandler.send(message);
            conversation.append("\n" + message);
            messageText.setText("");
        }

    }

    @Override
    public void sendMessage(String message){
        conversation.append("\n" + message);
    }

}
