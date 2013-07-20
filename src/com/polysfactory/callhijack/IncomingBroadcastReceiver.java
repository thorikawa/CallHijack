
package com.polysfactory.callhijack;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

public class IncomingBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
        Log.d(L.TAG, "state:" + state);
        if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
            Intent i = new Intent(context, LayerService.class);
            i.setAction(LayerService.ACTION_SHOW_VIEW);
            i.putExtras(intent);
            context.startService(i);
        } else if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
            Intent i = new Intent(context, LayerService.class);
            i.setAction(LayerService.ACTION_REMOVE_VIEW);
            context.startService(i);
        }
    }
}
