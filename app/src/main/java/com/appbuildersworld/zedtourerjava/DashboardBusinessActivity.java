package com.appbuildersworld.zedtourerjava;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.appbuildersworld.zedtourerjava.activities.ProductsAdminActivity;
import com.appbuildersworld.zedtourerjava.ui.ProcessDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class DashboardBusinessActivity extends AppCompatActivity {

    private FloatingActionButton fabSales;
    private FloatingActionButton fabCustomers;
    private FloatingActionButton fabProducts;
    private FloatingActionButton fabCashiers;
    private FloatingActionButton fabBusinessProfile;
    private FloatingActionButton fabLogout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_business);

        Intent intent = getIntent();
        String businessJSON = intent.getStringExtra("user");
        Log.d("NNN", "User JSON: " + businessJSON);

        fabSales = findViewById(R.id.fabSales);
        fabSales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(DashboardBusinessActivity.this, SalesActivity.class);
                startActivity(i);
            }
        });

        fabCustomers = findViewById(R.id.fabCustomers);
        fabCustomers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(DashboardBusinessActivity.this, CustomerListActivity.class);
                startActivity(i);
            }
        });

        fabProducts = findViewById(R.id.fabProducts);
        fabProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(DashboardBusinessActivity.this, ProductsAdminActivity.class);
                startActivity(i);
            }
        });

        fabCashiers = findViewById(R.id.fabCashiers);
        fabCashiers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(DashboardBusinessActivity.this, CashierActivity.class);
                startActivity(i);
            }
        });

        fabBusinessProfile = findViewById(R.id.fabBusinessProfile);
        fabBusinessProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(DashboardBusinessActivity.this, BusinessProfileActivity.class);
                startActivity(i);
            }
        });

        fabLogout = findViewById(R.id.fabLogout);
        fabLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


    }

}