package com.appbuildersworld.zedtourerjava;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.appbuildersworld.zedtourerjava.models.MCustomer;
import com.appbuildersworld.zedtourerjava.ui.ProcessDialog;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;

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

        bRegisterCustomer = findViewById(R.id.bRegisterCustomer);
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
        });

    }

    public void setupViews(){
        connectionFailureDialog = new ProcessDialog(this);
        globalErrorDialog = new ProcessDialog(this);

        bRegisterCustomer = findViewById(R.id.bRegisterCustomer);
        bRegisterCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }
}