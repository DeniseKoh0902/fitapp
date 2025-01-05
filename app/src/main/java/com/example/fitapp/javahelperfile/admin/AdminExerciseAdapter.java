package com.example.fitapp.javahelperfile.admin;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fitapp.R;
import com.example.fitapp.javahelperfile.profile.User;
import com.example.fitapp.ui.admin.EditExercise;

import java.util.List;

public class AdminExerciseAdapter extends RecyclerView.Adapter<AdminExerciseAdapter.ViewHolder> {

    private Context context;
    private List<AdminExercise> adminExerciseList;
    private OnEditClickListener onEditClickListener;

    private User user;
    private boolean isAdmin;

    public interface OnEditClickListener {
        void onEditClick(AdminExercise exercise);
    }

    public AdminExerciseAdapter(Context context, List<AdminExercise> adminExerciseList, OnEditClickListener onEditClickListener, User user, boolean isAdmin) {
        this.context = context;
        this.adminExerciseList = adminExerciseList;
        this.onEditClickListener = onEditClickListener;
        this.user=user;
        this.isAdmin=isAdmin;
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

        String imageUrl = exercise.getExercisePicPath();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.jogging)
                    .error(R.drawable.jogging)
                    .into(holder.imageExercise);
        } else {

            holder.imageExercise.setImageResource(exercise.getDrawableResId());
        }


        setBackgroundBasedOnCategory(holder.exerciseItemLayout, exercise.getExerciseCategory());

        holder.btnEdit.setOnClickListener(v -> {
            onEditClickListener.onEditClick(exercise);

            Intent intent = new Intent(context, EditExercise.class);
            intent.putExtra("user",user);
            intent.putExtra("isAdmin",isAdmin);
            intent.putExtra("exercise_name", exercise.getExerciseName());
            intent.putExtra("exercise_duration", exercise.getExerciseDuration());
            intent.putExtra("exercise_image", exercise.getDrawableResId());


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
        RelativeLayout exerciseItemLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvExerciseName = itemView.findViewById(R.id.TVExerciseName);
            tvDuration = itemView.findViewById(R.id.TVDuration);
            imageExercise = itemView.findViewById(R.id.ImageExercise);
            btnEdit = itemView.findViewById(R.id.BtnEdit);
            exerciseItemLayout = itemView.findViewById(R.id.RLExercise);
        }
    }

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