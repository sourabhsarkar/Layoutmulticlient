package com.project.layoutmulticlient;

import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;

import com.project.layoutmulticlient.Fragments.ReviewListFragment;
import com.project.layoutmulticlient.Fragments.ScoreDisplayFragment;

public class ResultContestant extends AppCompatActivity {

    ScoreDisplayFragment scoreDisplayFragment;
    ReviewListFragment reviewListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_contestant);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        scoreDisplayFragment = new ScoreDisplayFragment();
        reviewListFragment = new ReviewListFragment();

        showScoreDisplayFragment();
        scoreDisplayFragment.setScoreText(getIntent().getStringExtra("result"));
    }

    public void showScoreDisplayFragment() {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.frameResult, scoreDisplayFragment);
        fragmentTransaction.commit();
    }

    public void showReviewListFragment() {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.frameResult, reviewListFragment);
        fragmentTransaction.commit();
    }
}
