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

import java.util.List;

public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder> {
    private List<Exercise> exerciseList;
    private String category;
    private Context context;
    private User user;

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

        holder.exerciseName.setText(exercise.getExerciseName());
        holder.exerciseDuration.setText(String.format("%d min", exercise.getExerciseDuration()));

        String picPath = exercise.getExercisePicPath();
        if (picPath != null && !picPath.isEmpty()) {
            Glide.with(context)
                    .load(picPath)
                    .placeholder(R.drawable.jogging)
                    .error(R.drawable.jogging)
                    .into(holder.exerciseImage);
        } else {
            int imageResId = exercise.getDrawableResId();
            if (imageResId != 0) {
                holder.exerciseImage.setImageResource(imageResId);
            } else {
                holder.exerciseImage.setImageResource(R.drawable.jogging);
            }
        }


        setBackgroundBasedOnCategory(holder.exerciseItemLayout);

        holder.addButton.setOnClickListener(v -> {
            if (user == null || user.getUserId() == null) {
                Toast.makeText(context, "User not logged in. Please log in to add exercises.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (context instanceof ExerciseListActivity) {
                ExerciseListActivity activity = (ExerciseListActivity) context;
                activity.onAddExercise(exercise);
            }
        });
    }

    @Override
    public int getItemCount() {
        return exerciseList.size();
    }

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
