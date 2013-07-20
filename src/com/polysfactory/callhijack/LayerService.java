
package com.polysfactory.callhijack;

import java.util.ArrayList;
import java.util.Locale;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.IBinder;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class LayerService extends Service {
    View view;
    WindowManager wm;
    private SpeechRecognizer mSpeechRecognizer;
    private TextView textView;
    private String number;

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

            number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            view = layoutInflater.inflate(R.layout.overlay, null);
            textView = (TextView) view.findViewById(R.id.textView1);
            Button b = (Button) view.findViewById(R.id.button1);
            b.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(L.TAG, "button1 clicked");
                    sendSMS(number, "ima derenaio!");
                }
            });
            Button b2 = (Button) view.findViewById(R.id.button2);
            b2.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(L.TAG, "button2 clicked");
                    startSpeachRecognition();
                }
            });
            wm.addView(view, params);
        } else if (ACTION_REMOVE_VIEW.equals(action)) {
            wm.removeView(view);
        }

        return super.onStartCommand(intent, flags, startId);
    }

    protected void startSpeachRecognition() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.JAPAN.toString());
        mSpeechRecognizer.startListening(intent);
        textView.setText("Preparing...");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mSpeechRecognizer.setRecognitionListener(new HijackRecognitionListener());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSpeechRecognizer.destroy();
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

    class HijackRecognitionListener implements RecognitionListener {

        @Override
        public void onReadyForSpeech(Bundle params) {
            textView.setText("Please speak now");
            Log.d(L.TAG, "speech recognition: ready");
        }

        @Override
        public void onBeginningOfSpeech() {
            textView.setText("Recording...");
            Log.d(L.TAG, "speech recognition: begin");
        }

        @Override
        public void onRmsChanged(float rmsdB) {
        }

        @Override
        public void onBufferReceived(byte[] buffer) {
        }

        @Override
        public void onEndOfSpeech() {
            textView.setText("Recognizing...");
            Log.d(L.TAG, "speech recognition: end");
        }

        @Override
        public void onError(int error) {
            textView.setText("Error: " + error);
            Log.d(L.TAG, "speech recognition error:" + error);
        }

        @Override
        public void onResults(Bundle results) {
            Log.d(L.TAG, "speech recognition: results");
            ArrayList<String> recData = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            if (recData.size() > 0) {
                String message = recData.get(0);
                sendSMS(number, message);
                // textView.setText("Sent: \"" + message + "\"");
                textView.setText("");
                Toast.makeText(LayerService.this, "Message Sent", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onPartialResults(Bundle partialResults) {
            Log.d(L.TAG, "speech recognition: paratial:" + partialResults);
        }

        @Override
        public void onEvent(int eventType, Bundle params) {
            Log.d(L.TAG, "speech recognition event:" + eventType + ":" + params);
        }

    }
}
