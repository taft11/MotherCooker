package com.example.recipe;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {
    private final List<Recipes> recipes;
    private Context context;
    private final OnRecipeClickListener listener;

    public RecipeAdapter(List<Recipes> recipes, OnRecipeClickListener listener) {
        this.recipes = recipes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.recipe_item, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Recipes recipe = recipes.get(position);
        holder.titleTextView.setText(recipe.getTitle());
        holder.descriptionTextView.setText(recipe.getDescription());
        Glide.with(context)
                .load(recipe.getImageUrl())  // URL for the recipe image
                .into(holder.recipeImage);
        holder.itemView.setOnClickListener(v -> listener.onRecipeClick(recipe));
        holder.averageRatingTextView.setText(String.format("Average Rating: %.1f", recipe.getAverageRating()));
        holder.numberOfLikesTextView.setText(String.valueOf(recipe.getNumberOfLikes()));
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    public interface OnRecipeClickListener {
        void onRecipeClick(Recipes recipe);
    }

    static class RecipeViewHolder extends RecyclerView.ViewHolder {
        ImageView recipeImage;
        TextView titleTextView;
        TextView descriptionTextView;
        TextView averageRatingTextView;
        TextView numberOfLikesTextView;

        RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            recipeImage = itemView.findViewById(R.id.recipe_image); // Ensure you have this in your layout
            titleTextView = itemView.findViewById(R.id.recipe_title); // Ensure you have this in your layout
            descriptionTextView = itemView.findViewById(R.id.recipe_description); // Ensure you have this in your layout
            averageRatingTextView = itemView.findViewById(R.id.average_rating_text_view); // Ensure you have this in your layout
            numberOfLikesTextView = itemView.findViewById(R.id.number_of_likes);
        }
    }
}
