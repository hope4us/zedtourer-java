package com.appbuildersworld.zedtourerjava;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.appbuildersworld.zedtourerjava.activities.LoginActivity;
import com.appbuildersworld.zedtourerjava.activities.ProductsAdminActivity;
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
                showLogoutDialog();
            }
        });


    }

    private void showLogoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Logout");
        builder.setPositiveButton("LOGOUT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                SharedPreferences settings = DashboardBusinessActivity.this.getSharedPreferences("userPrefs", Context.MODE_PRIVATE);
                settings.edit().clear().commit();
                Intent intent = new Intent(DashboardBusinessActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("CANCEL", null);
        builder.show();
    }

}