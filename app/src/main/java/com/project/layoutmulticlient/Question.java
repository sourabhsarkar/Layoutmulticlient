package com.project.layoutmulticlient;

/**
 * Created by Sourabh on 17-Apr-16.
 */

public class Question {
    public String quesStatement;
    public String option1;
    public String option2;
    public String option3;
    public String option4;

    public Question (String q, String o1, String o2, String o3, String o4){
        quesStatement = q;
        option1 = o1;
        option2 = o2;
        option3 = o3;
        option4 = o4;
    }
}