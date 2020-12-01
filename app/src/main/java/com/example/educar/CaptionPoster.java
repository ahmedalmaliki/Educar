package com.example.educar;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CaptionPoster implements Runnable {
    private DatabaseReference reff, reff1;
    private Post post;
    private ImageLink imageLink;

    public CaptionPoster(Post post, ImageLink imageLink) {
        this.post = post;
        this.imageLink = imageLink;
    }

    @Override
    public void run() {
        while (!imageLink.changed()){
            synchronized (imageLink){
                try {
                    imageLink.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        post.setImage_url(imageLink.getLink());
        reff = FirebaseDatabase.getInstance().getReference().child("Post");
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
