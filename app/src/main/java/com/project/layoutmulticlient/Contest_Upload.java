package com.project.layoutmulticlient;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

public class Contest_Upload extends Activity {

    public final String TAG = Contest_Upload.class.getSimpleName();
    private Uri uri;
    private ArrayList<Question> questionArrayList = new ArrayList<Question>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contest_upload);
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
                Toast.makeText(this, "No file found!", Toast.LENGTH_LONG).show();
            } else {
                uri = data.getData();
                Log.i(TAG, "Excel URI: " + uri);
                questionArrayList = ReadExcelFile.readExcelFile(uri);
            }
        }
        else if (resultCode != RESULT_CANCELED) {
            Toast.makeText(this, "There was an error reading the file!", Toast.LENGTH_LONG).show();
        }
    }

    public void sendQues(View view) {

        NsdChatActivity.mConnection.sendAllMessage("ques",questionArrayList);
    }
}
