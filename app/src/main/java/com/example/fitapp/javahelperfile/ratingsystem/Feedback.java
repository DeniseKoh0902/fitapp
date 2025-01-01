package com.example.fitapp.javahelperfile.ratingsystem;
public class Feedback {
    private String feedbackText;
    private float rating;
    private String feedbackDatetime;
    private String username;
    private String userId;

    // Default constructor (required for Firebase)
    public Feedback() {
    }

    public Feedback(String feedbackText, float rating, String feedbackDatetime) {
        this.feedbackText = feedbackText;
        this.rating = rating;
        this.feedbackDatetime = feedbackDatetime;
    }

    public Feedback(String feedbackText, float rating, String feedbackDatetime, String userId) {
        this.feedbackText = feedbackText;
        this.rating = rating;
        this.feedbackDatetime = feedbackDatetime;
        this.userId = userId;
    }


    // Getters and setters
    public String getFeedbackText() {
        return feedbackText;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public String getFeedbackDatetime() {
        return feedbackDatetime;
    }

    public void setFeedbackDatetime(String feedbackDatetime) {
        this.feedbackDatetime = feedbackDatetime;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
