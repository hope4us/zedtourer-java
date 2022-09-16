package com.appbuildersworld.zedtourerjava;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {

    private TextView bBusinessSignUp;
    private TextView bCustomerSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        bBusinessSignUp = findViewById(R.id.bBusinessSignUp);
        bBusinessSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this, BusinessSignUpActivity.class);
                startActivity(i);
            }
        });

        bCustomerSignUp = findViewById(R.id.bCustomerSignUp);
        bCustomerSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this, CustomerSignupActivity.class);
                startActivity(i);
            }
        });

    }
}