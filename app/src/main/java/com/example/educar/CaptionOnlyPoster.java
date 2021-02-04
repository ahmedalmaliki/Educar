package com.example.educar;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CaptionOnlyPoster implements Runnable{
    private Post post;

    public CaptionOnlyPoster( Post post) {

        this.post = post;
    }

    @Override
    public void run() {
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
