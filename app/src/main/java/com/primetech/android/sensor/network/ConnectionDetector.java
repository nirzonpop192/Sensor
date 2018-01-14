package com.primetech.android.sensor.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;

/**
 * Created by Faisal on 1/14/2018.
 */

public class ConnectionDetector {

   public static boolean  isNetworkConnected(Context _context) {

        ConnectivityManager cm = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm != null) {
            /*
             * upper version checker
             */
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Network[] networks = cm.getAllNetworks();
                if (networks != null) {
                    for (Network net : networks) {
                        NetworkInfo info = cm.getNetworkInfo(net);
                        if (info.getState() == NetworkInfo.State.CONNECTED) {
                            return true;
                        }

                    }
                } else {
                    // no network found
                }
            } else {
                /*
                 * older version less than
                 */

                NetworkInfo[] infos = cm.getAllNetworkInfo();
                if (infos != null) {
                    for (NetworkInfo info : infos) {
                        if (info.getState() == NetworkInfo.State.CONNECTED){
                            return true;
                        }
                    }
                }else {
                    // no network found
                }
            }


        }
        return false;
    }
}
