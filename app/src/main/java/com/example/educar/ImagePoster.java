package com.example.educar;

import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class ImagePoster  implements Runnable{
    FirebaseStorage storage = FirebaseStorage.getInstance();
    private Bitmap bitmap;
    private String postId;
    private ImageLink imageLink;

    public ImagePoster( Bitmap bitmap, String postId, ImageLink imageLink) {
        this.postId = postId;
        this.bitmap = bitmap;
        this.imageLink = imageLink;
    }

    @Override
    public void run() {
        StorageReference storageRef = storage.getReference();
        StorageReference profilesRef = storageRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()+ "/"+postId+"/" + "photo.jpg");
        Log.d("ID_posted",FirebaseAuth.getInstance().getCurrentUser().getUid());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        if (bitmap == null) {
            //Do nothing my nigga

        }
        else {
            sendSelectedImage(profilesRef, baos);
        }


    }
    private void sendSelectedImage(StorageReference profilesRef, ByteArrayOutputStream baos) {
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

        byte[] data = baos.toByteArray();
        UploadTask uploadTask = profilesRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        imageLink.setLink(uri.toString());
                        imageLink.changeLink();
                        synchronized (imageLink) {
                            imageLink.notifyAll();
                            Log.d("NOTIFY_ALL", "notified");

                        }
                    }
                });
            }

        });
    }
}