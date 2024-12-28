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
import com.example.fitapp.javahelperfile.profile.User;
import com.example.fitapp.ui.exercise.ExerciseListActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder> {
    private List<Exercise> exerciseList;
    private String category;
    private Context context;
    private User user;

    // Constructor
    public ExerciseAdapter(User user, Context context, List<Exercise> exerciseList, String category) {
        this.user = user;
        this.context = context;
        this.exerciseList = exerciseList;
        this.category = category;
    }

    @NonNull
    @Override
    public ExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.exercise_item, parent, false);
        return new ExerciseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseViewHolder holder, int position) {
        Exercise exercise = exerciseList.get(position);

        // Set exercise details
        holder.exerciseName.setText(exercise.getExerciseName());
        holder.exerciseDuration.setText(String.format("%d min", exercise.getExerciseDuration()));

        String picPath = exercise.getExercisePicPath();
        if (picPath != null && !picPath.isEmpty()) {
            Glide.with(context)
                    .load(picPath)  // Use the pictire path or URL directly from Firebase
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

        setBackgroundBasedOnCategory(holder.exerciseItemLayout);

        holder.addButton.setOnClickListener(v -> {
            if (user == null || user.getUserId() == null) {
                Toast.makeText(context, "User not logged in. Please log in to add exercises.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Ensure context is an instance of ExerciseListActivity before calling the addExercise method
            if (context instanceof ExerciseListActivity) {
                ExerciseListActivity activity = (ExerciseListActivity) context;
                activity.onAddExercise(exercise);  // Pass exercise to Activity
            }
        });
    }

    @Override
    public int getItemCount() {
        return exerciseList.size();
    }

    // ViewHolder class
    public static class ExerciseViewHolder extends RecyclerView.ViewHolder {
        TextView exerciseName;
        TextView exerciseDuration;
        RelativeLayout exerciseItemLayout;
        ImageView exerciseImage;
        ImageButton addButton;

        public ExerciseViewHolder(View itemView) {
            super(itemView);
            exerciseName = itemView.findViewById(R.id.TVExerciseName);
            exerciseDuration = itemView.findViewById(R.id.TVDuration);
            exerciseImage = itemView.findViewById(R.id.ImageExercise);
            addButton = itemView.findViewById(R.id.BtnAdd);
            exerciseItemLayout = itemView.findViewById(R.id.RLExercise);
        }
    }

    //method to handle background setting based on the category
    private void setBackgroundBasedOnCategory(RelativeLayout exerciseItemLayout) {
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
