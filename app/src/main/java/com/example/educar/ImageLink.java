package com.example.educar;

public class ImageLink {
    private String link;
    private boolean isChanged = false;

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void changeLink(){
        isChanged = true;
    }
    public boolean changed(){
        return isChanged;
    }
}
