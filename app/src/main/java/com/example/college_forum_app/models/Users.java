package com.example.college_forum_app.models;


public class Users {

    private String username, full_name, user_image, cover_photo;
    private int id;

    public Users(int id, String username, String full_name, String user_image, String cover_photo) {
        this.id = id;
        this.username = username;
        this.full_name = full_name;
        this.user_image = user_image;
        this.cover_photo = cover_photo;
    }

    public Users() {
    }

    public String getUsername() {
        return this.username;
    }

    public String getFull_name() {
        return this.full_name;
    }

    public String getUser_image() {
        return this.user_image;
    }

    public String getCover_photo() {
        return this.cover_photo;
    }

    public Integer getId() {
        return this.id;
    }


    public void setUsername(String username) {
        this.username = username;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public void setUser_image(String user_image) {
        this.user_image = user_image;
    }

    public void setCover_photo(String cover_photo) {
        this.cover_photo = cover_photo;
    }

    public void setId(Integer id) {
        this.id = id;
    }

}