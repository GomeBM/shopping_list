package com.example.targil_three.Models;

import static com.example.targil_three.R.*;

import android.content.Context;
import android.content.res.ColorStateList;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

    private List<itemData> itemList;
    private FloatingActionButton removeItemButton;
    private Context context;

    //constructor for interacting with adapter inside of fragmentUser
    public CustomAdapter(List<itemData> itemList, FloatingActionButton removeItemButton,Context context) {
        this.itemList = itemList;
        this.removeItemButton = removeItemButton;
        this.context=context;
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder{

        EditText itemName;
        EditText itemAmount;
        CheckBox deleteCheckBox;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(id.itemTitle);
            itemAmount = itemView.findViewById(id.itemAmount);
            deleteCheckBox=itemView.findViewById(id.deleteCheckBox);
        }
    }

    @NonNull
    @Override
    public CustomAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(layout.item_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomAdapter.MyViewHolder holder, int position) {
        itemData item = itemList.get(position);
        holder.itemName.setText(item.getItemName());
        holder.itemAmount.setText(item.getItemAmount());

        //Set checked state of checkbox
        holder.deleteCheckBox.setChecked(item.isChecked());

        //Listen for changes in any of the shopping lists entries checkbox and update the removeItem buttons color accordingly
        holder.deleteCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                item.setChecked(isChecked);
                updateRemoveButtonColor();
            }
        });

        //In order for the items the user entered to not disappear after scrolling the shopping list
        holder.itemName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    int adapterPosition = holder.getAdapterPosition();
                    if (adapterPosition != RecyclerView.NO_POSITION && adapterPosition < itemList.size()) {
                        itemList.get(adapterPosition).setItemName(holder.itemName.getText().toString());
                    }
                }
            }
        });

        holder.itemAmount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    int adapterPosition = holder.getAdapterPosition();
                    if (adapterPosition != RecyclerView.NO_POSITION && adapterPosition < itemList.size()) {
                        itemList.get(adapterPosition).setItemAmount(holder.itemAmount.getText().toString());
                    }
                }
            }
        });
        Log.d("CustomAdapter", "Item bound at position: " + position);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public void addItem(itemData item) {
        itemList.add(item);
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        itemList.remove(position);
        notifyDataSetChanged();
    }

    public List<itemData> getItemList() {
        return itemList;
    }


    //Function used to set the color of the remove item button based on whether or not any item in the shopping list is checked
    public void updateRemoveButtonColor() {
        for (itemData item : itemList) {
            if (item.isChecked()) {
                //If any item is checked, change removeItem button color to red
                removeItemButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, color.red)));
                return;
            }
        }
        //If no item is checked, change removeItem button color to default purple
        removeItemButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, color.purple)));
    }
}
