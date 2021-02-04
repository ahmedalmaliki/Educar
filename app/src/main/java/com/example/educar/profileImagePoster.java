package com.example.educar;

import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Spinner;

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
import java.io.IOException;

public class profileImagePoster implements Runnable {
    private Bitmap profileBitmap;
    private Spinner gendersSpinner;
    private Bitmap dMale;
    private Bitmap dFemale;
    private Bitmap dNonBinary;

    public profileImagePoster( Bitmap profileBitmap, Spinner genderSpinner, Bitmap dMale, Bitmap dFemale, Bitmap dNonBinary) {
        this.profileBitmap = profileBitmap;
        this.gendersSpinner = genderSpinner;
        this.dMale = dMale;
        this.dFemale = dFemale;
        this.dNonBinary = dNonBinary;

    }

    @Override
    public void run() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference profilesRef = storageRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()+ "/profilePicture/" + "profile.jpg");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if (profileBitmap == null) {
            sendDefaultProfileImage(baos, profilesRef);

        }
        else {
            sendSelectedProfileImage(profilesRef, baos);
        }

    }
    private void sendSelectedProfileImage(StorageReference profilesRef, ByteArrayOutputStream baos) {
        profileBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

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
                        FirebaseDatabase.getInstance().getReference("UsersProfileImages").child(FirebaseAuth.getInstance().
                                getCurrentUser().getUid()).setValue(uri.toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                            }
                        });

                    }
                });

            }
        });
    }

    private void sendDefaultProfileImage( ByteArrayOutputStream baos,  StorageReference profilesRef) {
        switch (gendersSpinner.getSelectedItemPosition()) {
            //Male
            case (0):
                profileBitmap = dMale;
                break;
            //Female
            case (1):
                profileBitmap = dFemale;
                break;
            //nonBinary
            case (2):

                profileBitmap = dNonBinary;
                break;
        }
        profileBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);

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
                        FirebaseDatabase.getInstance().getReference("UsersProfileImages").child(FirebaseAuth.getInstance().
                                getCurrentUser().getUid()).setValue(uri.toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                            }
                        });
                    }
                });
            }
        });

    }
}
