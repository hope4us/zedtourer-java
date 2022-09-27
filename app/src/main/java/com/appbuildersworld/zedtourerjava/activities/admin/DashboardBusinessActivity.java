package com.appbuildersworld.zedtourerjava.activities.admin;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.appbuildersworld.zedtourerjava.CashierActivity;
import com.appbuildersworld.zedtourerjava.CustomerListActivity;
import com.appbuildersworld.zedtourerjava.R;
import com.appbuildersworld.zedtourerjava.SalesActivity;
import com.appbuildersworld.zedtourerjava.activities.LoginActivity;
import com.appbuildersworld.zedtourerjava.activities.ProductsAdminActivity;
import com.appbuildersworld.zedtourerjava.connectivity.Constant;
import com.appbuildersworld.zedtourerjava.interfaces.RetrofitInterface;
import com.appbuildersworld.zedtourerjava.models.MBusinessAccount;
import com.appbuildersworld.zedtourerjava.models.MProductCategory;
import com.appbuildersworld.zedtourerjava.ui.ProcessDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class DashboardBusinessActivity extends AppCompatActivity {
    private ProcessDialog connectionFailureDialog;
    private ProcessDialog globalErrorDialog;
    private ProcessDialog remoteCallPDialog;
    private int clickCount = 0;
    private String response;

    private FloatingActionButton fabSales;
    private FloatingActionButton fabCustomers;
    private FloatingActionButton fabProducts;
    private FloatingActionButton fabCashiers;
    private FloatingActionButton fabBusinessProfile;
    private FloatingActionButton fabLogout;
    private TextView tvBalance;

    private int businessId;
    private MBusinessAccount businessAccount;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_business);
        SharedPreferences sharedpreferences = getSharedPreferences("userPrefs", Context.MODE_PRIVATE);
        String user = sharedpreferences.getString("user", "");
        String businessName = sharedpreferences.getString("businessName", "");
        businessId = sharedpreferences.getInt("businessId", 0);

        JSONObject userObj;
        String names = "";
        try {
            userObj = new JSONObject(user);
            names = userObj.getString("names");
        } catch (JSONException e) {
            e.printStackTrace();
        }


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_baseline_house_24);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(names);
        getSupportActionBar().setSubtitle(businessName);

        remoteCallPDialog = new ProcessDialog(this);
        remoteCallPDialog.setTouchCancel(false);
        remoteCallPDialog.setCancelable(true);
        connectionFailureDialog = new ProcessDialog(this);
        globalErrorDialog = new ProcessDialog(this);

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

        getBusinessAccountBalance();



    }

    private void getBusinessAccountBalance() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constant.baseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        final RetrofitInterface retrofitInterface = retrofit.create(RetrofitInterface.class);
        final retrofit2.Call getCategoriesCall = retrofitInterface.getBusinessAccount(businessId);
        remoteCallPDialog.showProcessingDialog("Getting account.. Please wait..");

        remoteCallPDialog.setOnCloseDialogListener(new ProcessDialog.CloseDialogClickListener() {
            @Override
            public void onCloseDialogClick() {
                remoteCallPDialog.setCancelable(true);
                remoteCallPDialog.dismissDialog();
                getCategoriesCall.cancel();

            }
        });
        remoteCallPDialog.setOnBackPressedListener(new ProcessDialog.BackPressedListener() {
            @Override
            public void onBackPressedClick() {
                remoteCallPDialog.setCancelable(true);
                remoteCallPDialog.dismissDialog();
                getCategoriesCall.cancel();
            }
        });

        getCategoriesCall.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(retrofit2.Call<ResponseBody> classCall, final Response<ResponseBody> netResponse) {

                remoteCallPDialog.dismissDialog();

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
                        JSONObject jsonObject= responseJObject.getJSONObject("message");

                        businessAccount = new MBusinessAccount();
                        businessAccount.setAccountId(jsonObject.getInt("accountId"));
                        businessAccount.setBusinessId(jsonObject.getInt("businessId"));
                        businessAccount.setAccountBalance(jsonObject.getInt("accountBalance"));

                        tvBalance = findViewById(R.id.tvBalance);
                        tvBalance.setText("ZMW " + businessAccount.getAccountBalance());

                    } catch (final Exception e) {
                        remoteCallPDialog.dismissDialog();

                        showErrorDialog(e);
                        globalErrorDialog.setOnActionListener(new ProcessDialog.ActionClickListener() {
                            @Override
                            public void onActionClick() {
                                globalErrorDialog.dismissDialog();
                                getBusinessAccountBalance();
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
                    remoteCallPDialog.dismissDialog();
                    showConnectionFailureDialog(t);
                    connectionFailureDialog.setOnActionListener(new ProcessDialog.ActionClickListener() {
                        @Override
                        public void onActionClick() {
                            connectionFailureDialog.dismissDialog();
                            getBusinessAccountBalance();
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
                    final ProcessDialog errorDialog = new ProcessDialog(DashboardBusinessActivity.this);
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
                    final ProcessDialog errorDialog = new ProcessDialog(DashboardBusinessActivity.this);
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