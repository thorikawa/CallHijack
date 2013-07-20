
package com.polysfactory.callhijack;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;

public class LayerService extends Service {
    View view;
    WindowManager wm;

    public static final String ACTION_SHOW_VIEW = "show_view";

    public static final String ACTION_REMOVE_VIEW = "remove_view";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String action = intent.getAction();
        Log.d(L.TAG, "action:" + action);

        if (ACTION_SHOW_VIEW.equals(action)) {
            LayoutInflater layoutInflater = LayoutInflater.from(this);

            WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    // TYPE_SYSTEM_OVERLAY does not accept touch event on my
                    // Galaxy S3
                    // c.f.
                    // <http://stackoverflow.com/questions/4481226/creating-a-system-overlay-always-on-top-button-in-android>
                    // WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                    WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                    WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                    PixelFormat.TRANSLUCENT);

            wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

            final String number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            view = layoutInflater.inflate(R.layout.overlay, null);
            Button b = (Button) view.findViewById(R.id.button1);
            b.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(L.TAG, "button clicked");
                    sendSMS(number, "ima derenaio!");
                }
            });
            wm.addView(view, params);
        } else if (ACTION_REMOVE_VIEW.equals(action)) {
            wm.removeView(view);
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        wm.removeView(view);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void sendSMS(String phoneNumber, String message) {
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, null, null);
    }
}
