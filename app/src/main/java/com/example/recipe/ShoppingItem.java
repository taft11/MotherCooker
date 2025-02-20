package com.example.recipe;

public class ShoppingItem {
    private String id;  // Unique ID for Firestore
    private String name;
    private String itemCategory;
    private boolean checked;

    // Constructor
    public ShoppingItem(String id, String name, String category, boolean checked) {
        this.id = id;  // Set the ID
        this.name = name;
        this.itemCategory = category;
        this.checked = checked;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getItemCategory() {
        return itemCategory;
    }

    public void setItemCategory(String itemCategory) {
        this.itemCategory = itemCategory;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    // New method to categorize based on the ingredient name
    public void categorizeItem() {
        String lowerCaseName = name.toLowerCase();
        if (lowerCaseName.contains("chicken") || lowerCaseName.contains("beef") || lowerCaseName.contains("pork")) {
            itemCategory = "Meat";
        } else if (lowerCaseName.contains("egg") || lowerCaseName.contains("salt") ||
                lowerCaseName.contains("pepper") || lowerCaseName.contains("calamansi") ||
                lowerCaseName.contains("lemon") || lowerCaseName.contains("garlic") ||
                lowerCaseName.contains("sugar") || lowerCaseName.contains("rice")) {
            itemCategory = "Produce";
        } else if (lowerCaseName.contains("milk")) {
            itemCategory = "Dairy";
        } else {
            itemCategory = "Other"; // Default category
        }
    }
}


