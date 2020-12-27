package com.example.educar;

import android.net.Uri;

import java.util.HashMap;

public class FirebaseMethods  {

    private String  postId;
    private Post thePost;

    public void sendMediaPostToDataBase(Post post, HashMap selectedUris) {
        MediaLink mediaLink = new MediaLink();
      Thread threadImage = new Thread(new MediaPoster(selectedUris, post.getPost_id(), mediaLink));
      Thread threadCaption = new Thread(new MediaCaptionPoster(post, mediaLink));
      threadImage.start();
      threadCaption.start();


    }
    public void sendDocPostToDataBase(Post post,String docName, Uri docUri){
        DocLink docLink = new DocLink();
        Thread threadDoc = new Thread(new DocPoster(docName, docUri, post.getPost_id(), docLink));
        Thread threadCaption = new Thread(new DocCaptionPoster(post, docLink, docName));
        threadDoc.start();
        threadCaption.start();

    }
    public void sendCaptionOnlyPostToDataBase(Post post){
        Thread threadCaption = new Thread(new CaptionOnlyPoster(post));
        threadCaption.start();
    }






}
