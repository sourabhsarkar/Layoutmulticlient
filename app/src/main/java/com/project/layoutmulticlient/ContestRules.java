package com.project.layoutmulticlient;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class ContestRules extends AppCompatActivity {

    protected Msg message;
    private static boolean contestStarted;
    protected static final String TAG = ContestRules.class.getSimpleName();
    public static long timer;
    Button startContestBtn;
    TextView con_details_tv;
    protected String con_details;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contest_rules);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        startContestBtn = (Button) findViewById(R.id.startContestBtn);
        con_details_tv = (TextView) findViewById(R.id.contestRules);

        con_details = getIntent().getStringExtra("contest_details");
        con_details_tv.setText(con_details);
    }

    public void startContest(View view) {
        contestStarted = true;
        Intent intent = new Intent(this, DisplayQuestions.class);
        intent.putExtra("quesMsg", message);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(!contestStarted) {
            message = (Msg) getIntent().getSerializableExtra("quesMsg");
            timer = getIntent().getLongExtra("timer", 0);
        }
        else {
            startContestBtn.setEnabled(false);
        }
    }
}
