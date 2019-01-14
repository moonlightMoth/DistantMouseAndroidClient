package com.crutcher.inpctrlclient;

import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;

public class SocketAsyncTask extends AsyncTask<AppCompatActivity, Void, Object> {


    private BufferedWriter bw;
    public Socket socket;

    @Override
    protected Object doInBackground(AppCompatActivity... a) {
        Log.d("SocketAsyncTask", "attempting to run...");
        try {
            Log.d("SocketAsyncTask", "running...");

            Button scUp = a[0].findViewById(R.id.scrollUp);
            Button scDw = a[0].findViewById(R.id.scrollDown);

            String addr = GlobalVars.getIpAddr();

            socket = new Socket(addr, 1488);

            bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            final Scanner sc = new Scanner(new InputStreamReader(socket.getInputStream()));

            scUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    spitOut("4c");
                }
            });

            scDw.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    spitOut("5c");
                }
            });

            while (true) {
                if (sc.hasNextLine()) {

                    Log.d("answer", sc.nextLine());
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

    public void closeConnection() throws IOException
    {
        spitOut("closeConnection");
        socket.close();
        cancel(true);
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




















