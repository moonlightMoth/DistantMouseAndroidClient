package com.crutcher.inpctrlclient;

public class GlobalVars
{
    private static String IpAddr = "192.168.0.";

    static void setIpAddr(String ipAddr) {
        IpAddr = ipAddr;
    }

    static String getIpAddr() {
        return IpAddr;
    }
}
