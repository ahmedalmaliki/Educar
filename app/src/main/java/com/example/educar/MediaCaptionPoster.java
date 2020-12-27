package com.example.educar;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MediaCaptionPoster implements Runnable {
    private DatabaseReference reff, reff1;
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
        reff = FirebaseDatabase.getInstance().getReference().child("Posts");
        reff.push().setValue(post).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                //Nothing for now
            }
        });

        reff1 = FirebaseDatabase.getInstance().getReference("UserPosts").child(FirebaseAuth.getInstance().
                getCurrentUser().getUid());
        reff1.push().setValue(post);

    }
}
