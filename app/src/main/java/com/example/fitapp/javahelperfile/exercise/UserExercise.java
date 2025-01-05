package com.example.fitapp.javahelperfile.exercise;

public class UserExercise {
    private String id;
    private String userId;
    private String exerciseId;

    public UserExercise() {
    }

    public UserExercise(String id, String userId, String exerciseId) {
        this.id = id;
        this.userId = userId;
        this.exerciseId = exerciseId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(String exerciseId) {
        this.exerciseId = exerciseId;
    }
}
