package com.appbuildersworld.zedtourerjava;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.net.Inet4Address;

public class DashboardBusinessActivity extends AppCompatActivity {

    private FloatingActionButton fabSales;
    private FloatingActionButton fabCustomers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_business);

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


    }
}