package com.project.layoutmulticlient;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

public class DisplayQuestions extends AppCompatActivity {

    protected Msg message;
    protected int quesNo = 5;
    protected ArrayList<Question> questions = new ArrayList<Question>();
    TextView quesStatement;
    RadioGroup radioGroup;
    RadioButton radioButton1, radioButton2, radioButton3, radioButton4;
    Button next_btn;
    Random rand = new Random();
    boolean set[] = new boolean[quesNo];
    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_question);

        quesStatement = (TextView) findViewById(R.id.quesStatement);
        radioButton1 = (RadioButton) findViewById(R.id.option1);
        radioButton2 = (RadioButton) findViewById(R.id.option2);
        radioButton3 = (RadioButton) findViewById(R.id.option3);
        radioButton4 = (RadioButton) findViewById(R.id.option4);
        next_btn = (Button) findViewById(R.id.nextButton);

        message = (Msg)getIntent().getSerializableExtra("quesMsg");
        questions = message.getQuestionArray();

        int i = rand.nextInt(quesNo);
        set[i] = true;
        count++;
        quesStatement.setText(questions.get(i).quesStatement);
        radioButton1.setText(questions.get(i).option1);
        radioButton2.setText(questions.get(i).option2);
        radioButton3.setText(questions.get(i).option3);
        radioButton4.setText(questions.get(i).option4);
    }

    public void nextClicked(View view) {

        if (count < quesNo) {

            int i = 0, j = 0;
            i = rand.nextInt(quesNo);
            while (set[i])
                i = rand.nextInt(quesNo);
            set[i] = true;
            count++;

            quesStatement.setText(questions.get(i).quesStatement);
            radioButton1.setText(questions.get(i).option1);
            radioButton2.setText(questions.get(i).option2);
            radioButton3.setText(questions.get(i).option3);
            radioButton4.setText(questions.get(i).option4);
        }
    }
}
