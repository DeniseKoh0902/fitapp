package com.example.fitapp.javahelperfile.ratingsystem;

public class FeedbackWithUser {
    private String feedbackText;
    private float rating;
    private String userId;
    private String username;
    private String profilePicPath;

    public FeedbackWithUser(String feedbackText, float rating, String userId, String username, String profilePicPath) {
        this.feedbackText = feedbackText;
        this.rating = rating;
        this.userId = userId;
        this.username = username;
        this.profilePicPath = profilePicPath;
    }

    // Getters and Setters
    public String getFeedbackText() {
        return feedbackText;
    }

    public void setFeedbackText(String feedbackText) {
        this.feedbackText = feedbackText;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

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

    public String getProfilePicPath() {
        return profilePicPath;
    }

    public void setProfilePicPath(String profilePicPath) {
        this.profilePicPath = profilePicPath;
    }
}
