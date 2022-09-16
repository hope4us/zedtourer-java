package com.appbuildersworld.zedtourerjava;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class SalesActivity extends AppCompatActivity {

    private CardView cvSale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales);

        cvSale = findViewById(R.id.cvSale);
        cvSale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SalesActivity.this, SaleReceiptActivity.class);
                startActivity(i);
            }
        });

    }
}