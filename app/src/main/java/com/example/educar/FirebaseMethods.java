package com.example.educar;

import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.atomic.AtomicBoolean;

public class FirebaseMethods  {

    private String  postId;
    private Post thePost;

    public void sendPostToDataBase(Post post, Bitmap bitmap) {
        ImageLink imageLink = new ImageLink();
      Thread threadImage = new Thread(new ImagePoster(bitmap, post.getPost_id(), imageLink));
      Thread threadCaption = new Thread(new CaptionPoster(post, imageLink));
      threadImage.start();
      threadCaption.start();


    }






}
