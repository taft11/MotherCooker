package com.example.recipe;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {
    private List<Review> reviewList;

    public ReviewAdapter(List<Review> reviewList) {
        this.reviewList = reviewList;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_item, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Review review = reviewList.get(position);
        holder.reviewTextView.setText(review.getReviewText());
        holder.reviewRating.setRating(review.getRating());
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }
    public void updateReviewList(List<Review> newReviews) {
        reviewList.clear(); // Clear the current list
        reviewList.addAll(newReviews); // Add the new list of reviews
        notifyDataSetChanged(); // Notify the adapter to refresh the UI
    }

    static class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView reviewTextView;
        RatingBar reviewRating;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            reviewTextView = itemView.findViewById(R.id.review_text);
            reviewRating = itemView.findViewById(R.id.review_rating);
        }
    }
}
