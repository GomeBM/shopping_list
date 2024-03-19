package com.example.targil_three.Models;

//Class used for each entry in the recyclerView shopping list
public class itemData {
    private String itemName;
    private String itemAmount;
    private boolean checked;

    public itemData(String itemName, String itemAmount) {
        this.itemName = itemName;
        this.itemAmount = itemAmount;
        this.checked = false;
    }

    public itemData() {
        // Default constructor required for calls to DataSnapshot.getValue(itemData.class)
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getItemName() {
        return itemName;
    }

    public String getItemAmount() {
        return itemAmount;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public void setItemAmount(String itemAmount) {
        this.itemAmount = itemAmount;
    }
}
