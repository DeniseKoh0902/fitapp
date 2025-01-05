package com.example.fitapp.javahelperfile.ratingsystem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fitapp.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FeedbackAdapter extends RecyclerView.Adapter<FeedbackAdapter.FeedbackViewHolder> {

    private List<FeedbackWithUser> feedbackList;
    private Context context;


    public FeedbackAdapter(List<FeedbackWithUser> feedbackList, Context context) {
        this.feedbackList = feedbackList;
        this.context = context;
    }

    @NonNull
    @Override
    public FeedbackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.feedback_item, parent, false);
        return new FeedbackViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedbackViewHolder holder, int position) {
        FeedbackWithUser feedback = feedbackList.get(position);
        holder.usernameTextView.setText(feedback.getUsername());
        holder.feedbackTextView.setText(feedback.getFeedbackText());
        holder.ratingBar.setRating(feedback.getRating());

        loadProfilePictureFromDatabase(feedback.getUserId(), holder.profilePic);
    }

    @Override
    public int getItemCount() {
        return feedbackList.size();
    }

    public class FeedbackViewHolder extends RecyclerView.ViewHolder {
        TextView usernameTextView, feedbackTextView;
        CircleImageView profilePic;
        RatingBar ratingBar;

        public FeedbackViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.TVUserName);
            feedbackTextView = itemView.findViewById(R.id.TVUserFeedback);
            profilePic = itemView.findViewById(R.id.IVUserImage);
            ratingBar = itemView.findViewById(R.id.RatingUserFeedback);
        }
    }

    private void loadProfilePictureFromDatabase(String userId, CircleImageView profilePic) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("User").child(userId);

        userRef.child("profile_pic_path").get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().getValue() != null) {
                String imageUrl = task.getResult().getValue(String.class);
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    Glide.with(context)
                            .load(imageUrl)
                            .circleCrop()
                            .placeholder(R.drawable.profilepic)
                            .error(R.drawable.profilepic)
                            .into(profilePic);
                } else {
                    profilePic.setImageResource(R.drawable.profilepic);
                }
            } else {
                profilePic.setImageResource(R.drawable.profilepic);
            }
        });
    }


}
