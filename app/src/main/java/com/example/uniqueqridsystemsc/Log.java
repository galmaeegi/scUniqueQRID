package com.example.uniqueqridsystemsc;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.uniqueqridsystemsc.LogItem;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Log extends AppCompatActivity {

    TextView rt_date_time;
    private Handler handler = new Handler();
    private DatabaseReference databaseReference;
    private LogAdapter adapter;
    private SearchView searchView;
    private TextView itemCountTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        rt_date_time = findViewById(R.id.rt_Date_Time);

        // Initialize the itemCountTextView
        itemCountTextView = findViewById(R.id.itemCountTextView);

        // Create a runnable to update the date and time TextView
        Runnable updateTimeRunnable = new Runnable() {
            @Override
            public void run() {
                updateDateTime();
                handler.postDelayed(this, 1000); // Update every 1 second (1000 milliseconds)
            }
        };



        // Start updating the date and time
        handler.post(updateTimeRunnable);

        // Initialize Firebase database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("SC_Log").child("October");

        // Initialize RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the adapter for the RecyclerView
        adapter = new LogAdapter(new ArrayList<>(), this, new LogAdapter.SwipeActionListener() {
            @Override
            public void onSwipeRight(int position) {
                // Handle the swipe action
            }
        });

        recyclerView.setAdapter(adapter);

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
                // Filter the adapter based on the search query
                adapter.getFilter().filter(newText);
                return true;
            }
        });
    }

    private void updateDateTime() {
        // Get the current date and time
        Date currentTime = new Date();

        // Format the date (dd-MM-yyyy) and time (hh:mm:ss a) in 12-hour format with AM/PM
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss a", Locale.getDefault());

        String formattedDate = dateFormat.format(currentTime);
        String formattedTime = timeFormat.format(currentTime);

        // Update the TextView with the formatted date and time
        rt_date_time.setText("Date: " + formattedDate + "   "+ "Time: " + formattedTime);
    }

    private void populateRecyclerView() {
        // Get the current date
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());

        // Attach a ValueEventListener to fetch data
        databaseReference.child(currentDate).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                List<LogItem> tempList = new ArrayList<>();

                // Clear the adapter to avoid duplicates
                adapter.clear();

                // Iterate through the dataSnapshot to retrieve data
                for (DataSnapshot studentSnapshot : dataSnapshot.getChildren()) {
                    String uniqueID = studentSnapshot.getKey(); // Get the unique ID
                    String firstName = studentSnapshot.child("firstName").getValue(String.class);
                    String lastName = studentSnapshot.child("lastName").getValue(String.class);
                    String school = studentSnapshot.child("school").getValue(String.class);
                    String checkInTime = studentSnapshot.child("checkInTime").getValue(String.class);

                    // Create a LogItem object with the unique ID and add it to the adapter
                    LogItem logItem = new LogItem(uniqueID, firstName, lastName, school, checkInTime);
                    adapter.add(logItem);

//                    // Add items to the beginning of the temporary list
//                    tempList.add(0, logItem);
                }
//
//                // Clear the adapter to avoid duplicates
//                adapter.clear();

//                // Add all items from the temporary list to the adapter
//                adapter.addAll(tempList);

                // Notify the adapter that the data has changed
                adapter.notifyDataSetChanged();

                // Update the item count TextView
                updateItemCountText(adapter.getItemCount());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle database errors here
                Toast.makeText(Log.this, "Database error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to update the item count TextView
    private void updateItemCountText(int count) {
        itemCountTextView.setText("Total Students Checked In: " + count);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove the runnable when the activity is destroyed to stop updating
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Log.this, Dashboard.class);
        startActivity(intent);
    }
}
