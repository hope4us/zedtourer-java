package com.appbuildersworld.zedtourerjava.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.appbuildersworld.zedtourerjava.activities.admin.BusinessSignUpActivity;
import com.appbuildersworld.zedtourerjava.CustomerSignupActivity;
import com.appbuildersworld.zedtourerjava.DashboardCashierActivity;
import com.appbuildersworld.zedtourerjava.activities.admin.DashboardBusinessActivity;
import com.appbuildersworld.zedtourerjava.R;
import com.appbuildersworld.zedtourerjava.activities.cashier.CashierOrderListActivity;
import com.appbuildersworld.zedtourerjava.activities.customer.DashboardCustomerActivity;
import com.appbuildersworld.zedtourerjava.connectivity.Constant;
import com.appbuildersworld.zedtourerjava.interfaces.RetrofitInterface;
import com.appbuildersworld.zedtourerjava.ui.ProcessDialog;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class LoginActivity extends AppCompatActivity {

    private ProcessDialog connectionFailureDialog;
    private ProcessDialog globalErrorDialog;
    private ProcessDialog authenticatePDialog;
    private int clickCount = 0;
    private String response;

    private TextInputEditText tieUsername;
    private TextInputEditText tiePassword;
    private Button bSignIn;
    private TextView bBusinessSignUp;
    private TextView bCustomerSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        authenticatePDialog = new ProcessDialog(this);
        authenticatePDialog.setTouchCancel(false);
        authenticatePDialog.setCancelable(true);
        connectionFailureDialog = new ProcessDialog(this);
        globalErrorDialog = new ProcessDialog(this);

        tieUsername = (TextInputEditText) findViewById(R.id.tieUsername);
        tiePassword = (TextInputEditText) findViewById(R.id.tiePassword);

        bSignIn = (Button) findViewById(R.id.bSignIn);
        bSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String username = tieUsername.getText().toString().trim();
                String password = tiePassword.getText().toString().trim();

                if (username.isEmpty()) {
                    tieUsername.requestFocus();
                    tieUsername.setError("Phone number is empty");
                } else if (password.isEmpty()) {
                    tiePassword.requestFocus();
                    tiePassword.setError("Password is empty");
                } else {

                    JSONObject jCredentials = new JSONObject();
                    try {
                        jCredentials.put("username", tieUsername.getText().toString());
                        jCredentials.put("password", tiePassword.getText().toString());

                        authenticate(jCredentials.toString());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        });

        bBusinessSignUp = findViewById(R.id.bBusinessSignUp);
        bBusinessSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this, BusinessSignUpActivity.class);
                startActivity(i);
            }
        });

        bCustomerSignUp = findViewById(R.id.bCustomerSignUp);
        bCustomerSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this, CustomerSignupActivity.class);
                startActivity(i);
            }
        });

    }

    private void authenticate(String json) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constant.baseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        final RetrofitInterface retrofitInterface = retrofit.create(RetrofitInterface.class);
        final retrofit2.Call authenticateCall = retrofitInterface.authenticate(json);
        authenticatePDialog.showProcessingDialog("Authenticating user.. Please wait..");

        authenticatePDialog.setOnCloseDialogListener(new ProcessDialog.CloseDialogClickListener() {
            @Override
            public void onCloseDialogClick() {
                authenticatePDialog.setCancelable(true);
                authenticatePDialog.dismissDialog();
                authenticateCall.cancel();

            }
        });
        authenticatePDialog.setOnBackPressedListener(new ProcessDialog.BackPressedListener() {
            @Override
            public void onBackPressedClick() {
                authenticatePDialog.setCancelable(true);
                authenticatePDialog.dismissDialog();
                authenticateCall.cancel();
            }
        });

        authenticateCall.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(retrofit2.Call<ResponseBody> classCall, final Response<ResponseBody> netResponse) {

                authenticatePDialog.dismissDialog();

                if (classCall.isCanceled()) {
                    Log.d("NNN", "Network call cancelled");
                } else {

                    try {

                        if (netResponse.isSuccessful()) {
                            response = netResponse.body().string();
                            Log.d("NNN", "Response: " + response);

                        } else {
                            response = netResponse.errorBody().string();
                            Log.d("NNN", "Response Error: " + response);

                        }

                        JSONObject responseJObject = new JSONObject(response);
                        boolean error = responseJObject.getBoolean("error");
                        if (error) {

                        } else {

                            JSONObject messageObj = responseJObject.getJSONObject("message");
                            JSONObject userObj = messageObj.getJSONObject("user");

                            int userType = userObj.getInt("userType");
                            String user = messageObj.getString("user");
                            switch (userType) {
                                case 0:
                                    showLoginFailDialog();
                                case 1:
                                    try {

                                        int businessId = messageObj.getInt("businessId");
                                        String coordinates = messageObj.getString("coordinates");
                                        String products = messageObj.getString("products");
                                        String businessName = messageObj.getString("businessName");

                                        SharedPreferences sharedpreferences = getSharedPreferences("userPrefs", Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sharedpreferences.edit();
                                        editor.putInt("businessId", businessId);
                                        editor.putString("businessName", businessName);
                                        editor.putString("user", user);
                                        editor.putString("coordinates", coordinates);
                                        editor.putString("products", products);
                                        editor.putInt("userType", userType);
                                        editor.putBoolean("onboard", false);
                                        editor.commit();

                                        Intent i = new Intent(LoginActivity.this, DashboardBusinessActivity.class);
                                        startActivity(i);
                                        finish();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        Log.d("NNN", "Error: " + e.getLocalizedMessage());
                                    }

                                    break;
                                case 2:
                                    try {

                                        int customerId = messageObj.getInt("customerId");

                                        SharedPreferences sharedpreferences = getSharedPreferences("userPrefs", Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sharedpreferences.edit();
                                        editor.putInt("customerId", customerId);
                                        editor.putString("user", user);
                                        editor.putInt("userType", userType);
                                        editor.putBoolean("onboard", false);
                                        editor.commit();

                                        Intent i = new Intent(LoginActivity.this, DashboardCustomerActivity.class);
                                        startActivity(i);
                                        finish();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        Log.d("NNN", "Error: " + e.getLocalizedMessage());
                                    }
                                    break;
                                case 3:
                                    try {

                                        String business = messageObj.getString("business");
                                        JSONObject businessJSON = new JSONObject(business);

                                        int businessId = businessJSON.getInt("businessId");
                                        String businessName = businessJSON.getString("businessName");

                                        String status = messageObj.getString("status");

                                        Intent i;

                                        switch (status) {
                                            case "Active":
                                                SharedPreferences sharedpreferences = getSharedPreferences("userPrefs", Context.MODE_PRIVATE);
                                                SharedPreferences.Editor editor = sharedpreferences.edit();
                                                editor.putInt("businessId", businessId);
                                                editor.putString("businessName", businessName);
                                                editor.putString("user", user);
                                                editor.putInt("userType", userType);
                                                editor.putBoolean("onboard", false);
                                                editor.commit();

                                                i = new Intent(LoginActivity.this, CashierOrderListActivity.class);
                                                startActivity(i);
                                                finish();
                                                break;
                                            case "Inactive":

                                                Log.d("NNN", "Cashier Inactive");
                                                Log.d("NNN", "User ID: " + userObj.getInt("userId"));
                                                i = new Intent(LoginActivity.this, ConfirmCashierAccountActivity.class);
                                                i.putExtra("phone", userObj.getString("phone"));
                                                i.putExtra("userId", userObj.getInt("userId"));
                                                i.putExtra("cashierId", messageObj.getInt("cashierId"));

                                                startActivity(i);
                                                break;
                                        }


                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        Log.d("NNN", "Error: " + e.getLocalizedMessage());
                                    }

                                    break;
                            }

                        }

                    } catch (final Exception e) {
                        authenticatePDialog.dismissDialog();

                        showErrorDialog(e);
                        globalErrorDialog.setOnActionListener(new ProcessDialog.ActionClickListener() {
                            @Override
                            public void onActionClick() {
                                globalErrorDialog.dismissDialog();
                                authenticate(json);
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
                    authenticatePDialog.dismissDialog();
                    showConnectionFailureDialog(t);
                    connectionFailureDialog.setOnActionListener(new ProcessDialog.ActionClickListener() {
                        @Override
                        public void onActionClick() {
                            connectionFailureDialog.dismissDialog();
                            authenticate(json);

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

    private void showLoginFailDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_warning);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;


        ((AppCompatButton) dialog.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
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
                    final ProcessDialog errorDialog = new ProcessDialog(LoginActivity.this);
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
                    final ProcessDialog errorDialog = new ProcessDialog(LoginActivity.this);
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