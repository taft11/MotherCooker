package com.example.recipe;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class EditRecipeList extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecipeAdapter recipeAdapter; // Create this adapter to manage the list
    private List<Recipes> recipesList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_recipe_list);

        recyclerView = findViewById(R.id.recycler_view);
        recipesList = new ArrayList<>();
        recipeAdapter = new RecipeAdapter(recipesList, this::onRecipeClick);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(recipeAdapter);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        db = FirebaseFirestore.getInstance();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            fetchRecipes();
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            Log.e("EditRecipeList", "User ID is null");
        }
    }

    private void fetchRecipes() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid(); // Replace with actual user ID

        db.collection("recipes")
                .whereEqualTo("userId", userId) // Assuming you have userId stored in your recipe
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Recipes recipe = document.toObject(Recipes.class);
                            recipe.setRecipeId(document.getId()); // Set the document ID
                            recipesList.add(recipe);
                        }
                        recipeAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(EditRecipeList.this, "Failed to fetch recipes", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void onRecipeClick(Recipes recipe) {
        Log.d("EditRecipe", "Recipe clicked: " + recipe.getTitle());
        Intent intent = new Intent(this, EditRecipe.class);
        intent.putExtra("recipe", recipe); // Pass the entire recipe object
        startActivity(intent);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // Close the activity and return to the previous one
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
