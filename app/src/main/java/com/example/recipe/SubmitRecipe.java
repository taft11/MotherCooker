package com.example.recipe;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SubmitRecipe extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText titleEditText, descriptionEditText,categoriesEditText, ingredientsEditText, stepsEditText;
    private EditText cookingTimeEditText, preparationTimeEditText;
    private ImageView recipeImageView;
    private Button uploadImageButton, submitRecipeButton;
    private Uri imageUri;
    private FirebaseFirestore db;
    private StorageReference storageRef;
    private EditText caloriesEditText, proteinEditText, fatEditText, carbsEditText, servingsEditText, sodiumEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.submit_recipe);

        // Initialize views
        titleEditText = findViewById(R.id.title_edit_text);
        descriptionEditText = findViewById(R.id.description_edit_text);
        cookingTimeEditText = findViewById(R.id.cooking_time_edit_text);
        preparationTimeEditText = findViewById(R.id.preparation_time_edit_text);
        ingredientsEditText = findViewById(R.id.ingredients_edit_text);
        stepsEditText = findViewById(R.id.steps_edit_text); // Updated to steps
        recipeImageView = findViewById(R.id.recipe_image_view);
        uploadImageButton = findViewById(R.id.upload_image_button);
        submitRecipeButton = findViewById(R.id.submit_recipe_button);
        caloriesEditText = findViewById(R.id.calories_edit_text);
        proteinEditText = findViewById(R.id.protein_edit_text);
        fatEditText = findViewById(R.id.fat_edit_text);
        carbsEditText = findViewById(R.id.carbs_edit_text);
        servingsEditText = findViewById(R.id.servings_edit_text);
        sodiumEditText = findViewById(R.id.sodium_edit_text);
        categoriesEditText = findViewById(R.id.categories_edit_text);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference("recipe_images");

        // Enable the back button in the ActionBar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Upload image button click listener
        uploadImageButton.setOnClickListener(v -> openFileChooser());

        // Submit recipe button click listener
        submitRecipeButton.setOnClickListener(v -> submitRecipe());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // Close the activity and return to the previous one
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            recipeImageView.setImageURI(imageUri);
        }
    }

    private void submitRecipe() {
        String title = titleEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();
        String ingredients = ingredientsEditText.getText().toString().trim();
        String steps = stepsEditText.getText().toString().trim();
        String categoriesInput = categoriesEditText.getText().toString().trim();
        String cookingTime = cookingTimeEditText.getText().toString().trim();
        String preparationTime = preparationTimeEditText.getText().toString().trim();



        if (title.isEmpty() || description.isEmpty() || ingredients.isEmpty() || steps.isEmpty() ||
                categoriesInput.isEmpty() || cookingTime.isEmpty() || preparationTime.isEmpty() || imageUri == null) {
            Toast.makeText(this, "Please fill all fields and select an image", Toast.LENGTH_SHORT).show();
            return;
        }
        List<String> categoriesList = new ArrayList<>();
        Collections.addAll(categoriesList, categoriesInput.split("\\s*,\\s*"));

        int cookingTimeValue = Integer.parseInt(cookingTime);
        int preparationTimeValue = Integer.parseInt(preparationTime);

        String calories = caloriesEditText.getText().toString().trim();
        String protein = proteinEditText.getText().toString().trim();
        String fat = fatEditText.getText().toString().trim();
        String carbs = carbsEditText.getText().toString().trim();
        String servings = servingsEditText.getText().toString().trim();
        String sodium = sodiumEditText.getText().toString().trim();


        if (calories.isEmpty() || protein.isEmpty() || fat.isEmpty() || carbs.isEmpty() || servings.isEmpty() || sodium.isEmpty()) {
            Toast.makeText(this, "Please fill in all nutritional values", Toast.LENGTH_SHORT).show();
            return;
        }

        double caloriesValue = Double.parseDouble(calories);
        double proteinValue = Double.parseDouble(protein);
        double fatValue = Double.parseDouble(fat);
        double carbsValue = Double.parseDouble(carbs);
        double sodiumValue = Double.parseDouble(sodium);
        String servingsValue = String.join(servings);

        List<String> ingredientsList = new ArrayList<>();
        Collections.addAll(ingredientsList, ingredients.split("\\s*,\\s*"));

        // Split the steps input into a list, assuming each step is on a new line
        List<String> stepsList = new ArrayList<>();
        Collections.addAll(stepsList, steps.split("\\n")); // Split by new line

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Upload image to Firebase Storage
        StorageReference fileReference = storageRef.child(System.currentTimeMillis() + ".jpg");
        fileReference.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
            fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                // Create a recipe object with all required parameters
                Recipes recipe = new Recipes(
                        title,
                        description,
                        ingredientsList, // Ingredients (empty list or filled as needed)
                        stepsList, // Use the stepsList (List<String>)
                        categoriesList, // Categories (empty list or filled as needed)
                        cookingTimeValue, //Cooking Time
                        preparationTimeValue, // Preparation Time
                        servingsValue, // Servings
                        caloriesValue, // Calories
                        proteinValue, // Protein
                        fatValue, // Fat
                        carbsValue, // Carbohydrates
                        sodiumValue  // Sodium
                );

                recipe.setImageUrl(uri.toString());
                recipe.setUserId(userId);
                // Save recipe to Firestore
                db.collection("recipes").add(recipe).addOnSuccessListener(documentReference -> {
                    Toast.makeText(SubmitRecipe.this, "Recipe submitted", Toast.LENGTH_SHORT).show();
                    finish(); // Close the activity
                }).addOnFailureListener(e -> Toast.makeText(SubmitRecipe.this, "Failed to submit recipe", Toast.LENGTH_SHORT).show());
            });
        }).addOnFailureListener(e -> Toast.makeText(this, "Image upload failed", Toast.LENGTH_SHORT).show());
    }
}
