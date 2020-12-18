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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.security.KeyStore;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class mediaPoster implements Runnable{
    FirebaseStorage storage = FirebaseStorage.getInstance();
    private HashMap<String, Uri> selectedUris;
    private String postId;
    private mediaLink mediaLink;
    private int counter, numOfUploadedFiles;



    public mediaPoster(HashMap<String, Uri> selectedUris, String postId, mediaLink mediaLink) {
        this.postId = postId;
        this.selectedUris = selectedUris;
        this.mediaLink = mediaLink;
    }




    @Override
    public void run() {
        StorageReference storageRef = storage.getReference();
        Log.d("ID_posted",FirebaseAuth.getInstance().getCurrentUser().getUid());


        if (selectedUris == null) {
            //Do nothing my nigga

        }
        else {
            sendSelectedMedia(storageRef);
        }


    }
    private void sendSelectedMedia(StorageReference storageRef) {
        for (Uri uri : selectedUris.values()) {
            Log.d("Check_Two", selectedUris.toString());

            for (Map.Entry<String, Uri> entry : selectedUris.entrySet()) {
                if (entry.getValue() == uri) {
                    if (entry.getKey().contains("video")) {
                        sendVideo(storageRef, uri);

                    }
                    else {
                        sendImage(storageRef, uri);



                    }
                }
            }

        }
    }

    private void sendImage(StorageReference storageRef, Uri uri) {
        StorageReference profilesRef = storageRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()+ "/"+postId + "/" + "image"+"_"+counter++);
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
                        mediaLink.addLink(uri.toString());
                        numOfUploadedFiles++;
                        if(numOfUploadedFiles == selectedUris.size() ) {
                            mediaLink.changeLink();
                            synchronized (mediaLink) {
                                mediaLink.notifyAll();
                            }
                        }
                    }
                });
            }

        });
    }

    private void sendVideo(StorageReference storageRef, Uri uri) {
            StorageReference profilesRef = storageRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()+ "/"+postId + "/" + "video"+"_"+counter++);
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
                            mediaLink.addLink(uri.toString());
                            numOfUploadedFiles++;
                            if(numOfUploadedFiles == selectedUris.size() ) {
                                mediaLink.changeLink();
                                synchronized (mediaLink) {
                                    mediaLink.notifyAll();
                                }
                            }
                        }
                    });
                }

            });

        }
        }

