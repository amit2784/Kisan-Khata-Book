package com.ask2784.kisankhatabook;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkCheck {
    private Context _context;

    public NetworkCheck(Context context) {
        this._context = context;
    }

    public boolean noNetwork() {

        ConnectivityManager connectivityManager = (ConnectivityManager)
                _context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {

            NetworkInfo[] networkInfos = connectivityManager.getAllNetworkInfo();
            if (networkInfos != null)
                for (int i = 0; i < networkInfos.length; i++)
                    if (networkInfos[i].getState() == NetworkInfo.State.CONNECTED) {
                        return false;
                    }
        }
        return true;
    }

}
