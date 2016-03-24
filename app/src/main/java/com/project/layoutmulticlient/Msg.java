package com.project.layoutmulticlient;

import java.io.Serializable;
import java.net.InetAddress;

/**
 * Created by Sourabh on 24-Mar-16.
 */
public class Msg implements Serializable {

    private String key;
    private String message;
    private InetAddress address;

    public Msg(String key, String msg, InetAddress address) {
        this.key = key;
        message = msg;
        this.address = address;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public InetAddress getAddress() {
        return address;
    }

    public void setAddress(InetAddress address) {
        this.address = address;
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
