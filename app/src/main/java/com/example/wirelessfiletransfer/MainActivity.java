package com.example.wirelessfiletransfer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
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
    }


    public void connect(View v){
        connectionHandler = new ConnectionHandler(desktopIP.getText().toString(), 9270);
        connectionHandler.execute();
        if(connectionHandler != null)
            conversation.append("Connected");
        else
            conversation.append("Could not connect to the desktop app");
    }

    public void send(View v){
        if(connectionHandler != null) {
            String message = messageText.getText().toString();
            connectionHandler.send(message);
            conversation.append(message);
        }

    }
}
