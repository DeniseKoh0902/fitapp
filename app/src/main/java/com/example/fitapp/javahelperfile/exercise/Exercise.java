package com.example.fitapp.javahelperfile.exercise;

public class Exercise {
    private String exerciseId; // Unique ID for the exercise, generated by Firebase
    private String exerciseName;
    private String exerciseCategory;
    private String exercisePicPath; // picture path of the exercise image (if dynamically uploaded)
    private int drawableResId; // local drawable resource ID for fallback
    private int exerciseDuration;
    private float caloriesBurned;

    // Empty constructor for Firebase
    public Exercise() {}

    // Constructor without exerciseId (for creating new objects before saving to Firebase)
    public Exercise(String exerciseName, String exerciseCategory, String exercisePicPath, int drawableResId, int exerciseDuration, float caloriesBurned) {
        this.exerciseName = exerciseName;
        this.exerciseCategory = exerciseCategory;
        this.exercisePicPath = exercisePicPath;
        this.drawableResId = drawableResId;
        this.exerciseDuration = Math.max(exerciseDuration, 0); // Ensure non-negative value
        this.caloriesBurned = Math.max(caloriesBurned, 0); // Ensure non-negative value
    }

    // Constructor with exerciseId (for retrieving objects from Firebase)
    public Exercise(String exerciseId, String exerciseName, String exerciseCategory, String exercisePicPath, int drawableResId, int exerciseDuration, float caloriesBurned) {
        this.exerciseId = exerciseId;
        this.exerciseName = exerciseName;
        this.exerciseCategory = exerciseCategory;
        this.exercisePicPath = exercisePicPath;
        this.drawableResId = drawableResId;
        this.exerciseDuration = Math.max(exerciseDuration, 0); // Ensure non-negative value
        this.caloriesBurned = Math.max(caloriesBurned, 0); // Ensure non-negative value
    }

    // Getters and Setters
    public String getExerciseId() {
        return exerciseId != null ? exerciseId : ""; // Default to an empty string if null
    }

    public void setExerciseId(String exerciseId) {
        this.exerciseId = exerciseId;
    }

    public String getExerciseName() {
        return exerciseName != null ? exerciseName : "Unknown Exercise";
    }

    public void setExerciseName(String exerciseName) {
        this.exerciseName = exerciseName != null ? exerciseName : "Unknown Exercise";
    }

    public String getExerciseCategory() {
        return exerciseCategory != null ? exerciseCategory : "Unknown Category";
    }

    public void setExerciseCategory(String exerciseCategory) {
        this.exerciseCategory = exerciseCategory != null ? exerciseCategory : "Unknown Category";
    }

    public String getExercisePicPath() {
        return exercisePicPath != null ? exercisePicPath : "";
    }

    public void setExercisePicPath(String exercisePicPath) {
        this.exercisePicPath = exercisePicPath;
    }

    public int getDrawableResId() {
        return drawableResId;
    }

    public void setDrawableResId(int drawableResId) {
        this.drawableResId = drawableResId;
    }

    public int getExerciseDuration() {
        return exerciseDuration;
    }

    public void setExerciseDuration(int exerciseDuration) {
        this.exerciseDuration = Math.max(exerciseDuration, 0);
    }

    public float getCaloriesBurned() {
        return caloriesBurned;
    }

    public void setCaloriesBurned(float caloriesBurned) {
        this.caloriesBurned = Math.max(caloriesBurned, 0);
    }

    // Helper Method to check db data integrity
    public boolean isValid() {
        return exerciseName != null && !exerciseName.isEmpty() &&
                exerciseCategory != null && !exerciseCategory.isEmpty();
    }

    // Override toString for debug
    @Override
    public String toString() {
        return "Exercise{" +
                "exerciseId='" + exerciseId + '\'' +
                ", exerciseName='" + exerciseName + '\'' +
                ", exerciseCategory='" + exerciseCategory + '\'' +
                ", exercisePicPath='" + exercisePicPath + '\'' +
                ", drawableResId=" + drawableResId +
                ", exerciseDuration=" + exerciseDuration +
                ", caloriesBurned=" + caloriesBurned +
                '}';
    }
}
