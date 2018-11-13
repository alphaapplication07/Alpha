package com.seekethfind.alpha.model;

import android.os.Parcel;
import android.os.Parcelable;

public class User_SignUp implements Parcelable {
    private String user_id  = "";
    private String user_Name = "";
    private String user_Email = "";
    private String user_password = "";
    private String user_mobile;

    public User_SignUp() {
    }

    public User_SignUp(String user_id, String user_Name, String user_Email, String user_password, String user_mobile) {
        this.user_id = user_id;
        this.user_Name = user_Name;
        this.user_Email = user_Email;
        this.user_password = user_password;
        this.user_mobile = user_mobile;
    }

    protected User_SignUp(Parcel in) {
        user_id = in.readString();
        user_Name = in.readString();
        user_Email = in.readString();
        user_password = in.readString();
        user_mobile = in.readString();
    }

    public static final Creator<User_SignUp> CREATOR = new Creator<User_SignUp>() {
        @Override
        public User_SignUp createFromParcel(Parcel in) {
            return new User_SignUp(in);
        }

        @Override
        public User_SignUp[] newArray(int size) {
            return new User_SignUp[size];
        }
    };

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_Name() {
        return user_Name;
    }

    public void setUser_Name(String user_Name) {
        this.user_Name = user_Name;
    }

    public String getUser_Email() {
        return user_Email;
    }

    public void setUser_Email(String user_Email) {
        this.user_Email = user_Email;
    }

    public String getUser_password() {
        return user_password;
    }

    public void setUser_password(String user_password) {
        this.user_password = user_password;
    }

    public String getUser_mobile() {
        return user_mobile;
    }

    public void setUser_mobile(String user_mobile) {
        this.user_mobile = user_mobile;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(user_id);
        parcel.writeString(user_Name);
        parcel.writeString(user_Email);
        parcel.writeString(user_password);
        parcel.writeString(user_mobile);
    }
}
