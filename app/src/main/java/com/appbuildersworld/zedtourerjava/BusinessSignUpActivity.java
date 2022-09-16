package com.appbuildersworld.zedtourerjava;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class BusinessSignUpActivity extends AppCompatActivity {

    Button bRegisterBusiness;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_sign_up);

        bRegisterBusiness = findViewById(R.id.bRegisterBusiness);
        bRegisterBusiness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(BusinessSignUpActivity.this, VerificationActivity.class);
                i.putExtra("userTypeId", 1);
                startActivity(i);
            }
        });
    }
}