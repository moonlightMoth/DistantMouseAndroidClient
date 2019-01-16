package com.crutcher.inpctrlclient;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class SocketAsyncTask extends AsyncTask<AppCompatActivity, Void, Object> {


    private BufferedWriter bw;
    public Socket socket;

    @SuppressLint("WrongThread")
    @Override
    protected Object doInBackground(AppCompatActivity... a) {
        Log.d("SocketAsyncTask", "attempting to run...");
        try {
            Log.d("SocketAsyncTask", "running...");

            final Button scUp = a[0].findViewById(R.id.scrollUp);
            final Button scDw = a[0].findViewById(R.id.scrollDown);
            final View mvPane = a[0].findViewById(R.id.mvPane);

            String addr = GlobalVars.getIpAddr();

            socket = new Socket(addr, 1488);

            bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            final BufferedReader br =
                    new BufferedReader(new InputStreamReader(socket.getInputStream()));

            scUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    scrollUp();
                }
            });

            scDw.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    scrollDw();
                }
            });

            mvPane.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickLmb();
                }
            });

            String inputLine;

            while (true) {
                if ((inputLine = br.readLine()) != null) {

                    Log.d("answer", inputLine);
                }
                if (isCancelled())
                {
                    return new Object();
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    void closeConnection() throws IOException
    {
        spitOut("closeConnection");
        socket.shutdownInput();
        socket.shutdownOutput();
        socket.close();
        cancel(true);
    }

    private void clickLmb()
    {
        spitOut("1c");
    }

    private void scrollUp()
    {
        spitOut("4c");
    }

    private void scrollDw()
    {
        spitOut("5c");
    }

    private void spitOut(String str)
    {
        try {
            int SDK_INT = android.os.Build.VERSION.SDK_INT;

            if (SDK_INT > 8)
            {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                        .permitAll().build();
                StrictMode.setThreadPolicy(policy);
            }

            Log.d("listener", str);
            bw.write(str);
            bw.newLine();
            bw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}




















