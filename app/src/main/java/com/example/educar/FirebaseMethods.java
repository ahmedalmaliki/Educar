package com.example.educar;

import android.net.Uri;

import java.util.HashMap;
import java.util.List;

public class FirebaseMethods  {

    private String  postId;
    private Post thePost;

    public void sendPostToDataBase(Post post, HashMap selectedUris) {
        mediaLink mediaLink = new mediaLink();
      Thread threadImage = new Thread(new mediaPoster(selectedUris, post.getPost_id(), mediaLink));
      Thread threadCaption = new Thread(new CaptionPoster(post, mediaLink));
      threadImage.start();
      threadCaption.start();


    }






}
