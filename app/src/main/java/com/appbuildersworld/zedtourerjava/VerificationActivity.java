package com.appbuildersworld.zedtourerjava;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.appbuildersworld.zedtourerjava.connectivity.Constant;
import com.appbuildersworld.zedtourerjava.interfaces.RetrofitInterface;
import com.appbuildersworld.zedtourerjava.models.MBusiness;
import com.appbuildersworld.zedtourerjava.models.MCustomer;
import com.appbuildersworld.zedtourerjava.models.MProductCategory;
import com.appbuildersworld.zedtourerjava.ui.ProcessDialog;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class VerificationActivity extends AppCompatActivity {

    private AppCompatButton bVerify;
    private AppCompatEditText num1;
    private AppCompatEditText num2;
    private AppCompatEditText num3;
    private AppCompatEditText num4;
    private TextView tvWrongCode;

    private String verificationCode = "7788";
    private boolean isWrong = false;
    private MBusiness business;
    private MCustomer customer;

    private ProcessDialog connectionFailureDialog;
    private ProcessDialog globalErrorDialog;
    private ProcessDialog registerBusinessPDialog;
    private ProcessDialog registerCustomerPDialog;
    private int clickCount = 0;
    private String response;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        registerBusinessPDialog = new ProcessDialog(this);
        registerBusinessPDialog.setTouchCancel(false);
        registerBusinessPDialog.setCancelable(true);

        registerCustomerPDialog = new ProcessDialog(this);
        registerCustomerPDialog.setTouchCancel(false);
        registerCustomerPDialog.setCancelable(true);

        connectionFailureDialog = new ProcessDialog(this);
        globalErrorDialog = new ProcessDialog(this);

        business = (MBusiness) getIntent().getSerializableExtra("business");
        customer = (MCustomer) getIntent().getSerializableExtra("customer");

        num1 = (AppCompatEditText) findViewById(R.id.num1);
        num1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (isWrong) {

                    isWrong = false;
                    tvWrongCode.setVisibility(View.GONE);
                } else {
                    num2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        num2 = (AppCompatEditText) findViewById(R.id.num2);
        num2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (isWrong) {

                    isWrong = false;
                    tvWrongCode.setVisibility(View.GONE);
                } else {
                    num3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        num3 = (AppCompatEditText) findViewById(R.id.num3);
        num3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (isWrong) {

                    isWrong = false;
                    tvWrongCode.setVisibility(View.GONE);
                } else {
                    num4.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        num4 = (AppCompatEditText) findViewById(R.id.num4);
        num4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (isWrong) {
                    num1.setText("");
                    num2.setText("");
                    num3.setText("");

                    isWrong = false;
                    tvWrongCode.setVisibility(View.GONE);
                    num1.requestFocus();
                } else {

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        tvWrongCode = (TextView) findViewById(R.id.tvWrongCode);

        Intent intent = getIntent();
        int userType = intent.getIntExtra("userType", 0);

        bVerify = findViewById(R.id.bVerify);
        bVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String t1 = num1.getText().toString()
                        + num2.getText().toString()
                        + num3.getText().toString()
                        + num4.getText().toString();

                if (t1.equals(verificationCode)) {
                    if (userType == 1) {
                        Log.d("NNN", "Business Name: " + business.getBusinessName());
                        JSONArray productIds = null;
                        try {
                            productIds = new JSONArray(business.getProducts());
                            for (int i = 0; i < productIds.length(); i++) {
                                Log.d("NNN", "Product Id: " + productIds.get(i));
                            }

                            JSONObject coordinates = null;
                            coordinates = new JSONObject(business.getLocationCoordinates());
                            Log.d("NNN", "Latitude: " + coordinates.getDouble("latitude"));
                            Log.d("NNN", "Longitude: " + coordinates.getDouble("longitude"));

                            JSONObject user = null;
                            user = new JSONObject(business.getUser());
                            Log.d("NNN", "Owner Name: " + user.getString("names"));
                            Log.d("NNN", "Phone: " + user.getString("phone"));
                            Log.d("NNN", "Password: " + user.getString("password"));

                            JSONObject jsonBusiness = new JSONObject();
                            jsonBusiness.put("businessName", business.getBusinessName());
                            JSONArray jsonArray = new JSONArray();

                            for (int i = 0; i < productIds.length(); i++) {
                                jsonArray.put(productIds.get(i));
                            }

                            jsonBusiness.put("businessName", business.getBusinessName());
                            jsonBusiness.put("products", jsonArray);
                            jsonBusiness.put("latitude", coordinates.get("latitude"));
                            jsonBusiness.put("longitude", coordinates.get("longitude"));
                            jsonBusiness.put("ownerName", user.getString("names"));
                            jsonBusiness.put("phone", user.getString("phone"));
                            jsonBusiness.put("password", user.getString("password"));
                            jsonBusiness.put("userType", userType);

                            Log.d("NNN", jsonBusiness.toString(2));

                            registerBusiness(jsonBusiness.toString(), userType);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d("NNN", "Error: " + e.getLocalizedMessage());
                        }
                    }

                    if (userType == 2) {
                        Log.d("NNN", "Customer Name: " + customer.getCustomerNames());
                        Log.d("NNN", "Phone: " + customer.getPhone());
                        Log.d("NNN", "Password: " + customer.getPassword());
                        Log.d("NNN", "Gender: " + customer.getGender());

                        JSONObject jsonCustomer = new JSONObject();
                        try {
                            jsonCustomer.put("customerName", customer.getCustomerNames());
                            jsonCustomer.put("phone", customer.getPhone());
                            jsonCustomer.put("password", customer.getPassword());
                            jsonCustomer.put("gender", customer.getGender());
                            jsonCustomer.put("userType", userType);

                            registerCustomer(jsonCustomer.toString(), userType);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }


                } else {
                    Log.d("NNN", "Wrong code (" + t1 + ")");
                    isWrong = true;
                    tvWrongCode.setVisibility(View.VISIBLE);
                }

            }
        });


    }

    private void showVerifiedDialog(int userType) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_info);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;


        ((AppCompatButton) dialog.findViewById(R.id.bContinue)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (userType == 1) {
                    Intent i = new Intent(VerificationActivity.this, DashboardBusinessActivity.class);
                    startActivity(i);
                    finish();
                }

                if (userType == 2) {
                    Intent i = new Intent(VerificationActivity.this, DashboardCustomerActivity.class);
                    startActivity(i);
                    finish();
                }
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    private void registerBusiness(String json, int userType) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constant.baseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        final RetrofitInterface retrofitInterface = retrofit.create(RetrofitInterface.class);
        final retrofit2.Call registerBusinessCall = retrofitInterface.registerBusiness(json);
        registerBusinessPDialog.showProcessingDialog("Verifying account.. Please wait..");

        registerBusinessPDialog.setOnCloseDialogListener(new ProcessDialog.CloseDialogClickListener() {
            @Override
            public void onCloseDialogClick() {
                registerBusinessPDialog.setCancelable(true);
                registerBusinessPDialog.dismissDialog();
                registerBusinessCall.cancel();

            }
        });
        registerBusinessPDialog.setOnBackPressedListener(new ProcessDialog.BackPressedListener() {
            @Override
            public void onBackPressedClick() {
                registerBusinessPDialog.setCancelable(true);
                registerBusinessPDialog.dismissDialog();
                registerBusinessCall.cancel();
            }
        });

        registerBusinessCall.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(retrofit2.Call<ResponseBody> classCall, final Response<ResponseBody> netResponse) {

                registerBusinessPDialog.dismissDialog();

                if (classCall.isCanceled()) {
                    Log.d("NNN", "Network call cancelled");
                } else {

                    try {

                        if (netResponse.isSuccessful()) {
                            response = netResponse.body().string();
                            Log.d("NNN", "Response: " + response);

                        } else {
                            response = netResponse.errorBody().string();
                            Log.d("NNN", "Response: " + response);

                        }

                        JSONObject responseJObject = new JSONObject(response);
                        boolean error = responseJObject.getBoolean("error");
                        if (error) {

                        } else {
                            JSONObject messageObj = responseJObject.getJSONObject("message");
                            int businessId = messageObj.getInt("businessId");
                            String user = messageObj.getString("user");
                            String coordinates = messageObj.getString("coordinates");
                            String products = messageObj.getString("products");

                            SharedPreferences sharedpreferences = getSharedPreferences("userPrefs", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.putInt("businessId", businessId);
                            editor.putString("user", user);
                            editor.putString("coordinates", coordinates);
                            editor.putString("products", products);
                            editor.putInt("userType", userType);
                            editor.putBoolean("onboard", false);
                            editor.commit();

                            showVerifiedDialog(userType);
                        }

                    } catch (final Exception e) {
                        registerBusinessPDialog.dismissDialog();

                        showErrorDialog(e);
                        globalErrorDialog.setOnActionListener(new ProcessDialog.ActionClickListener() {
                            @Override
                            public void onActionClick() {
                                globalErrorDialog.dismissDialog();
                                registerBusiness(json, userType);
                            }
                        });

                        globalErrorDialog.setOnBackPressedListener(new ProcessDialog.BackPressedListener() {
                            @Override
                            public void onBackPressedClick() {
                                globalErrorDialog.dismissDialog();
                            }
                        });

                        globalErrorDialog.setOnCloseDialogListener(new ProcessDialog.CloseDialogClickListener() {
                            @Override
                            public void onCloseDialogClick() {
                                globalErrorDialog.dismissDialog();

                            }
                        });
                    }

                }

            }

            @Override
            public void onFailure(retrofit2.Call<ResponseBody> classCall, Throwable t) {
                if (classCall.isCanceled()) {
                    Log.d("NNN", "Network call cancelled: ");
                } else {
                    registerBusinessPDialog.dismissDialog();
                    showConnectionFailureDialog(t);
                    connectionFailureDialog.setOnActionListener(new ProcessDialog.ActionClickListener() {
                        @Override
                        public void onActionClick() {
                            connectionFailureDialog.dismissDialog();
                            registerBusiness(json, userType);

                        }
                    });

                    connectionFailureDialog.setOnBackPressedListener(new ProcessDialog.BackPressedListener() {
                        @Override
                        public void onBackPressedClick() {
                            connectionFailureDialog.dismissDialog();
                            onBackPressed();
                        }
                    });

                    connectionFailureDialog.setOnCloseDialogListener(new ProcessDialog.CloseDialogClickListener() {
                        @Override
                        public void onCloseDialogClick() {
                            connectionFailureDialog.dismissDialog();
                            onBackPressed();

                        }
                    });
                }
            }

        });
    }

    private void registerCustomer(String json, int userType) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constant.baseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        final RetrofitInterface retrofitInterface = retrofit.create(RetrofitInterface.class);
        final retrofit2.Call registerCustomerCall = retrofitInterface.registerCustomer(json);
        registerCustomerPDialog.showProcessingDialog("Verifying account.. Please wait..");

        registerCustomerPDialog.setOnCloseDialogListener(new ProcessDialog.CloseDialogClickListener() {
            @Override
            public void onCloseDialogClick() {
                registerCustomerPDialog.setCancelable(true);
                registerCustomerPDialog.dismissDialog();
                registerCustomerCall.cancel();

            }
        });
        registerCustomerPDialog.setOnBackPressedListener(new ProcessDialog.BackPressedListener() {
            @Override
            public void onBackPressedClick() {
                registerCustomerPDialog.setCancelable(true);
                registerCustomerPDialog.dismissDialog();
                registerCustomerCall.cancel();
            }
        });

        registerCustomerCall.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(retrofit2.Call<ResponseBody> classCall, final Response<ResponseBody> netResponse) {

                registerCustomerPDialog.dismissDialog();

                if (classCall.isCanceled()) {
                    Log.d("NNN", "Network call cancelled");
                } else {

                    try {

                        if (netResponse.isSuccessful()) {
                            response = netResponse.body().string();
                            Log.d("NNN", "Response: " + response);

                        } else {
                            response = netResponse.errorBody().string();
                            Log.d("NNN", "Response: " + response);

                        }

                        JSONObject responseJObject = new JSONObject(response);
                        boolean error = responseJObject.getBoolean("error");
                        if (error) {

                        } else {
                            int customerId = responseJObject.getInt("message");
                            SharedPreferences sharedpreferences = getSharedPreferences("userPrefs", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.putInt("customerId", customerId);
                            editor.putInt("userType", userType);
                            editor.commit();

                            showVerifiedDialog(userType);
                        }

                    } catch (final Exception e) {
                        registerCustomerPDialog.dismissDialog();

                        showErrorDialog(e);
                        globalErrorDialog.setOnActionListener(new ProcessDialog.ActionClickListener() {
                            @Override
                            public void onActionClick() {
                                globalErrorDialog.dismissDialog();
                                registerBusiness(json, userType);
                            }
                        });

                        globalErrorDialog.setOnBackPressedListener(new ProcessDialog.BackPressedListener() {
                            @Override
                            public void onBackPressedClick() {
                                globalErrorDialog.dismissDialog();
                            }
                        });

                        globalErrorDialog.setOnCloseDialogListener(new ProcessDialog.CloseDialogClickListener() {
                            @Override
                            public void onCloseDialogClick() {
                                globalErrorDialog.dismissDialog();

                            }
                        });
                    }

                }

            }

            @Override
            public void onFailure(retrofit2.Call<ResponseBody> classCall, Throwable t) {
                if (classCall.isCanceled()) {
                    Log.d("NNN", "Network call cancelled: ");
                } else {
                    registerCustomerPDialog.dismissDialog();
                    showConnectionFailureDialog(t);
                    connectionFailureDialog.setOnActionListener(new ProcessDialog.ActionClickListener() {
                        @Override
                        public void onActionClick() {
                            connectionFailureDialog.dismissDialog();
                            registerBusiness(json, userType);

                        }
                    });

                    connectionFailureDialog.setOnBackPressedListener(new ProcessDialog.BackPressedListener() {
                        @Override
                        public void onBackPressedClick() {
                            connectionFailureDialog.dismissDialog();
                            onBackPressed();
                        }
                    });

                    connectionFailureDialog.setOnCloseDialogListener(new ProcessDialog.CloseDialogClickListener() {
                        @Override
                        public void onCloseDialogClick() {
                            connectionFailureDialog.dismissDialog();
                            onBackPressed();

                        }
                    });
                }
            }

        });
    }

    private void showErrorDialog(final Exception e) {
        globalErrorDialog.showResponseDialog("An error occurred. Please try again or contact support if error persists.", "Try again");
        globalErrorDialog.enableResponseViewBtn();
        globalErrorDialog.setCancelable(true);
        globalErrorDialog.setTouchCancel(false);

        globalErrorDialog.setOnNetResponseListener(new ProcessDialog.NetResponseClickListener() {
            @Override
            public void onNetResponseClick() {
                Log.d("NNN", "Response Button Clicked");
                clickCount++;
                if (clickCount == 6) {
                    final ProcessDialog errorDialog = new ProcessDialog(VerificationActivity.this);
                    errorDialog.showResponseDialog("Error: \n" + e.getLocalizedMessage(), "Close");
                    errorDialog.setCancelable(true);
                    errorDialog.setTouchCancel(false);
                    errorDialog.setOnActionListener(new ProcessDialog.ActionClickListener() {
                        @Override
                        public void onActionClick() {
                            errorDialog.dismissDialog();
                            clickCount = 0;
                        }
                    });
                    errorDialog.setOnCloseDialogListener(new ProcessDialog.CloseDialogClickListener() {
                        @Override
                        public void onCloseDialogClick() {
                            errorDialog.dismissDialog();
                            clickCount = 0;
                        }
                    });
                    errorDialog.setOnBackPressedListener(new ProcessDialog.BackPressedListener() {
                        @Override
                        public void onBackPressedClick() {
                            errorDialog.dismissDialog();
                            clickCount = 0;
                        }
                    });

                }
            }
        });

    }

    private void showConnectionFailureDialog(final Throwable t) {
        connectionFailureDialog.showResponseDialog("There seems to be a problem with the connection. Please try again or contact support if problem persists.", "Try again");
        connectionFailureDialog.enableResponseViewBtn();
        connectionFailureDialog.setCancelable(true);
        connectionFailureDialog.setTouchCancel(false);

        connectionFailureDialog.setOnNetResponseListener(new ProcessDialog.NetResponseClickListener() {
            @Override
            public void onNetResponseClick() {
                Log.d("NNN", "Response Button Clicked");
                clickCount++;
                if (clickCount == 6) {
                    final ProcessDialog errorDialog = new ProcessDialog(VerificationActivity.this);
                    errorDialog.showResponseDialog("Error: \n" + t.getLocalizedMessage(), "Close");
                    errorDialog.setCancelable(true);
                    errorDialog.setTouchCancel(false);
                    errorDialog.setOnActionListener(new ProcessDialog.ActionClickListener() {
                        @Override
                        public void onActionClick() {
                            errorDialog.dismissDialog();
                            clickCount = 0;
                        }
                    });
                    errorDialog.setOnCloseDialogListener(new ProcessDialog.CloseDialogClickListener() {
                        @Override
                        public void onCloseDialogClick() {
                            errorDialog.dismissDialog();
                            clickCount = 0;
                        }
                    });
                    errorDialog.setOnBackPressedListener(new ProcessDialog.BackPressedListener() {
                        @Override
                        public void onBackPressedClick() {
                            errorDialog.dismissDialog();
                            clickCount = 0;
                        }
                    });

                }
            }
        });
    }
}