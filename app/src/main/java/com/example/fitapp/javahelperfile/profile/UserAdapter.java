package com.example.fitapp.javahelperfile.profile;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.fitapp.R;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.text.ParseException;
import java.util.Locale;
public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private List<User> userList;

    public UserAdapter(List<User> userList) {
        this.userList = userList;
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        User userItem = userList.get(position);
        holder.usernameTextView.setText(userItem.getUsername());

        String signUpDate = userItem.getSignUpDate();
        String formattedDate = formatSignUpDate(signUpDate);
        holder.userSignUpDate.setText("Joined at " + formattedDate);

        String imageUrl = userItem.getProfilePicPath();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(holder.profileImageView.getContext())
                    .load(imageUrl)
                    .circleCrop()
                    .placeholder(R.drawable.profilepic)
                    .into(holder.profileImageView);
        } else {
            holder.profileImageView.setImageResource(R.drawable.profilepic);
        }
    }

    private String formatSignUpDate(String signUpDate) {
        try {
            SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
            isoFormat.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
            Date date = isoFormat.parse(signUpDate);

            SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView usernameTextView;
        TextView userSignUpDate;
        ImageView profileImageView;


        public UserViewHolder(View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.TVUserName);
            profileImageView = itemView.findViewById(R.id.IVUserImage);
            userSignUpDate = itemView.findViewById(R.id.TVSignUpDate);
        }
    }
}

