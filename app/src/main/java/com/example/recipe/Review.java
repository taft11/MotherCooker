package com.example.recipe;

public class Review {
    private String reviewText;
    private float rating; // Change this to float

    public Review() {
        // Required empty constructor for Firestore
    }

    public Review(String reviewText, float rating) {
        this.reviewText = reviewText;
        this.rating = rating;
    }

    public String getReviewText() {
        return reviewText;
    }

    public float getRating() {
        return rating; // Return the rating
    }
}