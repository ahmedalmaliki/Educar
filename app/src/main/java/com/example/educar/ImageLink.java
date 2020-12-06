package com.example.educar;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class ImageLink {
    private List<String> links = new ArrayList<String>();;
    private boolean isChanged = false;


    public List<String> getLinks() {
        return links;
    }

    public void addLink(String link)
    {
        this.links.add(link);
      //  Log.d("Success_links1", String.valueOf(links));


    }

    public void changeLink(){
        isChanged = true;
    }
    public boolean changed(){
        return isChanged;
    }
}
