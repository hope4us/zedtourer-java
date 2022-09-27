package com.appbuildersworld.zedtourerjava.activities.customer;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import com.appbuildersworld.zedtourerjava.R;
import com.appbuildersworld.zedtourerjava.connectivity.Constant;
import com.appbuildersworld.zedtourerjava.utils.Tools;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class ProductDetailsCustomerActivity extends AppCompatActivity {

    private View parent_view;
    private TextView tv_qty, tvProductType, tvProductName, tvProductDescription, tvPrice;
    private ImageView image;

    private String productName;
    private String category;
    private String imageUrl;
    private String description;
    private double price;
    private int productId;
    private int businessId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details_customer);
        parent_view = findViewById(R.id.parent_view);

        Intent i = getIntent();
        productName = i.getStringExtra("productName");
        category = i.getStringExtra("category");
        imageUrl = i.getStringExtra("imageUrl");
        description = i.getStringExtra("description");
        price = i.getDoubleExtra("price", 0);
        productId = i.getIntExtra("productId", 0);
        businessId = i.getIntExtra("businessId", 0);

        initToolbar();
        initComponent();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Product Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setSystemBarColor(this);
    }

    private void initComponent() {
           tv_qty = (TextView) findViewById(R.id.tv_qty);
        ((FloatingActionButton) findViewById(R.id.fab_qty_sub)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int qty = Integer.parseInt(tv_qty.getText().toString());
                if (qty > 1) {
                    qty--;
                    tv_qty.setText(qty + "");
                }
            }
        });

        ((FloatingActionButton) findViewById(R.id.fab_qty_add)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int qty = Integer.parseInt(tv_qty.getText().toString());
                if (qty < 10) {
                    qty++;
                    tv_qty.setText(qty + "");
                }
            }
        });

        ((AppCompatButton) findViewById(R.id.bt_add_to_cart)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ProductDetailsCustomerActivity.this, CartActivity.class);
                i.putExtra("productName", productName);
                i.putExtra("category", category);
                i.putExtra("imageUrl", imageUrl);
                i.putExtra("description", description);
                i.putExtra("price", price);
                i.putExtra("amount", (price * Integer.parseInt(tv_qty.getText().toString())));
                i.putExtra("productId", productId);
                i.putExtra("businessId", businessId);

                startActivity(i);
            }
        });

        tvProductType = (TextView) findViewById(R.id.tvProductType);
        tvProductName = (TextView) findViewById(R.id.tvProductName);
        tvProductDescription = (TextView) findViewById(R.id.tvProductDescription);
        tvPrice = (TextView) findViewById(R.id.tvPrice);
        image = (ImageView) findViewById(R.id.image);

        tvProductType.setText(category);
        tvProductName.setText(productName);
        tvProductDescription.setText(description);
        tvPrice.setText("" + price);

        Tools.displayImageOriginal(this, image, Constant.baseURL + "/files/images/" + imageUrl);



    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_cart_setting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else {
            Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
}
