
package com.polysfactory.callhijack;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

public class IncomingBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(L.TAG, "flag1");

        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
        Log.d(L.TAG, "state:" + state);
        if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)
                || state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {

            Log.d(L.TAG, "Phone is ringing");

            Intent i = new Intent(context, MainActivity.class);
            String number = intent.getStringExtra(
                    TelephonyManager.EXTRA_INCOMING_NUMBER);
            Log.d(L.TAG, "from:" + number);
            i.putExtras(intent);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            context.startActivity(i);
        }
    }
}
