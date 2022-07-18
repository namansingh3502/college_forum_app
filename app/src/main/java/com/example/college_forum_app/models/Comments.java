package com.example.college_forum_app.models;

public class Comments {

    private String body;
    private Users user;
    private String time;
    private int id;

    public Comments(int id, String body, Users user, String time) {
        this.id = id;
        this.body = body;
        this.user = user;
        this.time = time;
    }

    public Comments() {
    }

    public Integer getId() {
        return this.id;
    }

    public String getBody() {
        return this.body;
    }

    public String getTime() {
        return this.time;
    }

    public Users getUser() {
        return this.user;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setUser(Users user){ this.user = user;}

    public void setBody(String body) {
        this.body = body;
    }

    public void setTime(String time) {
        this.time = time;
    }

}
