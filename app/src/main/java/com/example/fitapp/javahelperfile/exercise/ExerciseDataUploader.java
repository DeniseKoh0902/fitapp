package com.example.fitapp.javahelperfile.exercise;

import com.example.fitapp.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class ExerciseDataUploader {

    public void uploadExercises() {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference exercisesRef = database.getReference("Exercises");

        // List to store predefined exercises
        List<Exercise> exercises = new ArrayList<>();

        // Aerobic Exercises
        exercises.add(new Exercise("Jogging", "Aerobic", "", R.drawable.jogging, 30, 300));
        exercises.add(new Exercise("Cycling", "Aerobic", "", R.drawable.cycling, 45, 400));
        exercises.add(new Exercise("Swimming", "Aerobic", "", R.drawable.swimming, 30, 250));
        exercises.add(new Exercise("Dancing", "Aerobic", "", R.drawable.dancing, 45, 400));
        exercises.add(new Exercise("Jump Rope", "Aerobic", "", R.drawable.jump_rope, 15, 200));
        exercises.add(new Exercise("Hiking", "Aerobic", "", R.drawable.hiking, 60, 350));
        exercises.add(new Exercise("Elliptical training", "Aerobic", "", R.drawable.elliptical_training, 30, 300));

        // Flexibility Exercises
        exercises.add(new Exercise("Yoga", "Flexibility", "", R.drawable.yoga, 30, 150));
        exercises.add(new Exercise("Pilates", "Flexibility", "", R.drawable.yoga, 40, 200));
        exercises.add(new Exercise("Side Lunges", "Flexibility", "", R.drawable.side_lungs, 10, 50));
        exercises.add(new Exercise("Seated Hamstring Stretch", "Flexibility", "", R.drawable.yoga, 10, 30));
        exercises.add(new Exercise("Cat-Cow Stretch", "Flexibility", "", R.drawable.yoga, 10, 25));
        exercises.add(new Exercise("Chest Opener Stretch", "Flexibility", "", R.drawable.yoga, 10, 25));

        // Balance Exercises
        exercises.add(new Exercise("Tai Chi", "Balance", "", R.drawable.taichi, 30, 120));
        exercises.add(new Exercise("Standing on One Foot", "Balance", "", R.drawable.stand_by_one_foot, 10, 30));
        exercises.add(new Exercise("Stability Ball Exercises", "Balance", "", R.drawable.stability_ball, 30, 150));
        exercises.add(new Exercise("Bird Dog Exercises", "Balance", "", R.drawable.bird_dog, 15, 80));
        exercises.add(new Exercise("Flamingo Stand", "Balance", "", R.drawable.flamingo_stand, 15, 40));

        // HIIT Exercises
        exercises.add(new Exercise("Circuit Training", "HIIT", "", R.drawable.circuit_training, 30, 300));
        exercises.add(new Exercise("Burpees", "HIIT", "", R.drawable.burpees, 10, 150));
        exercises.add(new Exercise("Mountain Climbers", "HIIT", "", R.drawable.mountain_climbers, 15, 120));
        exercises.add(new Exercise("High Knees", "HIIT", "", R.drawable.high_knee, 10, 100));
        exercises.add(new Exercise("Jump Squats", "HIIT", "", R.drawable.jump_squats, 15, 120));
        exercises.add(new Exercise("Skater Jumps", "HIIT", "", R.drawable.skater_jumps, 10, 90));

        // Strength Exercises
        exercises.add(new Exercise("Push-Ups", "Strength", "", R.drawable.push_ups, 15, 100));
        exercises.add(new Exercise("Pull-Ups", "Strength", "", R.drawable.pull_ups, 10, 80));
        exercises.add(new Exercise("Squats", "Strength", "", R.drawable.squats, 20, 100));
        exercises.add(new Exercise("Deadlifts", "Strength", "", R.drawable.deadlifts, 30, 150));
        exercises.add(new Exercise("Sit-Ups", "Strength", "", R.drawable.sit_ups, 15, 100));
        exercises.add(new Exercise("Bicycle Crunch", "Strength", "", R.drawable.bicycle_crunch, 15, 120));
        exercises.add(new Exercise("Plank", "Strength", "", R.drawable.plank, 10, 50));

        // Upload each exercise to Firebase
        for (Exercise exercise : exercises) {
            // Generate a unique ID for the exercise
            DatabaseReference newExerciseRef = exercisesRef.push();
            String exerciseId = newExerciseRef.getKey();  // Get the unique key

            // Set the exerciseId to the exercise object
            exercise.setExerciseId(exerciseId);

            // Upload the exercise to Firebase
            newExerciseRef.setValue(exercise, (error, ref) -> {
                if (error != null) {
                    System.out.println("Failed to upload " + exercise.getExerciseName() + ": " + error.getMessage());
                } else {
                    System.out.println("Successfully uploaded " + exercise.getExerciseName());
                }
            });
        }
    }
}