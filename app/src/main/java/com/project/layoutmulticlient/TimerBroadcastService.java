package com.project.layoutmulticlient;

import android.app.Service;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Sourabh on 30-Apr-16.
 */
public class TimerBroadcastService extends Service {

    private final static String TAG = TimerBroadcastService.class.getSimpleName();
    private long timer;
    public static final String COUNTDOWN_BR = "countdown_broadcast";
    Intent broadcastIntent = new Intent(COUNTDOWN_BR);

    CountDownTimer countDownTimer = null;

    @Override
    public void onCreate() {
        super.onCreate();

        Log.i(TAG, "Starting timer...");
        timer = ContestRules.timer;
        countDownTimer = new CountDownTimer(timer, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                broadcastIntent.putExtra("countdown", millisUntilFinished);
                sendBroadcast(broadcastIntent);
                Log.i(TAG, "Countdown seconds remaining: " + millisUntilFinished / 1000);
            }
            @Override
            public void onFinish() {
                broadcastIntent.putExtra("countdown", 0L);
                sendBroadcast(broadcastIntent);
                Log.i(TAG, "Timer finished");
            }
        };

        countDownTimer.start();
    }

    @Override
    public void onDestroy() {

        countDownTimer.cancel();
        Log.i(TAG, "Timer cancelled");
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
