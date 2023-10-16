package com.crutcher.inpctrlclient;

import static android.support.constraint.Constraints.TAG;

import android.arch.lifecycle.Observer;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Enumeration;

public class ScanAsyncTask extends AsyncTask<Integer, Integer, InetAddress> {

    Observer<InetAddress> observer;

    public ScanAsyncTask(Observer<InetAddress> observer)
    {
        this.observer = inetAddress -> {
            observer.onChanged(inetAddress);
            inSocket.close();
            outSocket.close();
        };
    }

    private static final byte[] checkBuff = new byte[] {1, 6, 3, 127, -7, 34, 42, 43, 0, 0, 0, 0};

    private DatagramSocket inSocket = null;
    private DatagramSocket outSocket = null;

    @Override
    protected InetAddress doInBackground(Integer... integers){

        Log.d(TAG, "doInBackground: started method");

        Enumeration<NetworkInterface> interfaces = null;
        try {

            outSocket = new DatagramSocket(1338);
            inSocket = new DatagramSocket(1336);
            inSocket.setSoTimeout(15000);
            outSocket.setBroadcast(true);

            interfaces = NetworkInterface.getNetworkInterfaces();

        while(interfaces.hasMoreElements()) {
            NetworkInterface networkInterface = interfaces.nextElement();

            if (networkInterface.isLoopback())
                continue;

            for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses())
            {
                InetAddress broadcast = interfaceAddress.getBroadcast();

                Log.d("ScanAsyncTask", "doInBackground: " + interfaceAddress.getAddress());

                if (broadcast == null)
                    continue;

                SendAsyncTask receiveAsyncTask = new SendAsyncTask();
                receiveAsyncTask.execute(broadcast);
            }
        }
        } catch (SocketException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(InetAddress inetAddress) {
        super.onPostExecute(inetAddress);
    }

    class SendAsyncTask extends AsyncTask<InetAddress, Integer, InetAddress>
    {

        @Override
        protected InetAddress doInBackground(InetAddress... broadcastAddress) {

            Log.d(TAG, "doInBackground ReceiveAsyncTask: sending to " + broadcastAddress[0].getHostAddress());

            try {
                DatagramPacket outPacket = new DatagramPacket(checkBuff, 12, broadcastAddress[0], 1337);

                outSocket.send(outPacket);

                DatagramPacket inPacket = new DatagramPacket(new byte[12], 12);
                inSocket.receive(inPacket);


                return InetAddress.getByAddress(Arrays.copyOfRange(inPacket.getData(), 8,12));

            } catch (IOException e) {
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
}
