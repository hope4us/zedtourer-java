package com.appbuildersworld.zedtourerjava;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.appbuildersworld.zedtourerjava.activities.customer.CustomerOrderDetailsActivity;

public class BillActivity extends AppCompatActivity {

    private CardView cvBill;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill);

        cvBill = findViewById(R.id.cvBill);
        cvBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(BillActivity.this, CustomerOrderDetailsActivity.class);
                startActivity(i);
            }
        });
    }
}