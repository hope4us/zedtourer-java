package com.appbuildersworld.zedtourerjava;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class BillInvoiceActivity extends AppCompatActivity {

    private AppCompatButton bMakePayment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_invoice);

        bMakePayment = findViewById(R.id.bMakePayment);
        bMakePayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(BillInvoiceActivity.this, PaymentSuccessActivity.class);
                startActivity(i);
            }
        });
    }
}