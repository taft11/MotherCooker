package com.example.recipe;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RecipeDetails extends AppCompatActivity {

    private TextView titleTextView, descriptionTextView, ingredientsTextView, stepsTextView;
    private TextView servingSizeTextView, caloriesTextView, proteinTextView, fatTextView, carbohydratesTextView, sodiumTextView;
    private TextView averageRatingTextView, numberOfRatingsTextView;
    private TextView preparationTimeTextView, cookingTimeTextView;
    private Button addToShoppingListButton;
    private ImageView recipeImageView;
    private List<String> ingredients;
    private String recipeId;
    private EditText reviewInput;
    private Button submitReviewButton;
    private RecyclerView reviewRecyclerView;
    private ReviewAdapter reviewAdapter;
    private List<Review> reviewList;
    private RatingBar ratingBar;
    private Button likeButton;
    private boolean isLiked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipedetails);

        // Initialize the TextView elements
        titleTextView = findViewById(R.id.detail_title);
        likeButton = findViewById(R.id.like_button);
        recipeImageView = findViewById(R.id.detail_recipe_image);
        descriptionTextView = findViewById(R.id.detail_description);
        preparationTimeTextView = findViewById(R.id.preparation_time_text_view);
        cookingTimeTextView = findViewById(R.id.cooking_time_text_view);
        ingredientsTextView = findViewById(R.id.detail_ingredients);
        stepsTextView = findViewById(R.id.detail_steps);
        averageRatingTextView = findViewById(R.id.average_rating_text_view);
        numberOfRatingsTextView = findViewById(R.id.number_of_ratings_text_view);
        reviewInput = findViewById(R.id.review_input);
        submitReviewButton = findViewById(R.id.submit_review_button);
        reviewRecyclerView = findViewById(R.id.review_recycler_view);
        reviewList = new ArrayList<>();
        ratingBar = findViewById(R.id.rating_bar);
        servingSizeTextView = findViewById(R.id.serving_size_text_view);
        caloriesTextView = findViewById(R.id.calories_text_view);
        proteinTextView = findViewById(R.id.protein_text_view);
        fatTextView = findViewById(R.id.fat_text_view);
        carbohydratesTextView = findViewById(R.id.carbohydrates_text_view);
        sodiumTextView = findViewById(R.id.sodium_text_view);

        reviewRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        reviewAdapter = new ReviewAdapter(reviewList);
        reviewRecyclerView.setAdapter(reviewAdapter);

        // Retrieve the recipe ID passed from MainActivity
        recipeId = getIntent().getStringExtra("RECIPE_ID");

        checkIfRecipeLikedByUser();
        likeButton.setOnClickListener(v -> toggleLikeStatus());

        // Fetch recipe details from Firestore
        if (recipeId != null) {
            fetchRecipeDetails(recipeId);
        } else {
            Toast.makeText(this, "Error: Recipe ID is missing.", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity if the ID is missing
        }

        // Set up action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Recipe Details");
        }

        // Initialize the Add to Shopping List button
        addToShoppingListButton = findViewById(R.id.add_to_shopping_list_button);
        addToShoppingListButton.setOnClickListener(v -> {
            if (ingredients != null && !ingredients.isEmpty()) {
                addIngredientsToShoppingList(ingredients);
            } else {
                Toast.makeText(this, "No ingredients to add", Toast.LENGTH_SHORT).show();
            }
        });
        // Set up the button click listener to submit the review
        submitReviewButton.setOnClickListener(v -> {
            String reviewText = reviewInput.getText().toString().trim();
            float rating = ratingBar.getRating(); // Get the rating from the RatingBar
            if (!reviewText.isEmpty() && rating > 0) { // Ensure a rating is selected
                submitReview(reviewText, rating);
            } else {
                Toast.makeText(this, "Please enter a review and select a rating", Toast.LENGTH_SHORT).show();
            }
        });
        String recipeId = getIntent().getStringExtra("RECIPE_ID");
        fetchRecipeDetails(recipeId);
        fetchReviews(recipeId);
    }

    private void fetchRecipeDetails(String recipeId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("recipes").document(recipeId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String title = documentSnapshot.getString("title");
                        String description = documentSnapshot.getString("description");
                        ingredients = (List<String>) documentSnapshot.get("ingredients");
                        List<String> steps = (List<String>) documentSnapshot.get("steps");
                        String imageUrl = documentSnapshot.getString("imageUrl");

                        if (imageUrl != null) {
                            Glide.with(this) // Make sure to import Glide
                                    .load(imageUrl)
                                    .into(recipeImageView);
                        }

                        String preparationTime = documentSnapshot.contains("preparationTime") ?
                                String.valueOf(documentSnapshot.get("preparationTime")) : "N/A";
                        String cookingTime = documentSnapshot.contains("cookingTime") ?
                                String.valueOf(documentSnapshot.get("cookingTime")) : "N/A";

                        preparationTimeTextView.setText("Preparation Time: " + preparationTime + " Minutes");
                        cookingTimeTextView.setText("Cooking Time: " + cookingTime + " Minutes");

                        String servingSize = documentSnapshot.getString("servingSize") != null ? documentSnapshot.getString("servingSize") : "0";
                        double calories = documentSnapshot.getDouble("calories") != null ? documentSnapshot.getDouble("calories") : 0;
                        double protein = documentSnapshot.getDouble("protein") != null ? documentSnapshot.getDouble("protein") : 0;
                        double fat = documentSnapshot.getDouble("fat") != null ? documentSnapshot.getDouble("fat") : 0;
                        double carbohydrates = documentSnapshot.getDouble("carbohydrates") != null ? documentSnapshot.getDouble("carbohydrates") : 0;
                        double sodium = documentSnapshot.getDouble("sodium") != null ? documentSnapshot.getDouble("sodium") : 0;

                        servingSizeTextView.setText("Servings: " + servingSize);
                        caloriesTextView.setText("Calories: " + calories + " kcal");
                        proteinTextView.setText("Protein: " + protein + " g");
                        fatTextView.setText("Fat: " + fat + " g");
                        carbohydratesTextView.setText("Carbohydrates: " + carbohydrates + " g");
                        sodiumTextView.setText("Sodium: " + sodium + " mg");

                        titleTextView.setText(title);
                        descriptionTextView.setText(description);
                        ingredientsTextView.setText(formatListAsString(ingredients, "Ingredients"));
                        stepsTextView.setText(formatListAsString(steps, "Steps"));


                        // Safely handle the null value for averageRating
                        Double averageRatingValue = documentSnapshot.getDouble("averageRating");
                        float averageRating = (averageRatingValue != null) ? averageRatingValue.floatValue() : 0.0f;

                        long numberOfRatings = documentSnapshot.getLong("numberOfRatings") != null
                                ? documentSnapshot.getLong("numberOfRatings")
                                : 0;

                        // Set the recipe details to the TextViews
                        titleTextView.setText(title);
                        descriptionTextView.setText(description);
                        ingredientsTextView.setText(formatListAsString(ingredients, "Ingredients"));
                        preparationTimeTextView.setText("Preparation Time: " + preparationTime + "Minutes");
                        cookingTimeTextView.setText("Cooking Time: " + cookingTime + "Minutes");
                        stepsTextView.setText(formatListAsString(steps, "Steps"));
                        averageRatingTextView.setText("Average Rating: " + averageRating);
                        numberOfRatingsTextView.setText("Number of Ratings: " + numberOfRatings);
                    } else {
                        Toast.makeText(this, "Recipe not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error fetching recipe details", Toast.LENGTH_SHORT).show();
                });
    }

    private void checkIfRecipeLikedByUser() {
        // Get the current user ID
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("recipes").document(recipeId).collection("likes")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        isLiked = true;
                        likeButton.setText("Unlike");
                    } else {
                        isLiked = false;
                        likeButton.setText("Like");
                    }
                });
    }
    private void toggleLikeStatus() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (isLiked) {
            // Unlike the recipe by removing the user's like entry
            db.collection("recipes").document(recipeId).collection("likes")
                    .document(userId)
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Recipe unliked", Toast.LENGTH_SHORT).show();
                        isLiked = false;
                        likeButton.setText("Like");

                        // Remove user ID from likedByUsers subcollection
                        db.collection("recipes").document(recipeId)
                                .collection("likedByUsers").document(userId)
                                .delete()
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "Error removing like", Toast.LENGTH_SHORT).show();
                                });

                        // Decrement the number of likes
                        updateLikeCount(-1);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error unliking recipe", Toast.LENGTH_SHORT).show();
                    });
        } else {
            // Like the recipe by adding the user's like entry
            db.collection("recipes").document(recipeId).collection("likes")
                    .document(userId)
                    .set(new HashMap<>()) // Empty map to signify the like
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Recipe liked", Toast.LENGTH_SHORT).show();
                        isLiked = true;
                        likeButton.setText("Unlike");

                        // Add user ID to likedByUsers subcollection
                        db.collection("recipes").document(recipeId)
                                .collection("likedByUsers").document(userId)
                                .set(new HashMap<>())
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "Error adding like", Toast.LENGTH_SHORT).show();
                                });

                        // Increment the number of likes
                        updateLikeCount(1);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error liking recipe", Toast.LENGTH_SHORT).show();
                    });
        }
    }
    private void updateLikeCount(int change) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("recipes").document(recipeId)
                .update("numberOfLikes", FieldValue.increment(change))
                .addOnSuccessListener(aVoid -> {
                    Log.d("RecipeDetails", "Like count updated successfully");
                })
                .addOnFailureListener(e -> {
                    Log.e("RecipeDetails", "Error updating like count", e);
                });
    }


    private void addIngredientsToShoppingList(List<String> ingredients) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        for (String ingredient : ingredients) {
            // Generate a unique ID (or retrieve it if you have a specific way to generate IDs)
            String uniqueId = db.collection("shopping_list").document().getId();  // Creates a new document ID

            // Create the shopping item with all required parameters
            ShoppingItem shoppingItem = new ShoppingItem(uniqueId, ingredient, "Uncategorized", false); // false means unchecked
            db.collection("shopping_list").add(shoppingItem)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(this, "Added to shopping list", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error adding to shopping list", Toast.LENGTH_SHORT).show();
                    });
        }
    }
    private void fetchReviews(String recipeId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("recipes").document(recipeId).collection("reviews")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Review> reviews = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult()) {
                            Review review = document.toObject(Review.class);
                            reviews.add(review);
                        }
                        reviewAdapter.updateReviewList(reviews); // Update your adapter
                    } else {
                        Log.e("RecipeDetails", "Error fetching reviews", task.getException());
                    }
                });

    }
    private void submitReview(String reviewText, float rating) {
        String recipeId = getIntent().getStringExtra("RECIPE_ID");
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Review review = new Review(reviewText, rating);

        // Store the review
        db.collection("recipes").document(recipeId).collection("reviews").add(review)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Review submitted", Toast.LENGTH_SHORT).show();
                    fetchReviews(recipeId); // Refresh the review list
                    updateRecipeRating(recipeId, rating); // Update the recipe rating
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error submitting review", Toast.LENGTH_SHORT).show();
                });
    }
    private void updateRecipeRating(String recipeId, float newRating) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("recipes").document(recipeId).collection("reviews")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        int totalRatings = task.getResult().size();
                        float sumRatings = 0;

                        for (DocumentSnapshot document : task.getResult()) {
                            Review review = document.toObject(Review.class);
                            sumRatings += review.getRating();
                        }

                        // Include the new rating in the total
                        sumRatings += newRating;
                        totalRatings++;

                        // Calculate the average rating
                        float averageRating = sumRatings / totalRatings;

                        // Update the recipe document with the new average rating
                        db.collection("recipes").document(recipeId)
                                .update("averageRating", averageRating, "numberOfRatings", totalRatings)
                                .addOnSuccessListener(aVoid -> {
                                    Log.d("RecipeDetails", "Recipe rating updated successfully");
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("RecipeDetails", "Error updating recipe rating", e);
                                });
                    }
                });
    }



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // Handle back button click
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private String formatListAsString(List<String> list, String title) {
        StringBuilder formattedString = new StringBuilder(title + ":\n");
        for (int i = 0; i < list.size(); i++) {
            formattedString.append(i + 1).append(". ").append(list.get(i)).append("\n");
        }
        return formattedString.toString();
    }
}
