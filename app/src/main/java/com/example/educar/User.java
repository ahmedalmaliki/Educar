package com.example.educar;

import android.graphics.Bitmap;

public class User {
    public String fullName, email, occupation, username, school, age, bio, gender, profileImageLink;


    public User(String fullName, String email, String occupation, String username, String school, String age, String bio,String gender) {
        this.fullName = fullName;
        this.email = email;
        this.occupation = occupation;
        this.username = '@'+username;
        this.school = school;
        this.age = age;
        this.bio = bio;
        this.gender = gender;

    }

    public void setProfileImageLink(String profileImageLink) {
        this.profileImageLink = profileImageLink;
    }

    public User() {

    }


}
