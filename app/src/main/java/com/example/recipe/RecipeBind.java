package com.example.recipe;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class RecipeBind extends RecyclerView.Adapter<RecipeBind.RecipeViewHolder> {

    private Context context;
    private List<Recipes> recipeList;

    public RecipeBind(Context context, List<Recipes> recipeList) {
        this.context = context;
        this.recipeList = recipeList;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recipe_item, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Recipes recipe = recipeList.get(position);
        holder.titleTextView.setText(recipe.getTitle());
        holder.descriptionTextView.setText(recipe.getDescription());
        String recipeId = recipe.getRecipeId();

        String averageRatingText = String.format("Average Rating: %.1f", recipe.getAverageRating());
        holder.averageRating.setText(averageRatingText);
        holder.numberOfLikesTextView.setText(String.valueOf(recipe.getNumberOfLikes()));

        Glide.with(context)
                .load(recipe.getImageUrl())  // Load image from URL
                .into(holder.recipeImageView);



        // Set an onClick listener for the entire item view
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, RecipeDetails.class);
            intent.putExtra("title", recipe.getTitle());
            intent.putExtra("description", recipe.getDescription());
            intent.putStringArrayListExtra("ingredients", new ArrayList<>(recipe.getIngredients()));
            intent.putStringArrayListExtra("steps", new ArrayList<>(recipe.getSteps()));
            intent.putExtra("RECIPE_ID", recipeId);  // Pass the recipe ID
            context.startActivity(intent);  // Start the activity
        });
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    public void updateRecipeList(List<Recipes> newRecipeList) {
        this.recipeList = newRecipeList;
        notifyDataSetChanged();
    }

    public static class RecipeViewHolder extends RecyclerView.ViewHolder {
        ImageView recipeImageView;
        TextView titleTextView, descriptionTextView, averageRating, numberOfLikesTextView;

        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            recipeImageView = itemView.findViewById(R.id.recipe_image);
            titleTextView = itemView.findViewById(R.id.recipe_title);
            descriptionTextView = itemView.findViewById(R.id.recipe_description);
            averageRating = itemView.findViewById(R.id.average_rating_text_view);
            numberOfLikesTextView = itemView.findViewById(R.id.number_of_likes);
        }
    }
}
