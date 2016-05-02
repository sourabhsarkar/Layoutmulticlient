package com.project.layoutmulticlient;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
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
    Button next_btn, sub_btn;
    Random rand = new Random();
    boolean set[];
    boolean doubleBackToExitPressedOnce = false;
    Intent timerIntent;
    int count, i, quesOrder[], answerMarked[];
    long mins, secs;
    String time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_question);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        quesStatement = (TextView) findViewById(R.id.quesStatement);
        timerTextView = (TextView) findViewById(R.id.timerTextView);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroupOptions);
        radioButton1 = (RadioButton) findViewById(R.id.option1);
        radioButton2 = (RadioButton) findViewById(R.id.option2);
        radioButton3 = (RadioButton) findViewById(R.id.option3);
        radioButton4 = (RadioButton) findViewById(R.id.option4);
        next_btn = (Button) findViewById(R.id.nextButton);
        sub_btn = (Button) findViewById(R.id.submitButton);
        sub_btn.setVisibility(View.GONE);

        message = (Msg) getIntent().getSerializableExtra("quesMsg");
        questions = message.getQuestionArray();
        quesNo = questions.size();
        set = new boolean[quesNo];
        quesOrder = new int[quesNo];
        answerMarked = new int[quesNo];

        i = rand.nextInt(quesNo);
        set[i] = true;
        quesOrder[count++] = i;
        quesStatement.setText(questions.get(i).quesStatement);
        radioButton1.setText(questions.get(i).option1);
        radioButton2.setText(questions.get(i).option2);
        radioButton3.setText(questions.get(i).option3);
        radioButton4.setText(questions.get(i).option4);
        timerIntent = new Intent(this, TimerBroadcastService.class);
        startService(timerIntent);
        Log.i(TAG, "Started service");
    }

    public void nextClicked(View view) {

        checkMarkedAnswer();

        if (count < quesNo) {

            int j = 0;
            i = rand.nextInt(quesNo);
            while (set[i])
                i = rand.nextInt(quesNo);
            set[i] = true;
            quesOrder[count++] = i;

            radioGroup.clearCheck();
            quesStatement.setText(questions.get(i).quesStatement);
            radioButton1.setText(questions.get(i).option1);
            radioButton2.setText(questions.get(i).option2);
            radioButton3.setText(questions.get(i).option3);
            radioButton4.setText(questions.get(i).option4);

            if(count == quesNo) {
                next_btn.setVisibility(View.GONE);
                sub_btn.setVisibility(View.VISIBLE);
            }
        }
    }

    public void submitClicked(View view) {
        checkMarkedAnswer();
        int marks = 0;
        for (int y =0; y < count; y++) {
            if(questions.get(quesOrder[y]).correctOption == answerMarked[y]) {
                marks++;
            }
        }
        NsdChatActivity.mConnection.sendAllMessage("questionorder", quesOrder);
        NsdChatActivity.mConnection.sendAllMessage("markedanswer", answerMarked);
        Intent intent = new Intent(this, ResultContestant.class);
        intent.putExtra("result",String.valueOf(marks) + " out of " + String.valueOf(quesNo));
        startActivity(intent);
    }

    private void checkMarkedAnswer() {
        int check = radioGroup.getCheckedRadioButtonId();

        if (check == -1) {
            answerMarked[count - 1] = -1;
        } else if (check == R.id.option1) {
            answerMarked[count - 1] = 1;
        } else if (check == R.id.option2) {
            answerMarked[count - 1] = 2;
        } else if (check == R.id.option3) {
            answerMarked[count - 1] = 3;
        } else if (check == R.id.option4) {
            answerMarked[count - 1] = 4;
        }

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
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getExtras() != null) {
                long millisUntilFinished = intent.getLongExtra("countdown", 0);
                if(millisUntilFinished == 0) {
                    next_btn.setVisibility(View.GONE);
                    sub_btn.setVisibility(View.VISIBLE);
                }
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

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
}