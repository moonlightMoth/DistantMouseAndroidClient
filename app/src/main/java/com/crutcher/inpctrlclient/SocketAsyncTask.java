package com.crutcher.inpctrlclient;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import static java.lang.StrictMath.abs;

public class SocketAsyncTask extends AsyncTask<AppCompatActivity, Void, Object> {


    private BufferedWriter bw;
    Socket socket;
    private int x;
    private int y;
    private int olderX;
    private int olderY;
    private boolean isClick = false;

    @SuppressLint("WrongThread")
    @Override
    protected Object doInBackground(AppCompatActivity... a) {
        Log.d("SocketAsyncTask", "attempting to run...");
        try {
            Log.d("SocketAsyncTask", "running...");

            final Button scUp = a[0].findViewById(R.id.scrollUp);
            final Button scDw = a[0].findViewById(R.id.scrollDown);
            final Button clickButton = a[0].findViewById(R.id.clickButton);
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
            clickButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickLmb();
                }
            });

            mvPane.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {

                    int action = motionEvent.getAction();
                    x = (int) motionEvent.getX();
                    y = (int) motionEvent.getY();

                    if (action == MotionEvent.ACTION_DOWN)
                    {
                        olderX = x;
                        olderY = y;
                        //createClickArea();
                    }

//                    if (action == MotionEvent.ACTION_UP && doesPointerBelongClickArea())
//                        clickLmb();

                    if (action == MotionEvent.ACTION_MOVE)
                    {
                        isClick = false;

                        movePointer();

                        olderX = x;
                        olderY = y;
                    }

                    return true;
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

    private void movePointer() //TODO pointer movement is crutch
    {
        if (x - olderX < 0)
            for (int i = 0; i < abs(x - olderX); i++)
                spitOut("-h");

        if (x - olderX > 0)
            for (int i = 0; i < x - olderX; i++)
                spitOut("+h");

        if (y - olderY < 0)
            for (int i = 0; i < abs(y - olderY); i++)
                spitOut("+v");

        if (y - olderY > 0)
            for (int i = 0; i < y - olderY; i++)
                spitOut("-v");

//        spitOut("mv " + Integer.toString(x - olderX) + " "
//                + Integer.toString(y - olderY));
    }

    private void createClickArea()
    {
        //TODO for appropriate click
    }

    private boolean doesPointerBelongClickArea()
    {
        //TODO for appropriate click too
        return false;
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

    void closeConnection() throws IOException
    {
        spitOut("closeConnection");
        socket.shutdownInput();
        socket.shutdownOutput();
        socket.close();
        cancel(true);
    }



}




















