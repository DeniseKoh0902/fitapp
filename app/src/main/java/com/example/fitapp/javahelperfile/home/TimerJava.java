package com.example.fitapp.javahelperfile.home;

public class TimerJava {
    private String activityName;
    private int timeSpent;
    private int caloriesBurned;

    public TimerJava() {}

    public TimerJava(String activityName, int timeSpent, int caloriesBurned) {
        this.activityName = activityName;
        this.timeSpent = timeSpent;
        this.caloriesBurned = caloriesBurned;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public int getTimeSpent() {
        return timeSpent;
    }

    public void setTimeSpent(int timeSpent) {
        this.timeSpent = timeSpent;
    }

    public int getCaloriesBurned() {
        return caloriesBurned;
    }

    public void setCaloriesBurned(int caloriesBurned) {
        this.caloriesBurned = caloriesBurned;
    }
}
