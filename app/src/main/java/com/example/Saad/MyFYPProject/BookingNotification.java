package com.example.Saad.MyFYPProject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class BookingNotification extends AppCompatActivity {

    TextView textView;
    String OrderId="";
    Button backButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_notification);
        textView = findViewById(R.id.bookingDescription);
        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowMainActivity();
            }
        });
        OrderId = getIntent().getStringExtra("OrderId");
        textView.setText(String.format("Thank you for buy products your bill id is %s", OrderId));

    }

    private void ShowMainActivity() {
        Intent intent = new Intent(this,MainActivity.class);
        finish();
        startActivity(intent);

    }
}
