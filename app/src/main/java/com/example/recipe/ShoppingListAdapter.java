package com.example.recipe;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ShoppingListAdapter extends RecyclerView.Adapter<ShoppingListAdapter.ViewHolder> {

    private List<ShoppingItem> shoppingItemList;
    private Context context;

    public ShoppingListAdapter(Context context, List<ShoppingItem> shoppingItemList) {
        this.context = context;
        this.shoppingItemList = shoppingItemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.shopping_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ShoppingItem item = shoppingItemList.get(position);

        // Set the item name and category
        holder.itemNameTextView.setText(item.getName());
        holder.checkBox.setChecked(item.isChecked());

        // Set up a listener for the checkbox to update the checked status of the item
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            item.setChecked(isChecked);
            updateItemInFirestore(item);
        });

        // Set up a listener for the delete button
        holder.deleteButton.setOnClickListener(v -> {
            removeItem(position);
        });
    }

    // Method to remove item from the list
    public void removeItem(int position) {
        ShoppingItem itemToRemove = shoppingItemList.get(position);
        shoppingItemList.remove(position);
        notifyItemRemoved(position);
        // Call a method to remove the item from Firestore
        removeItemFromFirestore(itemToRemove);
    }

    private void removeItemFromFirestore(ShoppingItem item) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("shopping_list")
                .document(item.getId()) // Use the item's ID to delete the correct document
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Item deleted", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e("FirestoreError", "Error deleting item: " + e.getMessage());
                    Toast.makeText(context, "Error deleting item", Toast.LENGTH_SHORT).show();
                });
    }

    private void updateItemInFirestore(ShoppingItem item) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("shopping_list")
                .document(item.getId())
                .update("isChecked", item.isChecked())
                .addOnSuccessListener(aVoid -> {
                    Log.d("FirestoreUpdate", "Item updated successfully");
                })
                .addOnFailureListener(e -> {
                    Log.e("FirestoreError", "Error updating item: " + e.getMessage());
                });
    }

    public void updateShoppingList(List<ShoppingItem> items) {
        shoppingItemList.clear();
        shoppingItemList.addAll(items);
        notifyDataSetChanged();
        Log.d("ShoppingListAdapter", "Adapter updated with items: " + items.size());
    }


    @Override
    public int getItemCount() {
        return shoppingItemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemNameTextView;
        CheckBox checkBox;
        ImageView deleteButton; // Assuming you have an ImageView for delete button

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemNameTextView = itemView.findViewById(R.id.item_name_text_view);
            checkBox = itemView.findViewById(R.id.item_checkbox);
            deleteButton = itemView.findViewById(R.id.delete_button); // Initialize your delete button
        }
    }
}


