package com.example.recipe;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import java.util.Arrays;

public class EditRecipe extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText titleEditText, descriptionEditText, ingredientsEditText, stepsEditText, categoriesEditText;
    private EditText servingSizeEditText, caloriesEditText, proteinEditText, fatEditText, carbsEditText, sodiumEditText;
    private ImageView recipeImageView;
    private Uri imageUri;
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    private Recipes recipe;
    private FirebaseFirestore db;
    private StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_recipe);

        // Initialize fields
        titleEditText = findViewById(R.id.edit_recipe_title);
        descriptionEditText = findViewById(R.id.edit_recipe_description);
        ingredientsEditText = findViewById(R.id.edit_recipe_ingredients);
        stepsEditText = findViewById(R.id.edit_recipe_steps);
        categoriesEditText = findViewById(R.id.edit_recipe_categories);
        servingSizeEditText = findViewById(R.id.edit_recipe_serving_size);
        caloriesEditText = findViewById(R.id.edit_recipe_calories);
        proteinEditText = findViewById(R.id.edit_recipe_protein);
        fatEditText = findViewById(R.id.edit_recipe_fat);
        carbsEditText = findViewById(R.id.edit_recipe_carbohydrates);
        sodiumEditText = findViewById(R.id.edit_recipe_sodium);
        recipeImageView = findViewById(R.id.edit_recipe_image);

        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference("recipe_images");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        imageUri = result.getData().getData();
                        recipeImageView.setImageURI(imageUri);
                    }
                });


        // Get the recipe passed from the intent
        recipe = getIntent().getParcelableExtra("recipe");
        if (recipe != null) {
            populateFields();
        }else {
        Toast.makeText(this, "Error: No recipe data found", Toast.LENGTH_SHORT).show();
        finish();
    }


        findViewById(R.id.select_image_button).setOnClickListener(v -> openFileChooser());
        findViewById(R.id.save_button).setOnClickListener(v -> saveRecipeChanges());
    }

    private void populateFields() {
        titleEditText.setText(recipe.getTitle());
        descriptionEditText.setText(recipe.getDescription());
        ingredientsEditText.setText(String.join(", ", recipe.getIngredients()));
        stepsEditText.setText(String.join(", ", recipe.getSteps()));
        categoriesEditText.setText(String.join(", ", recipe.getCategories()));
        servingSizeEditText.setText(recipe.getServingSize());
        caloriesEditText.setText(String.valueOf(recipe.getCalories()));
        proteinEditText.setText(String.valueOf(recipe.getProtein()));
        fatEditText.setText(String.valueOf(recipe.getFat()));
        carbsEditText.setText(String.valueOf(recipe.getCarbohydrates()));
        sodiumEditText.setText(String.valueOf(recipe.getSodium()));

        // Load image if URL exists
        if (recipe.getImageUrl() != null && !recipe.getImageUrl().isEmpty()) {
            Glide.with(this)
                    .load(recipe.getImageUrl())
                    .into(recipeImageView);
        }
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent); // Use the launcher
    }

    private void saveRecipeChanges() {
        if (recipe.getUserId() == null || recipe.getUserId().isEmpty()) {
            recipe.setUserId(FirebaseAuth.getInstance().getCurrentUser().getUid());
        }
        recipe.setTitle(titleEditText.getText().toString());
        recipe.setDescription(descriptionEditText.getText().toString());
        recipe.setIngredients(Arrays.asList(ingredientsEditText.getText().toString().split(",\\s*")));
        recipe.setSteps(Arrays.asList(stepsEditText.getText().toString().split(",\\s*")));
        recipe.setCategories(Arrays.asList(categoriesEditText.getText().toString().split(",\\s*")));
        recipe.setServingSize(servingSizeEditText.getText().toString());
        recipe.setCalories(Double.parseDouble(caloriesEditText.getText().toString()));
        recipe.setProtein(Double.parseDouble(proteinEditText.getText().toString()));
        recipe.setFat(Double.parseDouble(fatEditText.getText().toString()));
        recipe.setCarbohydrates(Double.parseDouble(carbsEditText.getText().toString()));
        recipe.setSodium(Double.parseDouble(sodiumEditText.getText().toString()));

        if (imageUri != null) {
            uploadImage();
        } else {
            updateRecipeInFirestore();
        }
    }

    private void uploadImage() {
        StorageReference fileReference = storageRef.child(recipe.getRecipeId() + ".jpg");
        fileReference.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                    recipe.setImageUrl(uri.toString());
                    updateRecipeInFirestore();
                }))
                .addOnFailureListener(e -> Toast.makeText(this, "Image upload failed", Toast.LENGTH_SHORT).show());
    }

    private void updateRecipeInFirestore() {
        db.collection("recipes").document(recipe.getRecipeId())
                .set(recipe)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Recipe updated successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to update recipe", Toast.LENGTH_SHORT).show());
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
