package com.example.educar;

import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Spinner;

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
    public void uploadProfile(Bitmap profileBitmap, Spinner gendersSpinner, Bitmap dMale, Bitmap dFemale, Bitmap dNonBinary){

        Thread threadProfileImage = new Thread(new profileImagePoster(profileBitmap, gendersSpinner, dMale, dFemale, dNonBinary));

        threadProfileImage.start();
    }

    public void determineGender(ImageView profileImage){

        GENDER gender = new GENDER();
        Thread threadDetermineGender = new Thread(new DetermineGender(gender));
        Thread threadPopulateProfileImageView = new Thread(new PopulateProfileImageView(profileImage, gender));
        threadDetermineGender.start();
        threadPopulateProfileImageView.start();
    }


}
