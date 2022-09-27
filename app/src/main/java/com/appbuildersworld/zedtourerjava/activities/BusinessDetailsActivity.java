package com.appbuildersworld.zedtourerjava.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.appbuildersworld.zedtourerjava.R;
import com.appbuildersworld.zedtourerjava.activities.customer.ProductsCustomerActivity;
import com.appbuildersworld.zedtourerjava.models.MBusiness;
import com.appbuildersworld.zedtourerjava.utils.Tools;

public class BusinessDetailsActivity extends AppCompatActivity {

    private ImageView ivBkg;
    private TextView tvBusinessName;
    private Button bViewProducts;

    private MBusiness business;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_details);

        Intent i = getIntent();
        business = (MBusiness) i.getSerializableExtra("business");
        Log.d("NNN", "Nature of Business: " + business.getNatureOfBusiness());
        Log.d("NNN", "Products: " + business.getProducts());


        ivBkg = (ImageView) findViewById(R.id.ivbkg);
        tvBusinessName = (TextView) findViewById(R.id.tvBusinessName);
        bViewProducts = (Button) findViewById(R.id.bViewProducts);

        Tools.displayImageResized(this, ivBkg, business.getImageUrl());
        tvBusinessName.setText(business.getBusinessName());
        bViewProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(BusinessDetailsActivity.this, ProductsCustomerActivity.class);
                i.putExtra("businessId", business.getBusinessId());
                i.putExtra("products", business.getProducts());
                startActivity(i);
            }
        });
    }
}