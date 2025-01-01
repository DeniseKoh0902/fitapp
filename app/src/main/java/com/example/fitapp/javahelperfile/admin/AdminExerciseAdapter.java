package com.example.fitapp.javahelperfile.admin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fitapp.R;
import com.example.fitapp.ui.admin.EditExercise;

import java.util.List;

public class AdminExerciseAdapter extends RecyclerView.Adapter<AdminExerciseAdapter.ViewHolder> {

    private Context context;
    private List<AdminExercise> adminExerciseList;
    private OnEditClickListener onEditClickListener;

    public interface OnEditClickListener {
        void onEditClick(AdminExercise exercise);
    }

    public AdminExerciseAdapter(Context context, List<AdminExercise> adminExerciseList, OnEditClickListener onEditClickListener) {
        this.context = context;
        this.adminExerciseList = adminExerciseList;
        this.onEditClickListener = onEditClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.exercise_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AdminExercise exercise = adminExerciseList.get(position);

        holder.tvExerciseName.setText(exercise.getExerciseName());
        holder.tvDuration.setText(exercise.getExerciseDuration() + " minutes");
//        holder.imageExercise.setImageResource(exercise.getDrawableResId());

        // Check if exercisePicPath is null or empty
        String imageUrl = exercise.getExercisePicPath();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            // If there is an image URL, load it (you can use Glide if needed)
            Glide.with(context)
                    .load(imageUrl)  // Use the URL directly from Firebase
                    .placeholder(R.drawable.jogging)  // Optional: Placeholder while loading
                    .error(R.drawable.jogging)  // Optional: Error image in case of failure
                    .into(holder.imageExercise);
        } else {
            // Use a default drawable if image URL is empty or null
            holder.imageExercise.setImageResource(exercise.getDrawableResId());
        }

        holder.btnEdit.setOnClickListener(v -> {
            // Trigger the onEditClickListener callback
            onEditClickListener.onEditClick(exercise);

            // Create an Intent to navigate to EditExercise activity
            Intent intent = new Intent(context, EditExercise.class);

            // Ensure you're passing all necessary data
            intent.putExtra("exercise_name", exercise.getExerciseName());   // Pass exercise name
            intent.putExtra("exercise_duration", exercise.getExerciseDuration()); // Pass exercise duration
            intent.putExtra("exercise_image", exercise.getDrawableResId()); // Pass image resource ID

            // Start the EditExercise activity
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return adminExerciseList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvExerciseName, tvDuration;
        ImageView imageExercise;
        ImageButton btnEdit;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvExerciseName = itemView.findViewById(R.id.TVExerciseName);
            tvDuration = itemView.findViewById(R.id.TVDuration);
            imageExercise = itemView.findViewById(R.id.ImageExercise);
            btnEdit = itemView.findViewById(R.id.BtnEdit);
        }
    }
}
