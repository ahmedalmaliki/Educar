package com.example.educar;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class DetermineGender implements Runnable{
    private FirebaseUser user;
    private final GENDER gender;
    DatabaseReference ref;

    public DetermineGender(GENDER gender) {
        this.gender = gender;
    }

    @Override
    public void run() {

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        ref = database.getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        genderTypeInquary();
        Log.d("t0", "S");
    }

    private void genderTypeInquary() {
        ref.child("Users").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);
                gender.changeGenderValue(userProfile.gender);
                gender.changeGender();
               synchronized (gender) {
                    gender.notifyAll();
                }

            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
//        ref.child(user.getUid()).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                Log.d("stage6", "S");
//                User userProfile = dataSnapshot.getValue(User.class);
//                String genders = userProfile.gender;
//                Log.d("stage4", genders);
//                gender.changeGenderValue(userProfile.gender);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//            Log.d("stage7", String.valueOf(databaseError.getCode()));
//            }
//        });
//

    }
}
