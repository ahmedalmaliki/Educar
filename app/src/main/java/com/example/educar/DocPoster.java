package com.example.educar;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class DocPoster implements Runnable {
    FirebaseStorage storage = FirebaseStorage.getInstance();
    private String docName;
    private Uri docUri;
    private String postId;
    private DocLink docLink;

    public DocPoster( String docName, Uri docUri, String postId, DocLink docLink) {
        this.docName = docName;
        this.docUri = docUri;
        this.postId = postId;
        this.docLink = docLink;
    }

    @Override
    public void run() {
        StorageReference storageRef = storage.getReference();
        if (docUri == null) {
            //Do nothing my nigga

        }
        else {
            sendSelectedDoc(storageRef);
        }

    }

    private void sendSelectedDoc(StorageReference storageRef) {
        StorageReference docRef = storageRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()+ "/"+postId + "/" + docName);
        UploadTask uploadTask = docRef.putFile(docUri);
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
                        docLink.addLink(uri.toString());
                        docLink.changeLink();
                        synchronized (docLink) {
                            docLink.notifyAll();

                        }
                    }
                });
            }

        });

    }
}
