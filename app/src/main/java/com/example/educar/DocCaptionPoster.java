package com.example.educar;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DocCaptionPoster implements Runnable{
    private DatabaseReference reff, reff1;
    private Post post;
    private DocLink docLink;
    private String docName;
    public DocCaptionPoster(Post post, DocLink docLink, String docName) {
        this.post = post;
        this.docLink  = docLink;
        this.docName = docName;
    }

    @Override
    public void run() {
        while (!docLink.changed()){
            synchronized (docLink){
                try {
                    docLink.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        Log.d("NOTIFY_ALL", "notified");
        post.setUrls(docLink.getLink());
        post.setFile_name(docName);
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
