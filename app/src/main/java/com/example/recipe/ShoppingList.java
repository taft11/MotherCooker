package com.example.recipe;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ShoppingList extends AppCompatActivity {

    private RecyclerView shoppingRecyclerView;
    private ShoppingListAdapter adapter;
    private List<ShoppingItem> shoppingItems;
    private Spinner categorySpinner;
    private String selectedCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shopping_list);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        shoppingRecyclerView = findViewById(R.id.shopping_list_recycler_view);
        categorySpinner = findViewById(R.id.category_spinner);

        // Initialize the shopping list
        shoppingItems = new ArrayList<>();

        // Set up the RecyclerView
        adapter = new ShoppingListAdapter(this, shoppingItems);
        shoppingRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        shoppingRecyclerView.setAdapter(adapter);

        // Load shopping list items from Firestore
        loadShoppingList();

        setupCategorySpinner();
    }

    private void setupCategorySpinner() {
        // Create an array of categories
        String[] categories = {"All", "Dairy", "Produce", "Meat", "Other"};

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        // Set a listener for the spinner
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected category
                selectedCategory = categories[position];
                filterShoppingListByCategory(selectedCategory);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    private void loadShoppingList() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("shopping_list")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            String id = document.getId(); // Get the document ID
                            String name = document.getString("name");
                            boolean isChecked = document.getBoolean("isChecked") != null && document.getBoolean("isChecked");

                            // Create a ShoppingItem
                            ShoppingItem item = new ShoppingItem(id, name, "Other", isChecked);
                            item.categorizeItem(); // Categorize the item based on its name

                            // Log the categorized item
                            Log.d("ShoppingList", "Loaded item: " + item.getName() + " categorized as " + item.getItemCategory());

                            // Add the categorized item to the list
                            shoppingItems.add(item);
                        }
                        adapter.notifyDataSetChanged(); // Notify the adapter that the data has changed
                    } else {
                        Toast.makeText(this, "Error loading shopping list", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void filterShoppingListByCategory(String category) {
        List<ShoppingItem> filteredItems = new ArrayList<>();

        Log.d("ShoppingList", "Filtering by category: " + category);

        // If the selected category is "All", show all items
        if ("All".equalsIgnoreCase(category)) {
            filteredItems.addAll(shoppingItems);
        } else {
            for (ShoppingItem item : shoppingItems) {
                // Log the item details for debugging
                String itemCategory = item.getItemCategory();
                Log.d("ShoppingList", "Checking item: " + item.getName() + " | Category: " + itemCategory);

                // Check for null and compare categories
                if (itemCategory != null && itemCategory.equalsIgnoreCase(category)) {
                    filteredItems.add(item);
                }
            }
        }

        Log.d("ShoppingList", "Filtered items count: " + filteredItems.size());

        // Update the adapter with the filtered list
        adapter.updateShoppingList(filteredItems);
    }



    // Override onOptionsItemSelected to handle back button click
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // Close the current activity and go back to the previous one
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}


