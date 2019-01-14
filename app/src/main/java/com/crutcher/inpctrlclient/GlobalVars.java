package com.crutcher.inpctrlclient;

public class GlobalVars
{
    private static String IpAddr = "192.168.0.42";

    public static void setIpAddr(String ipAddr) {
        IpAddr = ipAddr;
    }

    public static String getIpAddr() {
        return IpAddr;
    }
}
