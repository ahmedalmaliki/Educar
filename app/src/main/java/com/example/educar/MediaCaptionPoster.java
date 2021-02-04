package com.example.educar;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MediaCaptionPoster implements Runnable {
    private Post post;
    private MediaLink mediaLink;

    public MediaCaptionPoster(Post post, MediaLink mediaLink) {
        this.post = post;
        this.mediaLink = mediaLink;
    }

    @Override
    public void run() {
        while (!mediaLink.changed()){
            synchronized (mediaLink){
                try {
                    Log.d("Waiting_thread", "waiting");
                    mediaLink.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        Log.d("NOTIFY_ALL", "notified");
        post.setUrls(mediaLink.getLinks());
        FirebaseDatabase.getInstance().getReference("Posts").child(post.getPost_id()).setValue(post).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });


        FirebaseDatabase.getInstance().getReference("UserPosts").child(post.getPost_id()).setValue(post).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });

    }
}
