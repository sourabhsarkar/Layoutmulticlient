package com.project.layoutmulticlient;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

public class DisplayQuestions extends AppCompatActivity {

    private final String TAG = DisplayQuestions.class.getSimpleName();

    protected Msg message;
    protected int quesNo;
    protected ArrayList<Question> questions = new ArrayList<Question>();
    TextView quesStatement, timerTextView;
    RadioGroup radioGroup;
    RadioButton radioButton1, radioButton2, radioButton3, radioButton4;
    Button next_btn;
    Random rand = new Random();
    boolean set[];
    Intent timerIntent;
    int count = 0, i = 0;
    long mins = 0, secs = 0;
    String time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_question);

        quesStatement = (TextView) findViewById(R.id.quesStatement);
        timerTextView = (TextView) findViewById(R.id.timerTextView);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroupOptions);
        radioButton1 = (RadioButton) findViewById(R.id.option1);
        radioButton2 = (RadioButton) findViewById(R.id.option2);
        radioButton3 = (RadioButton) findViewById(R.id.option3);
        radioButton4 = (RadioButton) findViewById(R.id.option4);
        next_btn = (Button) findViewById(R.id.nextButton);

        message = (Msg) getIntent().getSerializableExtra("quesMsg");
        questions = message.getQuestionArray();
        quesNo = questions.size();
        set = new boolean[quesNo];

        i = rand.nextInt(quesNo);
        set[i] = true;
        count++;
        if (questions.size() > i) {
            quesStatement.setText(questions.get(i).quesStatement);
            radioButton1.setText(questions.get(i).option1);
            radioButton2.setText(questions.get(i).option2);
            radioButton3.setText(questions.get(i).option3);
            radioButton4.setText(questions.get(i).option4);
        } else {
            Log.d(TAG, "Ques list less than index chosen!");
        }
        timerIntent = new Intent(this, TimerBroadcastService.class);
        startService(timerIntent);
        Log.i(TAG, "Started service");
    }

    public void nextClicked(View view) {

        int check = radioGroup.getCheckedRadioButtonId();

        if (check == -1) {
            Toast.makeText(this, "Not attempted", Toast.LENGTH_SHORT).show();
        } else if (check == R.id.option1 && questions.get(i).correctOption == 1) {
            Toast.makeText(this, "Correct answer!", Toast.LENGTH_SHORT).show();
        } else if (check == R.id.option2 && questions.get(i).correctOption == 2) {
            Toast.makeText(this, "Correct answer!", Toast.LENGTH_SHORT).show();
        } else if (check == R.id.option3 && questions.get(i).correctOption == 3) {
            Toast.makeText(this, "Correct answer!", Toast.LENGTH_SHORT).show();
        } else if (check == R.id.option4 && questions.get(i).correctOption == 4) {
            Toast.makeText(this, "Correct answer!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Wrong answer!", Toast.LENGTH_SHORT).show();
        }

        if (count < quesNo) {

            int j = 0;
            i = rand.nextInt(quesNo);
            while (set[i])
                i = rand.nextInt(quesNo);
            set[i] = true;
            count++;

            if (questions.size() > i) {
                radioGroup.clearCheck();
                quesStatement.setText(questions.get(i).quesStatement);
                radioButton1.setText(questions.get(i).option1);
                radioButton2.setText(questions.get(i).option2);
                radioButton3.setText(questions.get(i).option3);
                radioButton4.setText(questions.get(i).option4);
            } else {
                Log.d(TAG, "Ques list less than index chosen!");
            }
        }
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getExtras() != null) {
                long millisUntilFinished = intent.getLongExtra("countdown", 0);
                if(millisUntilFinished == 0)
                    next_btn.setEnabled(false);
                secs = millisUntilFinished/1000;
                mins = secs / 60;
                secs -= mins * 60;
                time = mins + " : " + secs;
                Log.i(TAG, time);
                timerTextView.setText(time);
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
        stopService(timerIntent);
        Log.i(TAG, "Stopped service");
        super.onDestroy();
    }
}