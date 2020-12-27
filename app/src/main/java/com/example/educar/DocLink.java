package com.example.educar;

import java.util.ArrayList;
import java.util.List;

public class DocLink {
    private List<String> docLink = new ArrayList<String>();

    private boolean isChanged = false;

    public List<String> getLink() {
        return docLink;
    }

    public void addLink(String docLink)
    {
        this.docLink.add(docLink);
    }

    public void changeLink(){
        isChanged = true;
    }
    public boolean changed (){
        return isChanged;
    }
}
