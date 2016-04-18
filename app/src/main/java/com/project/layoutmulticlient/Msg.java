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
    private InetAddress address = null;

    public Msg(String key, String msg) {
        this.key = key;
        message = msg;
    }

    public Msg(String key, ArrayList<Question> q) {
        this.key = key;
        questionArray = q;
    }

    public String getKey() {
        return key;
    }

    public InetAddress getAddress() {
        return address;
    }

    public String getMessage() {
        return message;
    }

    public ArrayList<Question> getQuestionArray() {
        return questionArray;
    }

}
