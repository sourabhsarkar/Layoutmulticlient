package com.project.layoutmulticlient;

/**
 * Created by Sourabh on 08-May-16.
 */
public class Score implements Comparable<Score>{

    private String username;
    private int marks;

    public int getMarks() {
        return marks;
    }

    public void setMarks(int marks) {
        this.marks = marks;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public int compareTo(Score score) {
        return (score.getMarks())-marks;
    }
}
