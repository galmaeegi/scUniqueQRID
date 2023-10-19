package com.example.uniqueqridsystemsc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class ScannerCamera extends AppCompatActivity {
    private IntentIntegrator qrScan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner_camera);

        // Initialize the QR code scanner
        qrScan = new IntentIntegrator(this);
        qrScan.setOrientationLocked(false); // Allow both portrait and landscape scanning
        qrScan.setBeepEnabled(true); // Enable a beep sound when a QR code is detected
        qrScan.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                // QR code scanning was canceled
                finish();
            } else {
                String scannedQRCode = result.getContents();
                checkAndOpenRegistration(scannedQRCode);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void checkAndOpenRegistration(String scannedQRCode) {
        // Assuming you have a reference to your Firebase database here
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        // Check if the scanned QR code matches any registered student's unique ID
        databaseReference.child("SC_Permanent").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String uniqueId = snapshot.getKey();

                    if (scannedQRCode.equals(uniqueId)) {
                        // The scanned QR code matches a registered student
                        // You can open the registration details activity here

                        Intent intent = new Intent(ScannerCamera.this, Log.class);
                        intent.putExtra("uniqueId", uniqueId);
                        startActivity(intent);

                        return; // Exit the loop since a match was found
                    }
                }

                // If the loop completes without finding a match
                Toast.makeText(ScannerCamera.this, "Student not found", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database errors here
                Toast.makeText(ScannerCamera.this, "Database error", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}
