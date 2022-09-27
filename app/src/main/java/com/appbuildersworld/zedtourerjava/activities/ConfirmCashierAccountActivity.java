package com.appbuildersworld.zedtourerjava.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

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

import com.appbuildersworld.zedtourerjava.CashierDashboardActivity;
import com.appbuildersworld.zedtourerjava.R;
import com.appbuildersworld.zedtourerjava.connectivity.Constant;
import com.appbuildersworld.zedtourerjava.interfaces.RetrofitInterface;
import com.appbuildersworld.zedtourerjava.ui.ProcessDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ConfirmCashierAccountActivity extends AppCompatActivity {

    private ProcessDialog connectionFailureDialog;
    private ProcessDialog globalErrorDialog;
    private ProcessDialog authenticatePDialog;
    private int clickCount = 0;
    private String response;

    private TextInputEditText tieUsername;
    private TextInputEditText tiePassword;
    private TextInputEditText tieConfirmPassword;
    private Button bSignIn;

    private int userId;
    private int cashierId;
    private String phone;

    private View parent_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_cashier_account);

        parent_view = findViewById(android.R.id.content);

        Intent i = getIntent();
        phone = i.getStringExtra("phone");
        userId = i.getIntExtra("userId", 0);
        cashierId = i.getIntExtra("cashierId", 0);

        Log.d("NNN", "Confirm User ID: " + userId);

        authenticatePDialog = new ProcessDialog(this);
        authenticatePDialog.setTouchCancel(false);
        authenticatePDialog.setCancelable(true);
        connectionFailureDialog = new ProcessDialog(this);
        globalErrorDialog = new ProcessDialog(this);

        tieUsername = (TextInputEditText) findViewById(R.id.tieUsername);
        tieUsername.setText(phone);
        tiePassword = (TextInputEditText) findViewById(R.id.tiePassword);
        tieConfirmPassword = (TextInputEditText) findViewById(R.id.tieConfirmPassword);


        bSignIn = (Button) findViewById(R.id.bSignIn);
        bSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String password = tiePassword.getText().toString().trim();

               if (password.isEmpty()) {
                    tiePassword.requestFocus();
                    tiePassword.setError("Password is empty");
                }else if(!tiePassword.getText().toString().equals(tieConfirmPassword.getText().toString())){
                    Snackbar.make(parent_view, "Passwords don't match!", Snackbar.LENGTH_SHORT).show();
                } else{

                   Log.d("NNN", "User ID: " + userId);
                   Log.d("NNN", "Cashier ID: " + cashierId);
                   Log.d("NNN", "Password: " + tiePassword.getText().toString());

                    JSONObject jCredentials = new JSONObject();
                    try {
                        jCredentials.put("userId", userId);
                        jCredentials.put("cashierId", cashierId);
                        jCredentials.put("password", tiePassword.getText().toString());

                        confirmCashiherAccount(jCredentials.toString());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
    }

    private void confirmCashiherAccount(String json) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constant.baseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        final RetrofitInterface retrofitInterface = retrofit.create(RetrofitInterface.class);
        final retrofit2.Call remoteCall = retrofitInterface.confirmCashierAccount(json);
        authenticatePDialog.showProcessingDialog("Confirming Account.. Please wait..");

        authenticatePDialog.setOnCloseDialogListener(new ProcessDialog.CloseDialogClickListener() {
            @Override
            public void onCloseDialogClick() {
                authenticatePDialog.setCancelable(true);
                authenticatePDialog.dismissDialog();
                remoteCall.cancel();

            }
        });
        authenticatePDialog.setOnBackPressedListener(new ProcessDialog.BackPressedListener() {
            @Override
            public void onBackPressedClick() {
                authenticatePDialog.setCancelable(true);
                authenticatePDialog.dismissDialog();
                remoteCall.cancel();
            }
        });

        remoteCall.enqueue(new retrofit2.Callback<ResponseBody>() {
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

                            SharedPreferences sharedpreferences = getSharedPreferences("userPrefs", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            int businessId = messageObj.getInt("business");
                            int cashierId = messageObj.getInt("cashierId");

                            editor.putInt("businessId", businessId);
                            editor.putString("user", user);
                            editor.putInt("userType", userType);
                            editor.putInt("cashierId", cashierId);
                            editor.putBoolean("onboard", false);
                            editor.commit();
                            showSuccessDialog();



                        }

                    } catch (final Exception e) {
                        authenticatePDialog.dismissDialog();

                        showErrorDialog(e);
                        globalErrorDialog.setOnActionListener(new ProcessDialog.ActionClickListener() {
                            @Override
                            public void onActionClick() {
                                globalErrorDialog.dismissDialog();
                                confirmCashiherAccount(json);
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
                            confirmCashiherAccount(json);

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

    private void showSuccessDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_info);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        ((TextView) dialog.findViewById(R.id.title)).setText("Account Activated");
        ((TextView) dialog.findViewById(R.id.content)).setText("You account has been activated successfully");

        ((AppCompatButton) dialog.findViewById(R.id.bContinue)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(ConfirmCashierAccountActivity.this, CashierDashboardActivity.class);
                startActivity(i);
                finish();

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
                    final ProcessDialog errorDialog = new ProcessDialog(ConfirmCashierAccountActivity.this);
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
                    final ProcessDialog errorDialog = new ProcessDialog(ConfirmCashierAccountActivity.this);
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