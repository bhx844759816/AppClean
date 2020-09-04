package com.appclean.main.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInstaller;
import android.util.Log;

/**
 * 卸载的广播监听
 */
public class UnInstallResultReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            final int status = intent.getIntExtra(
                    PackageInstaller.EXTRA_STATUS,
                    PackageInstaller.STATUS_FAILURE);
            String msg = null;
            Log.d("TAG", " status = " + status);
            if (status == PackageInstaller.STATUS_SUCCESS) {
                // success
                Log.d("TAG", " Success!");
            } else {
                msg = intent
                        .getStringExtra(PackageInstaller.EXTRA_STATUS_MESSAGE);

                Log.e("TAG",
                        "AbsAppReceiver FAILURE status_massage" + msg);
            }
        }
    }
}
