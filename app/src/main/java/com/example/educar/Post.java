package com.example.educar;

import android.util.Log;

import java.util.List;
import java.util.UUID;

public class Post {
    private String caption;
    private String date;
    private List<String> image_url;
    private String post_id;
    private String tags;
    private String user_id;

    public Post(String caption, String date, List<String> image_url, String tags, String user_id) {
        this.caption = caption;
        this.date = date;
        this.image_url = image_url;
        this.post_id =  UUID.randomUUID().toString();;
        this.tags = tags;
        this.user_id = user_id;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<String> getImage_url() {
        return image_url;
    }

    public void setImage_url(List<String> image_url) {
        this.image_url = image_url;
        Log.d("Success_links", String.valueOf(image_url));
    }

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
