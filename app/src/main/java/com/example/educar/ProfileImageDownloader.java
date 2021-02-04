package com.example.educar;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ProfileImageDownloader {
    private  ProfileImageLink profileImageLink;
    private String link;

//    private void setLink(){
//        Query usernameQuery = FirebaseDatabase.getInstance().getReference().child("Users").orderByChild("username").equalTo(username);
//        usernameQuery.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.exists()) {
//                    usernameNotTaken.set(false);
//                    notifyUsernameTaken();
//                    usernameNotTaken.set(false);
//
//                }else{
//                    usernameNotTaken.set(true);
//                }
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
  //  }
}
