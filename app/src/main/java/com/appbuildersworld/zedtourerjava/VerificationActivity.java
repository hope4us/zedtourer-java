package com.appbuildersworld.zedtourerjava;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class VerificationActivity extends AppCompatActivity {

    private AppCompatButton bVerify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        Intent intent = getIntent();
        int userTypeId = intent.getIntExtra("userTypeId", 0);

        bVerify = findViewById(R.id.bVerify);
        bVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(userTypeId == 1){
                    Intent i = new Intent(VerificationActivity.this, AccountVerifiedActivity.class);
                    i.putExtra("userTypeId", 1);
                    startActivity(i);
                }

                if(userTypeId == 2){
                    Intent i = new Intent(VerificationActivity.this, AccountVerifiedActivity.class);
                    i.putExtra("userTypeId", 2);
                    startActivity(i);
                }
            }
        });


    }
}