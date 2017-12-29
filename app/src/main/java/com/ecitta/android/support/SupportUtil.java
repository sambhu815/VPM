package com.ecitta.android.support;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;


/**
 * Created by parth.lad on 5/4/2016.
 */
public class SupportUtil {
    private Context mContext;

    public SupportUtil(Context mContext) {
        this.mContext = mContext;
    }

    /*
    * validate email address
    *
    * */
    public boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    /*
    * validate phone no*/

    public boolean isValidMobile(String phone) {
        return !TextUtils.isEmpty(phone) && android.util.Patterns.PHONE.matcher(phone).matches();
    }

    /*
     * Checking Internet connection
	 */
    public boolean checkInternetConnectivity() {
        ConnectivityManager connectivity = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
