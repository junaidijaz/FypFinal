package com.example.Saad.MyFYPProject.models.user;

import com.google.gson.annotations.SerializedName;


public class UserRes {

    private boolean result;
    @SerializedName("user")
    private User user;


    // Getter Methods

    public boolean getResult() {
        return result;
    }

    public User getUser() {
        return user;
    }

    // Setter Methods

    public void setResult(boolean result) {
        this.result = result;
    }

    public void setUser(User userObject) {
        this.user = userObject;
    }
}




