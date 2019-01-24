package com.crutcher.inpctrlclient;

import android.os.StrictMode;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;


class SocketProcessor extends Thread
{

    private OutputStream os;
    Socket socket;

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
    private final byte MV_XY = 12;
    private final byte EXIT_SIGNAL = 127;

    private byte[] byteWrapper = new byte[3];

    @Override
    public synchronized void start()
    {
        super.start();

        StrictMode.ThreadPolicy policy =
                new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    @Override
    public synchronized void run()
    {
        Log.d("SocketAsyncTask", "attempting to run...");

        try
        {
            Log.d("SocketAsyncTask", "running...");

            String addr = GlobalVars.getIpAddr();

            socket = new Socket(addr, 1488);

            os = socket.getOutputStream();

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }


    }

    void clickLmb()
    {
        byteWrapper[0] = CL_LB;
        flushWrapper();
    }

    void clickRmb()
    {
        byteWrapper[0] = CL_RB;
        flushWrapper();
    }

    void scrollUp()
    {
        byteWrapper[0] = SC_UP;
        flushWrapper();
    }

    void scrollDw()
    {
        byteWrapper[0] = SC_DW;
        flushWrapper();
    }

    void dragOn()
    {
        byteWrapper[0] = DR_ON;
        flushWrapper();
    }

    void dragOff()
    {
        byteWrapper[0] = DR_OF;
        flushWrapper();
    }

    synchronized void movePointer(byte deltaXByte, byte deltaYByte)
    {
        byteWrapper[0] = MV_XY;
        byteWrapper[1] = deltaXByte;
        byteWrapper[2] = deltaYByte;
        flushWrapper();
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

    private synchronized void spitOut(byte[] bytes)
    {
        try
        {
            os.write(bytes);
            os.flush();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private synchronized void flushWrapper()
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
    }
}










