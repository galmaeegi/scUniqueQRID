package com.example.uniqueqridsystemsc;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Check extends AppCompatActivity {

    private List<CheckItem> checkItemList = new ArrayList<>();
    private Handler handler = new Handler();
    private DatabaseReference databaseReference;
    private String selectedMonth = "DefaultMonth";
    private CheckAdapter checkAdapter;
    private SearchView searchView;
    private Spinner monthSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in_out);

        // Initialize Firebase database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("SC_Time_Log");

        // Initialize RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the CheckAdapter for the RecyclerView
        checkAdapter = new CheckAdapter(new ArrayList<>(), this, new CheckAdapter.SwipeActionListener() {
            @Override
            public void onSwipeRight(int position) {
                // Handle the swipe action
            }
        });

        recyclerView.setAdapter(checkAdapter);

        // Initialize the Spinner
        monthSpinner = findViewById(R.id.monthSpinner);

        // Populate Spinner with months
        populateMonthSpinner();

        // Fetch and populate data from Firebase
        populateRecyclerView(selectedMonth);

        // Initialize the SearchView
        searchView = findViewById(R.id.searchView);
        searchView.setQueryHint("Search a student or date...");
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
                checkAdapter.getFilter().filter(newText);
                return true;
            }
        });
    }

    // Fetch and populate data from Firebase for the selected month
    private void populateRecyclerView(String selectedMonth) {
        // Attach a ValueEventListener to fetch data for the selected month
        databaseReference.child(selectedMonth).child(selectedMonth).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Clear the adapter to avoid duplicates
                checkAdapter.clear();

                // Iterate through the students for the selected month
                for (DataSnapshot studentSnapshot : dataSnapshot.getChildren()) {
                    String uniqueID = studentSnapshot.getKey(); // Get the unique ID
                    String firstName = studentSnapshot.child("firstName").getValue(String.class);
                    String lastName = studentSnapshot.child("lastName").getValue(String.class);
                    String school = studentSnapshot.child("school").getValue(String.class);
                    String date = selectedMonth; // Set the date based on the selected month
                    String timedIn = studentSnapshot.child("checkInTime").getValue(String.class);
                    String timedOut = studentSnapshot.child("checkOutTime").getValue(String.class);

                    // Create a CheckItem object with all the data
                    CheckItem checkItem = new CheckItem(uniqueID, firstName, lastName, school, date, timedIn, timedOut);
                    checkAdapter.add(checkItem);
                }

                // Notify the adapter that the data has changed
                checkAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle database errors here
                Toast.makeText(Check.this, "Database error", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void populateMonthSpinner() {
        // Create an array to hold the months
        List<String> monthsList = new ArrayList<>();

        // Attach a ValueEventListener to fetch the list of months from Firebase
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot monthSnapshot : dataSnapshot.getChildren()) {
                    String month = monthSnapshot.getKey();
                    monthsList.add(month);
                }

                // Create an ArrayAdapter to populate the Spinner with months
                ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(Check.this, android.R.layout.simple_spinner_item, monthsList);
                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                monthSpinner.setAdapter(spinnerAdapter);

                // Set the initial selected month
                String selectedMonth = monthsList.get(0);
                monthSpinner.setSelection(spinnerAdapter.getPosition(selectedMonth));

                // Fetch and populate data for the initial selected month
                populateRecyclerView(selectedMonth);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle database errors here
                Toast.makeText(Check.this, "Database error", Toast.LENGTH_SHORT).show();
            }
        });

        // Set an item selected listener for the Spinner
        monthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Retrieve the selected month
                selectedMonth = monthsList.get(position);

                // Filter the data based on the selected month and update the RecyclerView
                filterDataByMonth(selectedMonth);

                // Call populateRecyclerView with the selected month
                populateRecyclerView(selectedMonth);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Handle case where no item is selected
            }
        });
    }



    private void filterDataByMonth(String selectedMonth) {
        // Filter the data based on the selected month
        List<CheckItem> filteredData = new ArrayList<>();
        for (CheckItem checkItem : checkItemList) {
            // Parse the month from the date (assuming the date format is "MMMM dd, yyyy")
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
            Date itemDate;
            try {
                itemDate = dateFormat.parse(checkItem.getDate());
            } catch (ParseException e) {
                e.printStackTrace();
                continue;
            }
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(itemDate);
            String itemMonth = new SimpleDateFormat("MMMM", Locale.getDefault()).format(calendar.getTime());

            if (itemMonth.equals(selectedMonth)) {
                filteredData.add(checkItem);
            }
        }

        // Update the RecyclerView with the filtered data
        checkAdapter.setFilteredData(filteredData);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove the runnable when the activity is destroyed to stop updating
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Check.this, Dashboard.class);
        startActivity(intent);
    }
}
