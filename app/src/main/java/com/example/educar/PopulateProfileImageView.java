package com.example.educar;

import android.util.Log;
import android.widget.ImageView;

public class PopulateProfileImageView implements Runnable {

    private ImageView profileImage;
    private GENDER gender;

    public PopulateProfileImageView(ImageView profileImage, GENDER gender) {
        this.profileImage = profileImage;
        this.gender = gender;
    }

    @Override
    public void run() {
        Log.d("stage9", gender.getGender());

        while (!gender.changed()){
            synchronized (gender){
                try {
                    gender.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        Log.d("stage8", gender.getGender());
        //loadGenericProfilePicture();
    }
    private void loadGenericProfilePicture() {
        switch (gender.getGender()){
            case "Male":
                profileImage.setImageResource(R.drawable.default_male);
                break;
            case "Female":
                profileImage.setImageResource(R.drawable.default_female);
                break;
            case "Other":
                profileImage.setImageResource(R.drawable.default_nonbinary);
                break;
            default:
                Log.d("WTF", "WTF");
        }
     }
}





