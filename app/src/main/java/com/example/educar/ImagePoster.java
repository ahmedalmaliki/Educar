package com.example.educar;

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

import java.util.List;

public class ImagePoster  implements Runnable{
    FirebaseStorage storage = FirebaseStorage.getInstance();
    private List<Uri> mSelected;
    private String postId;
    private ImageLink imageLink;
    private int counter, numOfUploadedFiles;



    public ImagePoster( List<Uri> mSelected, String postId, ImageLink imageLink) {
        this.postId = postId;
        this.mSelected = mSelected;
        this.imageLink = imageLink;
    }

    @Override
    public void run() {
        StorageReference storageRef = storage.getReference();
        Log.d("ID_posted",FirebaseAuth.getInstance().getCurrentUser().getUid());


        if (mSelected == null) {
            //Do nothing my nigga

        }
        else {
            sendSelectedImage(storageRef);
        }


    }
    private void sendSelectedImage(StorageReference storageRef) {
        for (Uri uri : mSelected) {
            StorageReference profilesRef = storageRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()+ "/"+postId + "/" + "photo.jpg"+"_"+counter++);
            UploadTask uploadTask = profilesRef.putFile(uri);
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
                            imageLink.addLink(uri.toString());
                            numOfUploadedFiles++;
                            if(numOfUploadedFiles == mSelected.size() ) {
                                imageLink.changeLink();
                                synchronized (imageLink) {
                                    imageLink.notifyAll();
                                }
                            }
                        }
                    });
                }

            });

            }
        }
    }
