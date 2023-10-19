package com.example.uniqueqridsystemsc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Registration extends AppCompatActivity {
    EditText ln_et_v, fn_et_v, mn_et_v, pe_et_v, phone_et_v;

    Spinner schoolspinner;

    String uniqueID, firstName, lastName, middleName, personalEmail, schoolSpinner, phoneNumber;
    Button regButton;
    DatabaseReference Upang, UDD, UL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        // Variables of EditText
        ln_et_v =findViewById(R.id.ln_et);
        fn_et_v =findViewById(R.id.fn_et);
        mn_et_v =findViewById(R.id.mn_et);
        pe_et_v =findViewById(R.id.pe_et);
        phone_et_v =findViewById(R.id.phone_et);

        // Variable of Spinner
        schoolspinner = findViewById(R.id.schoolspn);

        // Variable of Register Button
        regButton = findViewById(R.id.regBtn);

        // Database Instances
        Upang = FirebaseDatabase.getInstance().getReference().child("SC_Permanent");

        // Define the list of school choices
        List<String> schools = new ArrayList<>();
        schools.add("UPANG");
        schools.add("UL");
        schools.add("UDD");
        schools.add("DCNHS");
        schools.add("LNU");
        schools.add("MOTHERGOOSE");
        schools.add("HARVENT");
        schools.add("SJCS");
        schools.add("SMSBS");
        schools.add("PSU");
        schools.add("NORTHFIELD");
        schools.add("WONDERLAND");
        schools.add("KINGFISHER");
        schools.add("EDNAS");
        schools.add("DWAD");
        schools.add("YMCA");
        schools.add("MNHS");
        schools.add("PAMMA");
        schools.add("LA MAREA");
        schools.add("OAKRIDGE");
        schools.add("LA SALETTE");
        schools.add("ST. ROBERT");
        schools.add("STI");
        schools.add("AMA");
        schools.add("SAGS");



        // Add a hint item as the first item in the list
        schools.add(0, "Select a school");

        // Initialize the Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, schools);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        schoolspinner.setAdapter(adapter);

        // Set the selected item to the hint item
        schoolspinner.setSelection(0, false);

        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerStudent();
            }
        });
    }

    // Methods
    private void registerStudent() {
        // Get the text from the EditText fields
        firstName = fn_et_v.getText().toString().trim();
        lastName = ln_et_v.getText().toString().trim();
        middleName = mn_et_v.getText().toString().trim();
        personalEmail = pe_et_v.getText().toString().trim();
        schoolSpinner = schoolspinner.getSelectedItem().toString();
        phoneNumber = phone_et_v.getText().toString().trim();

        // Check if any of the fields are empty
        if (firstName.isEmpty() || lastName.isEmpty() || middleName.isEmpty() || personalEmail.isEmpty() || phoneNumber.isEmpty() || schoolSpinner.isEmpty()) {
            // Display an error message or toast to inform the user to fill in all required fields.
            Toast.makeText(getApplicationContext(), "Please fill in all required fields", Toast.LENGTH_SHORT).show();
        } else {
            // All fields are filled, proceed with registration

            // Check if the student is already registered
            checkIfStudentExists(firstName, lastName, personalEmail, schoolSpinner, phoneNumber);
        }
    }

    private void checkIfStudentExists(String firstName, String lastName, String personalEmail, String school, String phoneNumber) {
        Upang.orderByChild("personalEmail").equalTo(personalEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Student already exists
                    Toast.makeText(getApplicationContext(), "Student is already registered.", Toast.LENGTH_SHORT).show();
                } else {
                    // Student is not registered, proceed with registration
                    registerNewStudent(firstName, lastName, middleName, personalEmail, schoolSpinner, phoneNumber);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle any errors here
                Toast.makeText(getApplicationContext(), "Error checking registration status.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void registerNewStudent(String firstName, String lastName, String middleName, String personalEmail, String school, String phoneNumber) {
        // Check if the student is already registered in the "SC_Permanent" table
        Upang.orderByChild("personalEmail").equalTo(personalEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Student already exists
                    Toast.makeText(getApplicationContext(), "Student is already registered.", Toast.LENGTH_SHORT).show();
                } else {
                    // Student is not registered, proceed with registration
                    // Get the current date in the desired format (Month - Day - Year) with the appropriate locale
                    SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
                    Calendar calendar = Calendar.getInstance();
                    String registerDate = dateFormat.format(calendar.getTime());

                    POST registerStudent = new POST(firstName, lastName, middleName, personalEmail, school, registerDate, phoneNumber);

                    // Generate a unique key for each student
                    DatabaseReference studentRef = Upang.push();

                    // Set the student data under the generated unique key
                    studentRef.setValue(registerStudent);

                    // Get the unique ID
                    uniqueID = studentRef.getKey();

                    // Call registerStudentCheckIn with the uniqueID
                    registerStudentCheckIn(uniqueID);

                    // Generate a QR code based on the unique ID
                    Bitmap qrCodeBitmap = generateQRCode(uniqueID);

                    if (qrCodeBitmap != null) {
                        // Now you have the QR code image (qrCodeBitmap) that corresponds to the student's unique ID

                        // Display a message or perform any other actions as needed
                        Toast.makeText(Registration.this, "Student registered with Unique ID: " + uniqueID, Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(Registration.this, SuccessfullyRegistered.class);
                        intent.putExtra("qrCodeBitmap", qrCodeBitmap);
                        startActivity(intent);

                        // Send the QR code via email
                        sendQRCodeByEmail(personalEmail, qrCodeBitmap, firstName);
                    } else {
                        // Handle the case where QR code generation failed
                        Toast.makeText(Registration.this, "Failed to generate QR code", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle any errors here
                Toast.makeText(getApplicationContext(), "Error checking registration status.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void registerStudentCheckIn (String uniqueID) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

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

        // Create a new child node under "SC_Log" with the current month
        DatabaseReference monthReference = logReference.child(currentMonth);

        // Create a new child node under the current month with the current date
        DatabaseReference dateReference = monthReference.child(currentDate);

        // Store the data including First Name, Last Name, School, and check-in time
        DatabaseReference studentReference = dateReference.child(uniqueID);
        studentReference.child("checkInTime").setValue(currentTime);
        studentReference.child("firstName").setValue(firstName);
        studentReference.child("lastName").setValue(lastName);
        studentReference.child("school").setValue(schoolSpinner);

        // Display a success message and exit the loop
        Toast.makeText(Registration.this, "Student Checked in!", Toast.LENGTH_SHORT).show();
    }
    private File saveBitmapToFile(Bitmap bitmap) {
        try {
            // Get the directory where you want to save the file
            File directory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

            // Create a unique filename
            String fileName = "qr_code.png";

            // Create a new file in the specified directory
            File file = new File(directory, fileName);

            // Create an output stream to write the bitmap to the file
            FileOutputStream out = new FileOutputStream(file);

            // Compress the bitmap and write it to the output stream
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);

            // Close the output stream
            out.close();

            return file;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    public void sendQRCodeByEmail(String emailAddress, Bitmap qrCodeBitmap, String firstName) {
        // Create a content URI for the file
        File qrCodeFile = saveBitmapToFile(qrCodeBitmap);
        Uri contentUri = FileProvider.getUriForFile(
                this,
                "com.example.uniqueqridsystemsc.provider", // Replace with your FileProvider authority
                qrCodeFile
        );

        // Create the email body with the recipient's first name
        String emailBody = "Hi " + firstName + ", here's your QR Code. Please save this image as this will be your permanent QR code for student center to check in. Thank you!";

        // Create an Intent to send an email
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("image/*"); // Set the MIME type to image
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{emailAddress});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "QR Code"); // Email subject
        emailIntent.putExtra(Intent.EXTRA_TEXT, emailBody); // Email body

        // Grant read permission to the receiving app
        emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        // Attach the QR code file using the content URI
        emailIntent.putExtra(Intent.EXTRA_STREAM, contentUri);

        // Start the email intent
        startActivity(Intent.createChooser(emailIntent, "Send email..."));
    }



    private Bitmap generateQRCode(String uniqueId) {
        try {
            // Set QR code content to be the unique ID
            String qrCodeContent = uniqueId;

            // Encode the content into a QR code
            BitMatrix bitMatrix = new MultiFormatWriter().encode(
                    qrCodeContent,
                    BarcodeFormat.QR_CODE,
                    300, // QR code width
                    300  // QR code height
            );

            // Convert BitMatrix to Bitmap
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap qrCodeBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    qrCodeBitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);

                }
            }

            return qrCodeBitmap;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onBackPressed() {
            // Show a message or confirm dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Do you really want to go back? Your input data will be erased.");
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