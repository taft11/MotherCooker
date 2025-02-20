package com.example.recipe;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.navigation.NavigationView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recipesRecyclerView;
    private RecipeBind recipesAdapter;
    private List<Recipes> recipeList;
    private FirebaseFirestore db;
    private EditText searchEditText;
    private Button categoryBreakfast, categoryLunch, categoryDinner, categoryDesserts;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Spinner filterSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchEditText = findViewById(R.id.search_edit_text);
        recipesRecyclerView = findViewById(R.id.recipes_recycler_view);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        categoryBreakfast = findViewById(R.id.categoryBreakfast);
        categoryLunch = findViewById(R.id.categoryLunch);
        categoryDinner = findViewById(R.id.categoryDinner);
        categoryDesserts = findViewById(R.id.categoryDesserts);

        recipesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        recipeList = new ArrayList<>();
        recipesAdapter = new RecipeBind(this, recipeList);
        recipesRecyclerView.setAdapter(recipesAdapter);

        db = FirebaseFirestore.getInstance();

        loadRecipesFromFirestore();

        // Initialize and set up filter spinner
        filterSpinner = findViewById(R.id.filter_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.filter_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterSpinner.setAdapter(adapter);

        // Listen for filter option selections
        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedFilter = parent.getItemAtPosition(position).toString();
                applyFilter(selectedFilter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Search functionality by ingredients
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                filterRecipesByIngredient(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        toggle.getDrawerArrowDrawable().setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);

        setupCategoryButtonListeners();

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_profile) {
                startActivity(new Intent(MainActivity.this, Profile.class));
            } else if (id == R.id.nav_submit_recipe) {
                startActivity(new Intent(MainActivity.this, SubmitRecipe.class));
            } else if (id == R.id.nav_shopping_list) {
                startActivity(new Intent(MainActivity.this, ShoppingList.class));
            } else if (id == R.id.nav_edit_recipe) {
                startActivity(new Intent(MainActivity.this, EditRecipeList.class));
            }
            drawerLayout.closeDrawers();
            return true;
        });
    }

    private void setupCategoryButtonListeners() {
        categoryBreakfast.setOnClickListener(v -> filterRecipesByCategory("Breakfast"));
        categoryLunch.setOnClickListener(v -> filterRecipesByCategory("Lunch"));
        categoryDinner.setOnClickListener(v -> filterRecipesByCategory("Dinner"));
        categoryDesserts.setOnClickListener(v -> filterRecipesByCategory("Desserts"));
    }

    private void loadRecipesFromFirestore() {
        db.collection("recipes")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            recipeList.clear();
                            for (DocumentSnapshot document : querySnapshot) {
                                Recipes recipe = document.toObject(Recipes.class);
                                recipe.setRecipeId(document.getId());
                                recipeList.add(recipe);
                            }
                            recipesAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    private void filterRecipesByIngredient(String ingredient) {
        List<Recipes> filteredList = new ArrayList<>();
        for (Recipes recipe : recipeList) {
            if (recipe.getIngredients().toString().toLowerCase().contains(ingredient.toLowerCase())) {
                filteredList.add(recipe);
            }
        }
        recipesAdapter.updateRecipeList(filteredList);
    }

    private void filterRecipesByCategory(String category) {
        List<Recipes> filteredList = new ArrayList<>();
        String selectedCategory = category.toLowerCase();
        for (Recipes recipe : recipeList) {
            if (recipe.getCategories() != null) {
                for (String recipeCategory : recipe.getCategories()) {
                    if (recipeCategory.toLowerCase().equals(selectedCategory)) {
                        filteredList.add(recipe);
                        break;
                    }
                }
            }
        }
        recipesAdapter.updateRecipeList(filteredList);
    }

    private void applyFilter(String filterType) {
        switch (filterType) {
            case "Highest Protein":
                Collections.sort(recipeList, (r1, r2) -> Double.compare(r2.getProtein(), r1.getProtein()));
                break;
            case "Lowest Calories":
                Collections.sort(recipeList, (r1, r2) -> Double.compare(r1.getCalories(), r2.getCalories()));
                break;
            case "Shortest Cooking Time":
                Collections.sort(recipeList, (r1, r2) -> Integer.compare(r1.getCookingTime(), r2.getCookingTime()));
                break;
            case "Shortest Preparation Time":
                Collections.sort(recipeList, (r1, r2) -> Integer.compare(r1.getPreparationTime(), r2.getPreparationTime()));
                break;
            default:
                break;
        }
        recipesAdapter.notifyDataSetChanged();
    }
}
