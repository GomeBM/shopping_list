package com.example.targil_three.Models;

import java.util.List;

//Class for creating and retrieving a user from firebase
public class Data {

    private String email;
    private String password;
    private List<itemData> shoppingList;
    private String phone;
    private String userName;

    public Data( String email, String password, List<itemData> shoppingList) {
        this.email = email;
        this.password = password;
        this.shoppingList = shoppingList;
    }

    public Data( String email, String password, List<itemData> shoppingList, String phone, String userName) {
        this.email = email;
        this.password = password;
        this.shoppingList = shoppingList;
        this.phone = phone;
        this.userName = userName;
    }

    public String getPhone() {
        return phone;
    }

    public String getUserName() {
        return userName;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Data(){}
    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public List<itemData> getShoppingList() {
        return shoppingList;
    }

    public void setShoppingList(List<itemData> shoppingList) {
        this.shoppingList = shoppingList;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
