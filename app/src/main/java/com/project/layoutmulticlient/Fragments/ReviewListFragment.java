package com.project.layoutmulticlient.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.project.layoutmulticlient.R;

/**
 * Created by Sourabh on 03-May-16.
 */
public class ReviewListFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_review_list, container, false);
        return view;
    }
}
