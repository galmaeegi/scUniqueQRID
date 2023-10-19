package com.example.uniqueqridsystemsc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
// Other necessary imports

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class Dashboard extends AppCompatActivity {

    private IntentIntegrator qrScan;
    private boolean isScannerOpen = false;
    CardView registerBtn;
    CardView scannerBtn;
    CardView sclogBtn;
    CardView studentProfileBtn;
    CardView checkInOutBtn;
    private TextView totalStudentsCheckedIn;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);



        qrScan = new IntentIntegrator(this);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference();

        // After initializing your Firebase database and reference, retrieve the count
        DatabaseReference logReference = databaseReference.child("SC_Log");
        logReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Calculate the total number of students checked in
                int totalStudents = 0;

                for (DataSnapshot monthSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot dateSnapshot : monthSnapshot.getChildren()) {
                        totalStudents += dateSnapshot.getChildrenCount();
                    }
                }

                // Set the total students checked in to the TextView
                totalStudentsCheckedIn.setText("Total Students Checked In: " + totalStudents);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle database errors here
                Toast.makeText(Dashboard.this, "Database error", Toast.LENGTH_SHORT).show();
            }
        });

        // Initialize the TextView
        totalStudentsCheckedIn = findViewById(R.id.totalStudentsCheckedIn);
        registerBtn = findViewById(R.id.reg);
        scannerBtn = findViewById(R.id.scan);
        sclogBtn = findViewById(R.id.log);
        studentProfileBtn = findViewById(R.id.profile);
        checkInOutBtn = findViewById(R.id.check);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Dashboard.this, Registration.class);
                startActivity(intent);
            }
        });

        scannerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                qrScan = new IntentIntegrator(Dashboard.this);
                qrScan.setOrientationLocked(true);
                qrScan.setBeepEnabled(true);
                qrScan.initiateScan();
            }
        });

        sclogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Dashboard.this, Log.class);
                startActivity(intent);
            }
        });

        studentProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Prompt for the passcode
                showPasscodeDialog();
            }
        });

        checkInOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Intent intent = new Intent(Dashboard.this, Check.class);
                // startActivity(intent);
            }
        });
    }



    private void showPasscodeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Passcode");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String passcode = input.getText().toString();
                // Validate the entered passcode (You can replace "YourPasscode" with your actual passcode)
                if ("rowi".equals(passcode)) {
                    // Correct passcode, allow access to student profile
                    Intent intent = new Intent(Dashboard.this, Profile.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(Dashboard.this, "Incorrect Passcode", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                // QR code scanning was canceled
            } else {
                String scannedQRCode = result.getContents();
                checkAndOpenRegistration(scannedQRCode);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void checkAndOpenRegistration(String scannedQRCode) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        // Check if the scanned QR code matches any registered student's unique ID
        databaseReference.child("SC_Permanent").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean studentFound = false;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String uniqueId = snapshot.getKey();

                    if (scannedQRCode.equals(uniqueId)) {
                        studentFound = true;

                        // The scanned QR code matches a registered student
                        // Create a fixed database reference "SC_Log" and store the data
                        DatabaseReference logReference = databaseReference.child("SC_Log");

                        // Get the current date and time
                        SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM", Locale.getDefault());
                        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());
                        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss a", Locale.getDefault());

                        // Get the current month, date, and time
                        String currentMonth = monthFormat.format(new Date());
                        String currentDate = dateFormat.format(new Date());
                        String currentTime = timeFormat.format(new Date());

                        // You can get the values of First Name, Last Name, and School from the snapshot
                        String firstName = snapshot.child("firstName").getValue(String.class);
                        String lastName = snapshot.child("lastName").getValue(String.class);
                        String school = snapshot.child("school").getValue(String.class);

                        // Create a new child node under "SC_Log" with the current month
                        DatabaseReference monthReference = logReference.child(currentMonth);

                        // Create a new child node under the current month with the current date
                        DatabaseReference dateReference = monthReference.child(currentDate);

                        // Store the data including First Name, Last Name, School, and check-in time
                        DatabaseReference studentReference = dateReference.child(uniqueId);
                        studentReference.child("checkInTime").setValue(currentTime);
                        studentReference.child("firstName").setValue(firstName);
                        studentReference.child("lastName").setValue(lastName);
                        studentReference.child("school").setValue(school);

                        // Display a success message and exit the loop
                        Toast.makeText(Dashboard.this, "Student Checked In!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Dashboard.this, Dashboard.class);
                        startActivity(intent);

                        break;
                    }
                }

                if (!studentFound) {
                    // If the loop completes without finding a match
                    Toast.makeText(Dashboard.this, "Student not found", Toast.LENGTH_SHORT).show();
                }

                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database errors here
                Toast.makeText(Dashboard.this, "Database error", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        isScannerOpen = false; // Reset the flag when the activity is resumed
    }

    @Override
    public void onBackPressed() {
        if (isScannerOpen) {
            // If the QR code scanner is open, close it and return to the dashboard
            isScannerOpen = false;
            qrScan.initiateScan(); // Start the scanner to close it immediately
        } else {
            // Show a message or confirm dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Do you really want to exit the application?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // If "Yes" is clicked, close the activity
                    finish();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // If "No" is clicked, dismiss the dialog
                    dialog.dismiss();
                }
            });
            builder.show();
        }
    }




}