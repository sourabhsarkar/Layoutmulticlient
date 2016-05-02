package com.project.layoutmulticlient;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;

public class ResultContestant extends AppCompatActivity {

    TextView scoreTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_contestant);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        scoreTv = (TextView) findViewById(R.id.scoreTextView);

        scoreTv.setText(getIntent().getStringExtra("result"));
    }
}
