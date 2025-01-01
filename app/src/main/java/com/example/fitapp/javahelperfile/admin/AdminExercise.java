package com.example.fitapp.javahelperfile.admin;

public class AdminExercise {
    private String exerciseId;
    private String exerciseName;
    private String exerciseCategory;
    private int exerciseDuration;
    private int caloriesBurned;
    private int drawableResId;
    private String exercisePicPath;

    // Required empty constructor for Firebase
    public AdminExercise() {}

    public AdminExercise(String exerciseId, String exerciseName, String exerciseCategory, int exerciseDuration, int caloriesBurned, int drawableResId, String exercisePicPath) {
        this.exerciseId = exerciseId;
        this.exerciseName = exerciseName;
        this.exerciseCategory = exerciseCategory;
        this.exerciseDuration = exerciseDuration;
        this.caloriesBurned = caloriesBurned;
        this.drawableResId = drawableResId;
        this.exercisePicPath = exercisePicPath;
    }

    public String getExerciseId() {
        return exerciseId;
    }

    public String getExerciseName() {
        return exerciseName;
    }

    public String getExerciseCategory() {
        return exerciseCategory;
    }

    public int getExerciseDuration() {
        return exerciseDuration;
    }

    public int getCaloriesBurned() {
        return caloriesBurned;
    }

    public int getDrawableResId() {
        return drawableResId;
    }

    public String getExercisePicPath() {
        return exercisePicPath;
    }
}
