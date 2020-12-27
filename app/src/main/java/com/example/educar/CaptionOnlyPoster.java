package com.example.educar;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CaptionOnlyPoster implements Runnable{
    private DatabaseReference reff, reff1;
    private Post post;

    public CaptionOnlyPoster( Post post) {

        this.post = post;
    }

    @Override
    public void run() {
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
