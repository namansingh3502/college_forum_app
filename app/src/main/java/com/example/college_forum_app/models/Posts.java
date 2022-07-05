package com.example.college_forum_app.models;

public class Posts {

    private String body, time;
    private Boolean is_edited;
    private Integer id;
    private Users user;

    public Posts(String body, String time, Boolean is_edited, Integer id) {
        this.body = body;
        this.time = time;
        this.is_edited = is_edited;
        this.id = id;
    }

    public Posts() {
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setIs_edited(Boolean is_edited) {
        this.is_edited = is_edited;
    }

    public void setUser(Users user) {
        this.user = user;
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

    public Boolean getIs_edited() {
        return this.is_edited;
    }

    public Users getUser() {
        return this.user;
    }

}
