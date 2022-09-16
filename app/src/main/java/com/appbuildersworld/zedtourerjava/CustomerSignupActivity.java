package com.appbuildersworld.zedtourerjava;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CustomerSignupActivity extends AppCompatActivity {
    Button bRegisterCustomer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_signup);

        bRegisterCustomer = findViewById(R.id.bRegisterCustomer);
        bRegisterCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(CustomerSignupActivity.this, VerificationActivity.class);
                i.putExtra("userTypeId", 2);
                startActivity(i);
            }
        });

    }
}