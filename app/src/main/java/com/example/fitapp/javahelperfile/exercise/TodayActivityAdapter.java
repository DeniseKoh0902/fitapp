package com.example.fitapp.javahelperfile.exercise;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fitapp.R;
import com.example.fitapp.ui.exercise.TodayActivity;

import java.util.List;

public class TodayActivityAdapter extends RecyclerView.Adapter<TodayActivityAdapter.TodayActivityViewHolder> {
    private List<Exercise> exerciseList;
    private String mode;
    private Context context;

    // Constructor
    public TodayActivityAdapter(Context context, List<Exercise> exerciseList, String mode) {
        this.context = context;
        this.exerciseList = exerciseList;
        this.mode = mode;
    }

    @NonNull
    @Override
    public TodayActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Choose layout based on mode
        int layoutId = mode.equals("activity_today") ? R.layout.exercise_item_today : R.layout.exercise_item_edit_today;
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        return new TodayActivityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TodayActivityViewHolder holder, int position) {
        Exercise exercise = exerciseList.get(position);

        // Set exercise details
        holder.exerciseName.setText(exercise.getExerciseName());
        holder.exerciseDuration.setText(String.format("%d min", exercise.getExerciseDuration()));

        String picPath = exercise.getExercisePicPath();
        if (picPath != null && !picPath.isEmpty()) {
            Glide.with(context)
                    .load(picPath)  // Use the URL directly from Firebase
                    .placeholder(R.drawable.jogging)  // Optional: Placeholder while loading
                    .error(R.drawable.jogging)  // Optional: Error image in case of failure
                    .into(holder.exerciseImage);
        } else {
            // If no URL, load image from drawableResId
            int imageResId = exercise.getDrawableResId();
            if (imageResId != 0) {
                holder.exerciseImage.setImageResource(imageResId); // Use the drawable resource ID
            } else {
                // Set a default image if neither URL nor drawableResId is found
                holder.exerciseImage.setImageResource(R.drawable.jogging);
            }
        }

        // Set background color based on category
        setBackgroundBasedOnCategory(holder.exerciseItemLayout, exercise.getExerciseCategory());

        // Handle button clicks
        holder.actionButton.setOnClickListener(v -> {
            if (mode.equals("activity_today")) {
                // Handle Start button click
                Toast.makeText(context, "Starting " + exercise.getExerciseName(), Toast.LENGTH_SHORT).show();
            } else if (mode.equals("activity_today_edit")) {
                // Handle Delete button click
                if (context instanceof TodayActivity) {
                    TodayActivity activity = (TodayActivity) context;
                    activity.onDeleteExercise(exercise);
                }
            }
        });

        // Update the button image based on the mode
        if (mode.equals("activity_today")) {
            holder.actionButton.setImageResource(R.drawable.outline_arrow_circle_right_24);  // Start button icon
        } else if (mode.equals("activity_today_edit")) {
            holder.actionButton.setImageResource(R.drawable.baseline_delete_outline_24);  // Delete button icon
        }
    }

    @Override
    public int getItemCount() {
        return exerciseList.size();
    }

    public void updateMode(String newMode) {
        this.mode = newMode;
        notifyDataSetChanged(); // Notify the adapter to update the layout
    }

    // ViewHolder class
    public static class TodayActivityViewHolder extends RecyclerView.ViewHolder {
        TextView exerciseName;
        TextView exerciseDuration;
        RelativeLayout exerciseItemLayout;
        ImageView exerciseImage;
        ImageButton actionButton;

        public TodayActivityViewHolder(View itemView) {
            super(itemView);
            exerciseName = itemView.findViewById(R.id.TVExerciseName);
            exerciseDuration = itemView.findViewById(R.id.TVDuration);
            exerciseImage = itemView.findViewById(R.id.ImageExercise);
            actionButton = itemView.findViewById(R.id.BtnAction);
            exerciseItemLayout = itemView.findViewById(R.id.RLExercise);
        }
    }

    // Set background based on category
    private void setBackgroundBasedOnCategory(RelativeLayout exerciseItemLayout, String category) {
        switch (category) {
            case "Aerobic":
                exerciseItemLayout.setBackgroundResource(R.drawable.border_background_solidpink1);
                break;
            case "Flexibility":
                exerciseItemLayout.setBackgroundResource(R.drawable.border_background_solidyellow);
                break;
            case "Balance":
                exerciseItemLayout.setBackgroundResource(R.drawable.border_background_solidgreen1);
                break;
            case "HIIT":
                exerciseItemLayout.setBackgroundResource(R.drawable.border_background_solidgreen);
                break;
            case "Strength":
                exerciseItemLayout.setBackgroundResource(R.drawable.border_background_solidpurple);
                break;
            default:
                exerciseItemLayout.setBackgroundResource(R.drawable.border_background_solidpink1);
                break;
        }
    }
}
