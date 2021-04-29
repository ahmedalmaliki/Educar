package com.example.educar;



public class GENDER {
    private String gender;
    private boolean isChanged = false;
    public String getGender() {
        return gender;
    }

    public void changeGenderValue(String gender)
    {
       this.gender = gender;

    }

    public void changeGender(){
        isChanged = true;
    }
    public boolean changed(){
        return isChanged;
    }

}
