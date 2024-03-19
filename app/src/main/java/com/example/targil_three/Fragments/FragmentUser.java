package com.example.targil_three.Fragments;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.targil_three.Activities.MainActivity;
import com.example.targil_three.Models.CustomAdapter;
import com.example.targil_three.Models.Data;
import com.example.targil_three.Models.itemData;

import com.example.targil_three.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentUser#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentUser extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private List<itemData> itemList;
    private CustomAdapter adapter;
    private RecyclerView recyclerView;

    private FirebaseAuth mAuth;


    public FragmentUser() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentUser.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentUser newInstance(String param1, String param2) {
        FragmentUser fragment = new FragmentUser();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);

        //Get the references for the needed views for later use.
        FloatingActionButton addItemButton = view.findViewById(R.id.addItemButton);
        FloatingActionButton removeItemButton = view.findViewById(R.id.removeItemButton);
        FloatingActionButton saveList = view.findViewById(R.id.saveListButton);
        TextView userTextView = view.findViewById(R.id.userTextView);
        ImageButton infoButton = view.findViewById(R.id.infoButton);
        TextView listHeaderTextView = view.findViewById(R.id.listHeader);
        MainActivity mainActivity = (MainActivity) getActivity();

        //Will be used in order to retrieve the correct users list and data from the realtime database.
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        String userUID = firebaseUser.getUid();

        String userPhone = getArguments().getString("userPhone");
        String userEmail = getArguments().getString("userEmail");
        String userPassword = getArguments().getString("userPassword");

        //Attach the customAdapter to the recyclerView which will be used to display the user's shopping list items.
        recyclerView = view.findViewById(R.id.recyclerView);
        itemList = new ArrayList<>();
        adapter = new CustomAdapter(itemList, removeItemButton, getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        readDataFromDataBase(userUID);

        //Retrieve username from the database and update userTextView
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userUID);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Data userData = dataSnapshot.getValue(Data.class);
                    if (userData != null && userData.getUserName() != null) {
                        String userName = userData.getUserName();
                        String greeting = getString(R.string.greeting_message);
                        greeting = greeting + userName;
                        String listHeader = userName+"'s shopping list:";
                        userTextView.setText(greeting);
                        listHeaderTextView.setText(listHeader);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to retrieve username from database", Toast.LENGTH_SHORT).show();
            }
        });

        //A button for displaying information related to the user page.
        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInformationDetails();
            }
        });



        //Adds a new blank item to the recyclerView list.
        addItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemList.add(new itemData("", ""));
                //Notify adapter about a newly added item to the recyclerView
                adapter.notifyItemInserted(itemList.size() - 1);
                //Scroll RecyclerView to the newly added item
                recyclerView.scrollToPosition(itemList.size() - 1);
            }
        });

        //Removes all checked items from the list and updates the buttons color.
        removeItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Remove checked items from the itemList
                List<itemData> checkedItems = getCheckedItems();
                for (itemData item : checkedItems) {
                    itemList.remove(item);
                }
                adapter.notifyDataSetChanged();
                //Clear the checked state of all items and notify the adapter to change the color of the remove items button
                clearCheckedItems();
                adapter.updateRemoveButtonColor();
            }
        });

        //Saves the users list to his own data inside of the realtime database.
        saveList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.writeDataToDataBase(userEmail, userPassword, userPhone, itemList);
                Toast.makeText(getContext(), "Shopping list saved successfully", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }

    //Function for getting all the checked items used for deletion from the recyclerView list.
    private List<itemData> getCheckedItems() {
        List<itemData> checkedItems = new ArrayList<>();
        for (itemData item : itemList) {
            if (item.isChecked()) {
                checkedItems.add(item);
            }
        }
        return checkedItems;
    }

    //Function for clearing all the check boxes.
    private void clearCheckedItems() {
        for (itemData item : itemList) {
            item.setChecked(false);
        }
    }

    //Function for updating the UI after the users list is retrieved from the real time database.
    private void updateUI(List<itemData> shoppingList) {
        if (shoppingList != null && !shoppingList.isEmpty()) {
            itemList.clear();
            itemList.addAll(shoppingList);
            adapter.notifyDataSetChanged();
        } else {
            Log.d("updateUI", "Shopping list is empty or null");
        }
    }


    //Function for reading the correct users data from the realtime data base, based on his unique ID he got while registering.
    private void readDataFromDataBase(String userUID) {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("users").child(userUID);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Data userData = dataSnapshot.getValue(Data.class);
                    if (userData != null && userData.getShoppingList() != null) {
                        List<itemData> shoppingList = userData.getShoppingList();
                        updateUI(shoppingList);
                    } else {
                        Log.d("readDataFromDataBase", "No shopping list data found.");
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Toast.makeText(getContext(), "Failed to retrieve shopping list data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Function for displaying an alertDialog to the user explaining how to use the various buttons.
    public void showInformationDetails() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setCancelable(false);
        View alertDialogView = LayoutInflater.from(getContext()).inflate(R.layout.info_layout, null);
        builder.setView(alertDialogView);
        AlertDialog alert = builder.create();
        alert.show();
        Button confirmButton = alertDialogView.findViewById(R.id.closeButton);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
            }
        });
    }
}