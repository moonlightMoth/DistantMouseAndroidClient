package com.crutcher.inpctrlclient;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.IOException;
import java.util.ArrayList;

public class ControlPanelActivity extends AppCompatActivity {

    final ArrayList<SocketAsyncTask> asyncTaskArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        final AppCompatActivity this_ = this;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control_panel);


        Button rec = findViewById(R.id.reconnect);
        rec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCompatActivity[] a = new AppCompatActivity[1];
                a[0] = this_;
                SocketAsyncTask socketAsyncTask = new SocketAsyncTask();
                asyncTaskArrayList.add(socketAsyncTask);
                Log.d("Activity", "executing socketTask...");
                socketAsyncTask.execute(a);
            }
        });

    }

    @Override
    protected void onDestroy() {
        Log.d("Activity", "OnDestroy");
        Log.d("OnDestroy", Integer.toString(asyncTaskArrayList.size()));
        for (int i = 0; i < asyncTaskArrayList.size(); i++) {
            try {
                Log.d("SocketAsyncTask", "closing socket...");
                asyncTaskArrayList.get(i).closeConnection();
                asyncTaskArrayList.get(i).socket.close();
                if (asyncTaskArrayList.get(i).socket.isClosed())
                    Log.d("SocketAsyncTask", "socket closed");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.onDestroy();
    }
}
