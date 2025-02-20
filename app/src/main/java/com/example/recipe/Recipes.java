package com.example.recipe;

import java.util.List;
import android.os.Parcel;
import android.os.Parcelable;

public class Recipes implements Parcelable {
    private String recipeId;
    private String userId;
    private String title;
    private String description;
    private List<String> ingredients;
    private List<String> steps;
    private List<String> categories;
    private int cookingTime;
    private int preparationTime;
    private String servingSize;
    private double calories;
    private double protein;
    private double fat;
    private double carbohydrates;
    private double sodium;
    private String imageUrl;
    private double averageRating;
    private int numberOfRatings;
    private long numberOfLikes;
    private List<String> likedByUsers;

    // Required empty constructor for Firestore
    public Recipes() {}

    public Recipes(String title, String description, List<String> ingredients, List<String> steps,
                   List<String> categories,int cookingTime, int preparationTime,
                   String servingSize, double calories, double protein, double fat,
                   double carbohydrates, double sodium) {
        this.title = title;
        this.description = description;
        this.ingredients = ingredients;
        this.steps = steps;
        this.categories = categories;
        this.cookingTime = cookingTime;
        this.preparationTime = preparationTime;
        this.servingSize = servingSize;
        this.calories = calories;
        this.protein = protein;
        this.fat = fat;
        this.carbohydrates = carbohydrates;
        this.sodium = sodium;
        this.numberOfLikes = 0;
        this.likedByUsers = null;
    }
    protected Recipes(Parcel in) {
        recipeId = in.readString();
        userId = in.readString();
        title = in.readString();
        description = in.readString();
        ingredients = in.createStringArrayList();
        steps = in.createStringArrayList();
        categories = in.createStringArrayList();
        cookingTime = in.readInt();
        preparationTime = in.readInt();
        servingSize = in.readString();
        calories = in.readDouble();
        protein = in.readDouble();
        fat = in.readDouble();
        carbohydrates = in.readDouble();
        sodium = in.readDouble();
        averageRating = in.readDouble();
        numberOfRatings = in.readInt();
        numberOfLikes = in.readLong();
        likedByUsers = in.createStringArrayList();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(recipeId);
        dest.writeString(userId);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeStringList(ingredients);
        dest.writeStringList(steps);
        dest.writeStringList(categories);
        dest.writeInt(cookingTime);
        dest.writeInt(preparationTime);
        dest.writeString(servingSize);
        dest.writeDouble(calories);
        dest.writeDouble(protein);
        dest.writeDouble(fat);
        dest.writeDouble(carbohydrates);
        dest.writeDouble(sodium);
        dest.writeDouble(averageRating);
        dest.writeInt(numberOfRatings);
        dest.writeLong(numberOfLikes);
        dest.writeStringList(likedByUsers);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Recipes> CREATOR = new Creator<Recipes>() {
        @Override
        public Recipes createFromParcel(Parcel in) {
            return new Recipes(in);
        }

        @Override
        public Recipes[] newArray(int size) {
            return new Recipes[size];
        }
    };

    // Getters and setters for all fields
    public String getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(String recipeId) {
        this.recipeId = recipeId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    public List<String> getSteps() {
        return steps;
    }

    public void setSteps(List<String> steps) {
        this.steps = steps;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public int getCookingTime() {
        return cookingTime;
    }

    public void setcookingTime() {
        this.cookingTime = cookingTime;
    }

    public int getPreparationTime() {
        return preparationTime;
    }

    public void setPreparationTime(int preparationTime) {
        this.preparationTime = preparationTime;
    }

    public String getServingSize() {
        return servingSize;
    }

    public void setServingSize(String servingSize) {
        this.servingSize = servingSize;
    }

    public double getCalories() {
        return calories;
    }

    public void setCalories(double calories) {
        this.calories = calories;
    }

    public double getProtein() {
        return protein;
    }

    public void setProtein(double protein) {
        this.protein = protein;
    }

    public double getFat() {
        return fat;
    }

    public void setFat(double fat) {
        this.fat = fat;
    }

    public double getCarbohydrates() {
        return carbohydrates;
    }

    public void setCarbohydrates(double carbohydrates) {
        this.carbohydrates = carbohydrates;
    }

    public double getSodium() {
        return sodium;
    }

    public void setSodium(double sodium) {
        this.sodium = sodium;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    public int getNumberOfRatings() {
        return numberOfRatings;
    }

    public void setNumberOfRatings(int numberOfRatings) {
        this.numberOfRatings = numberOfRatings;
    }

    public long getNumberOfLikes() {
        return numberOfLikes;
    }

    public void setNumberOfLikes(long numberOfLikes) {
        this.numberOfLikes = numberOfLikes;
    }

    public List<String> getLikedByUsers() {
        return likedByUsers;
    }

    public void setLikedByUsers(List<String> likedByUsers) {
        this.likedByUsers = likedByUsers;
    }

    @Override
    public String toString() {
        return "Recipes{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", ingredients=" + ingredients +
                ", steps=" + steps +
                ", categories=" + categories +
                ", cookingTime" + cookingTime +
                ", preparationTime=" + preparationTime +
                ", servingSize='" + servingSize + '\'' +
                ", calories=" + calories +
                ", protein=" + protein +
                ", fat=" + fat +
                ", carbohydrates=" + carbohydrates +
                ", sodium=" + sodium +
                ", averageRating=" + averageRating +
                ", numberOfRatings=" + numberOfRatings +
                ", numberOfLikes=" + numberOfLikes +
                ", likedByUsers=" + likedByUsers +
                '}';
    }
}
