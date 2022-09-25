package com.appbuildersworld.zedtourerjava;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.appbuildersworld.zedtourerjava.connectivity.Constant;
import com.appbuildersworld.zedtourerjava.models.MCashier;
import com.appbuildersworld.zedtourerjava.utils.Tools;


public class CashierDetailsActivity extends AppCompatActivity {
    private MCashier cashier;
    private TextView tvNames, tvPhone, tvGender;
    private ImageButton ibMessage, ibPhone;
    private ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cashier_details);
        Intent i = getIntent();
        cashier = (MCashier) i.getSerializableExtra("cashier");
        initToolbar();
        initComponent();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Cashier Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setSystemBarColor(this, R.color.zed_light);
    }

    private void initComponent() {
        tvNames = findViewById(R.id.tvNames);
        tvPhone = findViewById(R.id.tvPhone);
        tvGender = findViewById(R.id.tvGender);
        ibMessage = findViewById(R.id.ibMessage);
        ibPhone = findViewById(R.id.ibPhone);
        image = findViewById(R.id.image);

        Tools.displayImageRoundUrl(this, image, Constant.baseURL + "/files/images/" + cashier.getImageUrl());

        tvNames.setText(cashier.getCashierNames());
        tvPhone.setText(cashier.getPhone());
        tvGender.setText(cashier.getGender());
        ibMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri sms_uri = Uri.parse("smsto:" + cashier.getPhone());
                Intent sms_intent = new Intent(Intent.ACTION_SENDTO, sms_uri);
                sms_intent.putExtra("sms_body", "");
                startActivity(sms_intent);
            }
        });

        ibPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + cashier.getPhone()));
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_cashier_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else if (item.getItemId() == R.id.mbDeleteAccount) {
            Toast.makeText(getApplicationContext(), "Delete account is not available in prototype version", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
}
