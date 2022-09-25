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

import com.appbuildersworld.zedtourerjava.CashierActivity;
import com.appbuildersworld.zedtourerjava.R;
import com.appbuildersworld.zedtourerjava.connectivity.Constant;
import com.appbuildersworld.zedtourerjava.interfaces.RetrofitInterface;
import com.appbuildersworld.zedtourerjava.models.MCashier;
import com.appbuildersworld.zedtourerjava.ui.ProcessDialog;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class AddCashierActivity extends AppCompatActivity {

    private ProcessDialog connectionFailureDialog;
    private ProcessDialog globalErrorDialog;
    private ProcessDialog registerCashierPDialog;
    private int clickCount = 0;
    private String response;
    private TextInputEditText tieCashierName;
    private TextInputEditText tiePhone;
    private String gender = "Female";
    private int businessId;

    Button bRegisterCashier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_cashier);

        SharedPreferences sharedpreferences = getSharedPreferences("userPrefs", Context.MODE_PRIVATE);
        String productsObj = sharedpreferences.getString("products", "");
        businessId = sharedpreferences.getInt("businessId", 0);

        registerCashierPDialog = new ProcessDialog(this);
        registerCashierPDialog.setTouchCancel(false);
        registerCashierPDialog.setCancelable(true);

        connectionFailureDialog = new ProcessDialog(this);
        globalErrorDialog = new ProcessDialog(this);

        bRegisterCashier = findViewById(R.id.bRegisterCashier);
        bRegisterCashier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tieCashierName = (TextInputEditText) findViewById(R.id.tieCashierName);
                tiePhone = (TextInputEditText) findViewById(R.id.tiePhone);

                MCashier m = new MCashier();
                m.setCashierNames(tieCashierName.getText().toString());
                m.setPhone(tiePhone.getText().toString());
                m.setGender(gender);
                m.setBusinessId(businessId);
                m.setPassword("12345");

                Log.d("NNN", "Cashier Name: " + m.getCashierNames());
                Log.d("NNN", "Phone: " + m.getPhone());
                Log.d("NNN", "Gender: " + m.getGender());

                JSONObject jsonCashier = new JSONObject();
                try {
                    jsonCashier.put("cashierName", m.getCashierNames());
                    jsonCashier.put("phone", m.getPhone());
                    jsonCashier.put("gender", m.getGender());
                    jsonCashier.put("password", m.getPassword());
                    jsonCashier.put("businessId", m.getBusinessId());
                    jsonCashier.put("userType", 3);
                    jsonCashier.put("status", "Inactive");
                    jsonCashier.put("imageUrl", "no_profile_pic.png");


                    registerCashier(jsonCashier.toString());

                } catch (JSONException e) {
                    e.printStackTrace();
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

        ((TextView) dialog.findViewById(R.id.title)).setText("Account Created");
        ((TextView) dialog.findViewById(R.id.content)).setText("An SMS with login instructions has been sent to the number used during account creation. " +
                "\n(Note: Default password in the prototype is '12345')");

        ((AppCompatButton) dialog.findViewById(R.id.bContinue)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(AddCashierActivity.this, CashierActivity.class);
                startActivity(i);
                finish();

                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    private void registerCashier(String json) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constant.baseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        final RetrofitInterface retrofitInterface = retrofit.create(RetrofitInterface.class);
        final retrofit2.Call remoteCall = retrofitInterface.registerCashier(json);
        registerCashierPDialog.showProcessingDialog("Creating account.. Please wait..");

        registerCashierPDialog.setOnCloseDialogListener(new ProcessDialog.CloseDialogClickListener() {
            @Override
            public void onCloseDialogClick() {
                registerCashierPDialog.setCancelable(true);
                registerCashierPDialog.dismissDialog();
                remoteCall.cancel();

            }
        });
        registerCashierPDialog.setOnBackPressedListener(new ProcessDialog.BackPressedListener() {
            @Override
            public void onBackPressedClick() {
                registerCashierPDialog.setCancelable(true);
                registerCashierPDialog.dismissDialog();
                remoteCall.cancel();
            }
        });

        remoteCall.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(retrofit2.Call<ResponseBody> classCall, final Response<ResponseBody> netResponse) {

                registerCashierPDialog.dismissDialog();

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
                            showSuccessDialog();
                        }

                    } catch (final Exception e) {
                        registerCashierPDialog.dismissDialog();

                        showErrorDialog(e);
                        globalErrorDialog.setOnActionListener(new ProcessDialog.ActionClickListener() {
                            @Override
                            public void onActionClick() {
                                globalErrorDialog.dismissDialog();
                                registerCashier(json);
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
                    registerCashierPDialog.dismissDialog();
                    showConnectionFailureDialog(t);
                    connectionFailureDialog.setOnActionListener(new ProcessDialog.ActionClickListener() {
                        @Override
                        public void onActionClick() {
                            connectionFailureDialog.dismissDialog();
                            registerCashier(json);
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
                    final ProcessDialog errorDialog = new ProcessDialog(AddCashierActivity.this);
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
                    final ProcessDialog errorDialog = new ProcessDialog(AddCashierActivity.this);
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