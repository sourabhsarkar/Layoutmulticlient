package com.project.layoutmulticlient.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.project.layoutmulticlient.R;
import com.project.layoutmulticlient.ResultContestant;

/**
 * Created by Sourabh on 03-May-16.
 */
public class ScoreDisplayFragment extends Fragment {

    TextView scoreTv;
    Button reviewBtn;
    String score_str;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_score_display, container, false);
        scoreTv = (TextView) view.findViewById(R.id.scoreTextView);
        reviewBtn = (Button) view.findViewById(R.id.reviewButton);

        scoreTv.setText(score_str);
        reviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ResultContestant)getActivity()).showReviewListFragment();
            }
        });
        return view;
    }

    public void setScoreText(String str) {
        score_str = str;
    }
}
