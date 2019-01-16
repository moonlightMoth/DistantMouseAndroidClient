package com.crutcher.inpctrlclient;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.net.Socket;
import java.util.ArrayList;

public class ControlPanelActivity extends AppCompatActivity {

    final SocketAsyncTask[] socket = new SocketAsyncTask[1];
    boolean isConnected = false;
    final AppCompatActivity this_ = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control_panel);

        final Button setIP = findViewById(R.id.bSetIP);
        final Button rec = findViewById(R.id.reconnect);
        final Button disc = findViewById(R.id.disc);
        final EditText etSetIP = findViewById(R.id.etSetIP);

        disc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isConnected)
                    disconnect();
            }
        });

        setIP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GlobalVars.setIpAddr(etSetIP.getText().toString());
            }
        });

        rec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isConnected)
                    connect();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        final EditText etSetIP = findViewById(R.id.etSetIP);
        etSetIP.setText(GlobalVars.getIpAddr());

    }

    @Override
    protected void onDestroy() {
        Log.d("Activity", "OnDestroy");

        disconnect();

        super.onDestroy();
    }

    private void connect()
    {
        isConnected = true;
        AppCompatActivity[] a = new AppCompatActivity[1];
        a[0] = this_;
        SocketAsyncTask socketAsyncTask = new SocketAsyncTask();
        socket[0] = socketAsyncTask;
        Log.d("Activity", "executing socketTask...");
        socketAsyncTask.execute(a);
    }

    private void disconnect()
    {
        isConnected = false;
        Log.d("disconnect", "processing");
            try {
                Log.d("SocketAsyncTask", "closing socket...");
                socket[0].closeConnection();
                if (socket[0].socket.isClosed())
                    Log.d("SocketAsyncTask", "socket closed");
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
    }
}
