package com.example.uniqueqridsystemsc;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class Profile extends AppCompatActivity {

    private Handler handler = new Handler();
    private DatabaseReference databaseReference;
    private ProfileAdapter profileadapter;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize Firebase database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("SC_Permanent");

        // Initialize RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the profileadapter for the RecyclerView
        profileadapter = new ProfileAdapter(new ArrayList<>(), this, new ProfileAdapter.SwipeActionListener() {
            @Override
            public void onSwipeRight(int position) {
                // Handle the swipe action
            }
        });

        recyclerView.setAdapter(profileadapter);

        // Fetch and populate data from Firebase
        populateRecyclerView();

        // Initialize the SearchView
        searchView = findViewById(R.id.searchView);
        searchView.setQueryHint("Search a student...");
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Handle search query submission if needed (e.g., trigger search)
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Filter the profileadapter based on the search query
                profileadapter.getFilter().filter(newText);
                return true;
            }
        });
    }

    private void populateRecyclerView() {
        // Get the current date
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());

        // Attach a ValueEventListener to fetch data
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Clear the adapter to avoid duplicates
                profileadapter.clear();

                // Iterate through the dataSnapshot to retrieve data
                for (DataSnapshot studentSnapshot : dataSnapshot.getChildren()) {
                    String uniqueID = studentSnapshot.getKey(); // Get the unique ID
                    String firstName = studentSnapshot.child("firstName").getValue(String.class);
                    String lastName = studentSnapshot.child("lastName").getValue(String.class);
                    String middleName = studentSnapshot.child("middleName").getValue(String.class);
                    String school = studentSnapshot.child("school").getValue(String.class);
                    String personalEmail = studentSnapshot.child("personalEmail").getValue(String.class);
                    String phoneNumber = studentSnapshot.child("phoneNumber").getValue(String.class);
                    String registerDate = studentSnapshot.child("registerDate").getValue(String.class);


                    // Create a LogItem object with the unique ID and add it to the adapter
                    ProfileItem profileItem = new ProfileItem(uniqueID, firstName, lastName, school, middleName, personalEmail, phoneNumber, registerDate);
                    profileadapter.add(profileItem);
                }

                // Notify the adapter that the data has changed
                profileadapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle database errors here
                Toast.makeText(Profile.this, "Database error", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove the runnable when the activity is destroyed to stop updating
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Profile.this, Dashboard.class);
        startActivity(intent);
    }
}
