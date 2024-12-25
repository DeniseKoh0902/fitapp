package com.example.fitapp.javahelperfile.profile;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {

    private String userId;
    private String username;
    private String email;
    private String passwordHash;
    private String userType;
    private String dob;
    private String phone;
    private int points;
    private String signUpDate;
    private String profilePicPath;

    // Default constructor (required for Firebase Firestore)
    public User() {}

    // Constructor
    public User(String userId, String username, String email, String dob, String phone, int points, String profilePicPath, String signUpDate) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.dob = dob;
        this.phone = phone;
        this.points = points;
        this.profilePicPath = profilePicPath;
        this.signUpDate = signUpDate;
        this.userType = calculateUserType(points);
    }

    // Parcelable implementation
    protected User(Parcel in) {
        userId = in.readString();
        username = in.readString();
        email = in.readString();
        passwordHash = in.readString();
        userType = in.readString();
        dob = in.readString();
        phone = in.readString();
        points = in.readInt();
        signUpDate = in.readString();
        profilePicPath = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userId);
        dest.writeString(username);
        dest.writeString(email);
        dest.writeString(passwordHash);
        dest.writeString(userType);
        dest.writeString(dob);
        dest.writeString(phone);
        dest.writeInt(points);
        dest.writeString(signUpDate);
        dest.writeString(profilePicPath);
    }

    // Method to calculate user type based on points
    private String calculateUserType(int points) {
        if (points >= 0 && points <= 500) {
            return "bronze";
        } else if (points >= 501 && points <= 1000) {
            return "silver";
        } else if (points >= 1001 && points <= 1500) {
            return "gold";
        } else if (points > 1500) {
            return "diamond";
        }
        return "bronze"; // Default user type
    }

    // Getters and Setters (Required for Firebase Firestore)
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
        this.userType = calculateUserType(points); // Recalculate user type if points change
    }

    public String getSignUpDate() {
        return signUpDate;
    }

    public void setSignUpDate(String signUpDate) {
        this.signUpDate = signUpDate;
    }

    public String getProfilePicPath() {
        return profilePicPath;
    }

    public void setProfilePicPath(String profilePicPath) {
        this.profilePicPath = profilePicPath;
    }
}
