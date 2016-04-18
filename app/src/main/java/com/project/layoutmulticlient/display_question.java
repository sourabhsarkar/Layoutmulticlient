package com.project.layoutmulticlient;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class display_question extends AppCompatActivity {

    protected Msg message;
    protected ArrayList<Question> questions = new ArrayList<Question>();
    TextView quesStatement;
    RadioGroup radioGroup;
    RadioButton radioButton1, radioButton2, radioButton3, radioButton4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_question);

        quesStatement = (TextView) findViewById(R.id.quesStatement);
        radioButton1 = (RadioButton) findViewById(R.id.option1);
        radioButton2 = (RadioButton) findViewById(R.id.option2);
        radioButton3 = (RadioButton) findViewById(R.id.option3);
        radioButton4 = (RadioButton) findViewById(R.id.option4);

        message = (Msg)getIntent().getSerializableExtra("quesMsg");
        questions = message.getQuestionArray();

        quesStatement.setText(questions.get(0).quesStatement);
        radioButton1.setText(questions.get(0).option1);
        radioButton2.setText(questions.get(0).option2);
        radioButton3.setText(questions.get(0).option3);
        radioButton4.setText(questions.get(0).option4);
    }
}
