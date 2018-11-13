package com.seekethfind.alpha.model;

public class UserSettings {



    private User_SignUp user;

    private UserAccountSettings settings;



    public UserSettings(User_SignUp user, UserAccountSettings settings) {

        this.user = user;

        this.settings = settings;

    }



    public UserSettings() {



    }





    public User_SignUp getUser() {

        return user;

    }



    public void setUser(User_SignUp user) {

        this.user = user;

    }



    public UserAccountSettings getSettings() {

        return settings;

    }



    public void setSettings(UserAccountSettings settings) {

        this.settings = settings;

    }



    @Override

    public String toString() {

        return "UserSettings{" +

                "user=" + user +

                ", settings=" + settings +

                '}';

    }

}