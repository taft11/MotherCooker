package com.example.recipe;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class Profile extends AppCompatActivity {

    private Button logoutButton;
    private ImageView profileIcon;
    private RecyclerView userRecipesRecyclerView;
    private RecipeBind recipeAdapter;
    private List<Recipes> userRecipeList;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        // Set up the action bar with a back button
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        // Initialize Firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        TextView ownedRecipesText = findViewById(R.id.owned_recipes_text);

        // Initialize views
        profileIcon = findViewById(R.id.profile_icon);
        logoutButton = findViewById(R.id.logout_button);
        userRecipesRecyclerView = findViewById(R.id.user_recipes_recyclerview);

        // Setup RecyclerView
        userRecipeList = new ArrayList<>();
        recipeAdapter = new RecipeBind(this, userRecipeList);
        userRecipesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        userRecipesRecyclerView.setAdapter(recipeAdapter);

        // Load user recipes
        loadUserRecipes();

        // Handle logout button click
        logoutButton.setOnClickListener(v -> {
            // Navigate back to LoginActivity
            auth.signOut(); // Sign out the user
            Intent intent = new Intent(Profile.this, Login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish(); // Close the Profile activity
        });
    }

    // Load recipes created by the logged-in user
    private void loadUserRecipes() {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            db.collection("recipes")
                    .whereEqualTo("userId", userId) // Query recipes by user ID
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        userRecipeList.clear();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            Recipes recipe = document.toObject(Recipes.class);
                            userRecipeList.add(recipe);
                        }
                        recipeAdapter.notifyDataSetChanged();
                    })
                    .addOnFailureListener(e -> {
                        // Handle error
                    });
        }
    }

    // Handle the action bar back button click
    @Override
    public boolean onSupportNavigateUp() {
        finish(); // Close the current activity and go back to the previous one
        return true;
    }
}