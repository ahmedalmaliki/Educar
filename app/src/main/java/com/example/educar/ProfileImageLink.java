package com.example.educar;

public class ProfileImageLink {
    private String Link;
    private boolean isChanged = false;

    public String getLink() {
        return Link;
    }

    public void setLink(String link) {
        Link = link;
    }
    public void changeLink(){
        isChanged = true;
    }
    public boolean changed(){
        return isChanged;
    }
}
