package com.project.layoutmulticlient;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ContestantResultList extends AppCompatActivity {

    ListView resultList;
    public static ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contestant_result_list);

        resultList = (ListView) findViewById(R.id.contestantResultListView);

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_2, android.R.id.text1, NsdChatActivity.mConnection.usernameList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                TextView text2 = (TextView) view.findViewById(android.R.id.text2);

                text1.setText(NsdChatActivity.mConnection.usernameList.get(position));
                text2.setText(String.valueOf(NsdChatActivity.mConnection.scoreList.get(position)));
                return view;
            }
        };
        resultList.setAdapter(adapter);
    }
}
