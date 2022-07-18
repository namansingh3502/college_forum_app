package com.example.college_forum_app.models;

import java.util.ArrayList;

public class Posts {

    private String body, time;
    private Boolean is_edited;
    private Integer id, comments_count;
    private Users user;
    private ArrayList<ChannelTags> posted_in;
    private ArrayList<Likes> likes;
    private ArrayList<String> image_urls;

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

    public void setComments_count(Integer comments_count) {
        this.comments_count = comments_count;
    }

    public void setPosted_in(ArrayList<ChannelTags> posted_in) {
        this.posted_in = posted_in;
    }

    public void setImage_urls(ArrayList<String> image_urls) {
        this.image_urls = image_urls;
    }

    public void setLikes(ArrayList<Likes> likes) {
        this.likes = likes;
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

    public Integer getComments_count() {
        return this.comments_count;
    }

    public String getPosted_in_string() {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < posted_in.size(); i++) {
            str.append("#").append(posted_in.get(i).getName()).append("  ");
        }
        return str.toString();
    }

    public ArrayList<Likes> getLikes() {
        return this.likes;
    }

    public ArrayList<String> getImage_urls() {
        return this.image_urls;
    }

    public void addLike(Likes new_like){
        this.likes.add(new_like);
    }

    public void removeLike(Likes newLike){
        this.likes.removeIf(like -> like.getUser_id().equals(newLike.getUser_id()));
    }

}
