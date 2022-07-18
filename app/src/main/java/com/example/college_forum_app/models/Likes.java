package com.example.college_forum_app.models;

public class Likes {

    private String full_name;
    private Integer user_id;

    public Likes(String full_name, Integer user_id) {
        this.full_name = full_name;
        this.user_id = user_id;
    }

    public Likes() {
    }

    public Likes(String user_full_name, String user_id) {
    }

    public String getFull_name() {
        return full_name;
    }

    public Integer getUser_id() {
        return user_id;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }
}
