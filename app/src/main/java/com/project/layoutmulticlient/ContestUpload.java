package com.project.layoutmulticlient;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class ContestUpload extends Activity {

    public final String TAG = ContestUpload.class.getSimpleName();
    private Uri uri;
    private ArrayList<Question> questionArrayList = new ArrayList<Question>();
    EditText timerHours, timerMins;
    Button addQuesBtn, sendBtn;
    Intent timerIntent;
    String strHrs, strMins;
    int tHrs, tMins;
    public static long millSecs;
    long mins, hours;
    boolean timerStarted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contest_upload);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        sendBtn = (Button) findViewById(R.id.sendQuesTimerButton);
        addQuesBtn = (Button) findViewById(R.id.add_ques_btn);
        sendBtn.setVisibility(View.GONE);

        timerHours = (EditText) findViewById(R.id.timerHours);
        timerMins = (EditText) findViewById(R.id.timerMins);
    }

    public void addQues(View view) {
        Intent chooseExcelIntent = new Intent(Intent.ACTION_GET_CONTENT);
        chooseExcelIntent.setType("application/vnd.ms-excel");
        startActivityForResult(chooseExcelIntent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "Result");

        if (resultCode == RESULT_OK && requestCode == 1) {
            if (data == null) {
                Toast.makeText(this, "No file found!", Toast.LENGTH_SHORT).show();
            } else {
                uri = data.getData();
                Log.i(TAG, "Excel URI: " + uri);
                Toast.makeText(this, uri.toString(), Toast.LENGTH_SHORT).show();
                questionArrayList = ReadExcelFile.readExcelFile(uri);
                sendBtn.setVisibility(View.VISIBLE);
            }
        }
        else if (resultCode != RESULT_CANCELED) {
            Toast.makeText(this, "There was an error reading the file!", Toast.LENGTH_SHORT).show();
        }
    }

    public void sendQues(View view) {
        strHrs = timerHours.getText().toString();
        strMins = timerMins.getText().toString();

        if(strHrs.equals("") || strMins.equals(""))
            Toast.makeText(this, "Timer not set", Toast.LENGTH_SHORT).show();
        else {
            tHrs = Integer.parseInt(strHrs);
            tMins = Integer.parseInt(strMins);

            if (tMins >= 60 || tMins < 0) {
                Toast.makeText(this, "Minutes must be within 0 to 59!", Toast.LENGTH_SHORT).show();
            } else {
                if(!timerStarted) {
                    timerIntent = new Intent(this, TimerBroadcastService.class);
                    startService(timerIntent);
                    timerStarted = true;
                    Log.i(TAG, "Started service");
                }
                millSecs = (tHrs * 60 + tMins) * 60000;
                NsdChatActivity.mConnection.sendAllMessage("timer", String.valueOf(millSecs));
                NsdChatActivity.mConnection.sendAllMessage("ques", questionArrayList);
                addQuesBtn.setEnabled(false);
            }
        }
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getExtras() != null) {
                long millisUntilFinished = intent.getLongExtra("countdown", 0);
                mins = millisUntilFinished/60000;
                hours = mins / 60;
                mins -= hours * 60;
                timerHours.setText(String.valueOf(hours));
                timerMins.setText(String.valueOf(mins));
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, new IntentFilter(TimerBroadcastService.COUNTDOWN_BR));
        Log.i(TAG, "Registered broadcast receiver");
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
        Log.i(TAG, "Unregistered broadcast receiver");
    }

    @Override
    public void onStop() {
        try {
            unregisterReceiver(broadcastReceiver);
        } catch (Exception e) {
            // Receiver was probably already stopped in onPause()
            Log.d(TAG, e.getMessage());
        }
        super.onStop();
    }

    @Override
    public void onDestroy() {
        try {
            stopService(timerIntent);
            Log.i(TAG, "Stopped service");
        }
        catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
        super.onDestroy();
    }

}
