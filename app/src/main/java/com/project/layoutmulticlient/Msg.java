package com.project.layoutmulticlient;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.ArrayList;

/**
 * Created by Sourabh on 24-Mar-16.
 */
public class Msg implements Serializable {

    private String key = null;
    private String message = null;

    private ArrayList<Question> questionArray = null;
    private int arrayQuesAns[];

    public Msg(String key, String msg) {
        this.key = key;
        message = msg;
    }

    public Msg(String key, ArrayList<Question> q) {
        this.key = key;
        questionArray = q;
    }

    public Msg(String key, int[] qa) {
        this.key = key;
        arrayQuesAns = qa;
    }

    public String getKey() {
        return key;
    }

    public String getMessage() {
        return message;
    }

    public ArrayList<Question> getQuestionArray() {
        return questionArray;
    }


    public int[] getArrayQuesAns() {
        return arrayQuesAns;
    }
}
