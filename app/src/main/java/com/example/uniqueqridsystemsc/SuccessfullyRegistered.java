package com.example.uniqueqridsystemsc;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class SuccessfullyRegistered extends AppCompatActivity {


    ImageView qrCodeShower;
    Button goBackButton, shareQrBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_successfully_registered);

        Registration registration = new Registration();

        // Retrieve the qrCodeBitmap from the Intent
        Bitmap qrCodeBitmap = getIntent().getParcelableExtra("qrCodeBitmap");

        // Find the ImageView in the layout
        qrCodeShower = findViewById(R.id.qrCodeShower);

        // Display the QR code in the ImageView
        if (qrCodeBitmap != null) {
            qrCodeShower.setImageBitmap(qrCodeBitmap);
        }
        goBackButton = findViewById(R.id.goBackBtn);
        goBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SuccessfullyRegistered.this, Dashboard.class);
                startActivity(intent);
            }
        });


    }

    @Override
    public void onBackPressed() {
        // Do nothing or show a message, or perform some other action
        // To completely disable the back button, don't call super.onBackPressed()
    }

}
