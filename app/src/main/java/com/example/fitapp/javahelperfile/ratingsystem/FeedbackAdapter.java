package com.example.fitapp.javahelperfile.ratingsystem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

        // Load the profile picture from Firebase using Glide inside the adapter
        loadProfilePictureFromDatabase(feedback.getUserId(), holder.profilePic);
    }

    @Override
    public int getItemCount() {
        return feedbackList.size();
    }

    public class FeedbackViewHolder extends RecyclerView.ViewHolder {
        TextView usernameTextView, feedbackTextView;
        CircleImageView profilePic; // Use CircleImageView for profile picture
        RatingBar ratingBar;

        public FeedbackViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.TVUserName);
            feedbackTextView = itemView.findViewById(R.id.TVUserFeedback);
            profilePic = itemView.findViewById(R.id.IVUserImage); // CircleImageView for profile picture
            ratingBar = itemView.findViewById(R.id.RatingUserFeedback);
        }
    }

    // Method to load profile picture from database and set it circular
    private void loadProfilePictureFromDatabase(String userId, CircleImageView profilePic) {
        // Get a reference to the user's profile picture path in the database
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("User").child(userId);

        // Get the profile picture path from Firebase
        userRef.child("profile_pic_path").get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().getValue() != null) {
                String imageUrl = task.getResult().getValue(String.class);
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    // Use Glide to load the image into the CircleImageView
                    Glide.with(context)
                            .load(imageUrl)  // Load the image URL
                            .circleCrop()  // Make it circular
                            .placeholder(R.drawable.profilepic)  // Placeholder image while loading
                            .error(R.drawable.profilepic)  // Default image on error
                            .into(profilePic);  // Load into CircleImageView
                } else {
                    // If the image URL is empty, set the default profile image
                    profilePic.setImageResource(R.drawable.profilepic);
                }
            } else {
                // If the Firebase query fails, set the default profile image
                profilePic.setImageResource(R.drawable.profilepic);
            }
        });
    }


}
