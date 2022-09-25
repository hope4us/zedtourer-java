package com.appbuildersworld.zedtourerjava;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.appbuildersworld.zedtourerjava.activities.MapsActivity;
import com.appbuildersworld.zedtourerjava.models.MBusiness;
import com.appbuildersworld.zedtourerjava.models.MCustomer;
import com.appbuildersworld.zedtourerjava.ui.ProcessDialog;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class CustomerSignupActivity extends AppCompatActivity {

    private ProcessDialog connectionFailureDialog;
    private ProcessDialog globalErrorDialog;
    private TextInputEditText tieCustomerName;
    private TextInputEditText tiePhone;
    private TextInputEditText tiePassword;
    private String gender = "Male";

    private int clickCount = 0;
    private String response;

    Button bRegisterCustomer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_signup);
        setupViews();

      /*  bRegisterCustomer = findViewById(R.id.bRegisterCustomer);
        bRegisterCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tieCustomerName = (TextInputEditText) findViewById(R.id.tieCustomerName);
                tiePhone = (TextInputEditText) findViewById(R.id.tiePhone);
                tiePassword = (TextInputEditText) findViewById(R.id.tiePassword);

                MCustomer m = new MCustomer();
                m.setCustomerNames(tieCustomerName.getText().toString());
                m.setPhone(tiePhone.getText().toString());
                m.setPassword(tiePassword.getText().toString());
                m.setGender(gender);

                Intent i = new Intent(CustomerSignupActivity.this, VerificationActivity.class);
                i.putExtra("customer", (Serializable) m);
                i.putExtra("userTypeId", 2);
                startActivity(i);
            }
        });*/

    }

    public void setupViews() {
        tieCustomerName = (TextInputEditText) findViewById(R.id.tieCustomerName);
        tiePhone = (TextInputEditText) findViewById(R.id.tiePhone);
        tiePassword = (TextInputEditText) findViewById(R.id.tiePassword);

        bRegisterCustomer = findViewById(R.id.bRegisterCustomer);
        bRegisterCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MCustomer m = new MCustomer();
                m.setCustomerNames(tieCustomerName.getText().toString());
                m.setGender(gender);
                m.setPhone(tiePhone.getText().toString());
                m.setPassword(tiePassword.getText().toString());


                JSONObject customer = new JSONObject();
                try {
                    customer.put("customerName", m.getCustomerNames());
                    customer.put("phone", m.getPhone());
                    customer.put("gender", m.getGender());
                    customer.put("password", m.getPassword());
                    customer.put("userType", 2);
                    customer.put("imageUrl", "no_profile_pic.png");

                } catch (JSONException e) {
                    e.printStackTrace();
                }


                Intent i = new Intent(CustomerSignupActivity.this, VerificationActivity.class);
                i.putExtra("customer", (Serializable) m);
                i.putExtra("userType", 2);
                startActivity(i);
            }
        });



    }

}