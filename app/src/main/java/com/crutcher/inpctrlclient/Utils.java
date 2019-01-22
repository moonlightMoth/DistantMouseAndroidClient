package com.crutcher.inpctrlclient;

final class Utils
{
    static byte intToByte(int i)
    {
        if (i == 0)
            return 0;

        if (i > 0 && i < 128 || i < 0 && i > -129)
            return (byte)i;

        if (i < -128)
            return -128;

        return 127;
    }
}
