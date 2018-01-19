package org.dualcom.hackprofkom.MyClass;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class isInternet {

    private static String HOST = "http://july.zadko.siteme.org/"; //"http://rapoo.mysit.ru/";
    public static String API = HOST+"hack?module=";

    public static Boolean active(Context context)
    {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (wifiInfo != null && wifiInfo.isConnected())
        {
            return true;
        }
        wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifiInfo != null && wifiInfo.isConnected())
        {
            return true;
        }
        wifiInfo = cm.getActiveNetworkInfo();
        if (wifiInfo != null && wifiInfo.isConnected())
        {
            return true;
        }
        return false;
    }

}
