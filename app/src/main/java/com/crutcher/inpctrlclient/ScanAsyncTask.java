package com.crutcher.inpctrlclient;

import android.arch.lifecycle.Observer;
import android.os.AsyncTask;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;

public class ScanAsyncTask extends AsyncTask<Integer, Integer, InetAddress> {

    Observer<InetAddress> observer;

    public ScanAsyncTask(Observer<InetAddress> observer)
    {
        this.observer = observer;
    }

    private static final byte[] checkBuff = new byte[] {1, 6, 3, 127, -7, 34, 42, 43, 0, 0, 0, 0};

    @Override
    protected InetAddress doInBackground(Integer... integers){

        try {

            DatagramSocket datagramSocket1 = new DatagramSocket(1338);
            datagramSocket1.setBroadcast(true);

            DatagramPacket datagramPacket1 = new DatagramPacket(checkBuff, 12, InetAddress.getByName("255.255.255.255"), 1337);

            datagramSocket1.send(datagramPacket1);
            datagramSocket1.close();


            DatagramSocket datagramSocket = new DatagramSocket(1336);

            DatagramPacket datagramPacket = new DatagramPacket(new byte[12], 12);
            datagramSocket.receive(datagramPacket);

            return  InetAddress.getByAddress(Arrays.copyOfRange(datagramPacket.getData(), 8,12));

        } catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(InetAddress inetAddress) {
        super.onPostExecute(inetAddress);
        observer.onChanged(inetAddress);
    }
}
