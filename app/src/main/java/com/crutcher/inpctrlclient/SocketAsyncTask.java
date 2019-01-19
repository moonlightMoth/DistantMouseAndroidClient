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
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;

import static java.lang.StrictMath.abs;

public class SocketAsyncTask extends AsyncTask<AppCompatActivity, Void, Object> {


    private OutputStream os;
    Socket socket;
    private int x;
    private int y;
    private int olderX;
    private int olderY;
    private boolean isClick = false;


    private final byte MV_UP = 1;
    private final byte MV_DW = 2;
    private final byte MV_RT = 3;
    private final byte MV_LT = 4;
    private final byte SC_UP = 5;
    private final byte SC_DW = 6;
    private final byte CL_LB = 7;
    private final byte DR_ON = 8;
    private final byte DR_OF = 9;
    private final byte CL_RB = 10;
    private final byte CL_MB = 11;
    private final byte EXIT_SIGNAL = 127;

    private byte[] byteWrapper = new byte[1];


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

            os = socket.getOutputStream();

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


//            String inputLine;
//
//            while (true) {
//                if ((inputLine = br.readLine()) != null) {
//
//                    Log.d("answer", inputLine);
//                }
//                if (isCancelled())
//                {
//                    return new Object();
//                }
//            }


        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void clickLmb()
    {
        byteWrapper[0] = CL_LB;
        flushWrapper();
    }

    private void scrollUp()
    {
        byteWrapper[0] = SC_UP;
        flushWrapper();
    }

    private void scrollDw()
    {
        byteWrapper[0] = SC_DW;
        flushWrapper();
    }

    private void movePointer() //TODO pointer movement is crutch
    {
        if (x - olderX < 0)
        {
            byteWrapper[0] = MV_LT;

            for (int i = 0; i < abs(x - olderX); i++)
                flushWrapper();
        }

        if (x - olderX > 0)
        {
            byteWrapper[0] = MV_RT;

            for (int i = 0; i < x - olderX; i++)
                flushWrapper();
        }

        if (y - olderY < 0)
        {
            byteWrapper[0] = MV_UP;

            for (int i = 0; i < abs(y - olderY); i++)
                flushWrapper();
        }

        if (y - olderY > 0)
        {
            byteWrapper[0] = MV_DW;

            for (int i = 0; i < y - olderY; i++)
                flushWrapper();
        }


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

    private void spitOut(byte[] bytes)
    {
        try {
            int SDK_INT = android.os.Build.VERSION.SDK_INT;

            if (SDK_INT > 8)
            {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                        .permitAll().build();
                StrictMode.setThreadPolicy(policy);
            }

            os.write(bytes);
            os.flush();

        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void flushWrapper()
    {
        spitOut(byteWrapper);
    }

    void closeConnection() throws IOException
    {
        byteWrapper[0] = EXIT_SIGNAL;
        flushWrapper();

        socket.shutdownInput();
        socket.shutdownOutput();
        socket.close();
        cancel(true);
    }



}




















