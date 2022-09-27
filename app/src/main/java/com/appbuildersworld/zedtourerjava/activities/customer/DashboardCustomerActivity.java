package com.appbuildersworld.zedtourerjava.activities.customer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.appbuildersworld.zedtourerjava.R;
import com.appbuildersworld.zedtourerjava.WalletActivity;
import com.appbuildersworld.zedtourerjava.activities.BusinessDetailsActivity;
import com.appbuildersworld.zedtourerjava.activities.LoginActivity;
import com.appbuildersworld.zedtourerjava.adapter.AdapterBusinessMap;
import com.appbuildersworld.zedtourerjava.connectivity.Constant;
import com.appbuildersworld.zedtourerjava.interfaces.RetrofitInterface;
import com.appbuildersworld.zedtourerjava.models.MBusiness;
import com.appbuildersworld.zedtourerjava.ui.ProcessDialog;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class DashboardCustomerActivity extends AppCompatActivity {
    private ProcessDialog connectionFailureDialog;
    private ProcessDialog globalErrorDialog;
    private ProcessDialog getItemPDialog;
    private int clickCount = 0;
    private String response;

    private HorizontalScrollView hsvBusiness;

    private DrawerLayout drawer;
    List<MBusiness> businesses;
    private ImageView ivDrawerBtn;
    private TextView tvCustomerNames, tvCustomerPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_customer);
        setUpDrawer();
    }

    private void setUpDrawer() {

        ivDrawerBtn = findViewById(R.id.bt_menu);
        ivDrawerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.openDrawer(GravityCompat.START);
            }
        });
        getItemPDialog = new ProcessDialog(this);
        getItemPDialog.setTouchCancel(false);
        getItemPDialog.setCancelable(true);
        connectionFailureDialog = new ProcessDialog(this);
        globalErrorDialog = new ProcessDialog(this);

        hsvBusiness = (HorizontalScrollView) findViewById(R.id.hsvBusiness);

        NavigationView nav_view = (NavigationView) findViewById(R.id.nav_view_customer);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        View headerView = nav_view.getHeaderView(0);
        tvCustomerNames = (TextView) headerView.findViewById(R.id.tvCustomerNames);
        tvCustomerPhone = (TextView) headerView.findViewById(R.id.tvCustomerPhone);

        SharedPreferences sharedpreferences = getSharedPreferences("userPrefs", Context.MODE_PRIVATE);
        String user = sharedpreferences.getString("user", "");
        try {
            JSONObject userObj = new JSONObject(user);
            tvCustomerNames.setText(userObj.getString("names"));
            tvCustomerPhone.setText(userObj.getString("phone"));
        } catch (JSONException e) {
            e.printStackTrace();
        }


        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(final MenuItem item) {
                //  Toast.makeText(getApplicationContext(), item.getTitle() + " Selected", Toast.LENGTH_SHORT).show();
                // actionBar.setTitle(item.getTitle());
                if (item.getTitle().equals("Wallet")) {
                    Intent i = new Intent(DashboardCustomerActivity.this, WalletActivity.class);
                    startActivity(i);
                }
                if (item.getTitle().equals("Orders")) {
                    Intent i = new Intent(DashboardCustomerActivity.this, CustomerOrderListActivity.class);
                    startActivity(i);
                }
                if (item.getTitle().equals("Logout")) {
                    showLogoutDialog();
                }
                drawer.closeDrawers();
                return true;
            }
        });

        businesses = new ArrayList<>();
        getBusinesses();


    }

    private void showLogoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Logout");
        builder.setPositiveButton("LOGOUT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                SharedPreferences settings = DashboardCustomerActivity.this.getSharedPreferences("userPrefs", Context.MODE_PRIVATE);
                settings.edit().clear().commit();
                Intent intent = new Intent(DashboardCustomerActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("CANCEL", null);
        builder.show();
    }

    private void getBusinesses() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constant.baseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        final RetrofitInterface retrofitInterface = retrofit.create(RetrofitInterface.class);
        final retrofit2.Call remoteCall = retrofitInterface.getBusinesses();
        getItemPDialog.showProcessingDialog("Loading Businesses.. Please wait..");

        getItemPDialog.setOnCloseDialogListener(new ProcessDialog.CloseDialogClickListener() {
            @Override
            public void onCloseDialogClick() {
                getItemPDialog.setCancelable(true);
                getItemPDialog.dismissDialog();
                remoteCall.cancel();

            }
        });
        getItemPDialog.setOnBackPressedListener(new ProcessDialog.BackPressedListener() {
            @Override
            public void onBackPressedClick() {
                getItemPDialog.setCancelable(true);
                getItemPDialog.dismissDialog();
                remoteCall.cancel();
            }
        });

        remoteCall.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(retrofit2.Call<ResponseBody> classCall, final Response<ResponseBody> netResponse) {

                getItemPDialog.dismissDialog();

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
                        JSONArray businessesJSON = responseJObject.getJSONArray("message");

                        Log.d("NNN", " Response Array Size: " + businessesJSON.length());

                        for (int i = 0; i < businessesJSON.length(); i++) {
                            JSONObject jObj = businessesJSON.getJSONObject(i);
                            MBusiness m = new MBusiness();
                            m.setBusinessId(jObj.getInt("businessId"));
                            m.setNatureOfBusiness(jObj.getString("natureOfBusiness"));
                            m.setBusinessName(jObj.getString("businessName"));
                            m.setImageUrl(jObj.getString("imageUrl"));
                            m.setProducts(jObj.getString("products"));

                            businesses.add(m);
                        }
                        if (businesses.size() > 0) {
                            initComponent();
                        } else {

                        }

                    } catch (final Exception e) {
                        getItemPDialog.dismissDialog();

                        showErrorDialog(e);
                        globalErrorDialog.setOnActionListener(new ProcessDialog.ActionClickListener() {
                            @Override
                            public void onActionClick() {
                                globalErrorDialog.dismissDialog();
                                getBusinesses();
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
                    getItemPDialog.dismissDialog();
                    showConnectionFailureDialog(t);
                    connectionFailureDialog.setOnActionListener(new ProcessDialog.ActionClickListener() {
                        @Override
                        public void onActionClick() {
                            connectionFailureDialog.dismissDialog();
                            getBusinesses();
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

    private void initComponent() {

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rvHorizontal);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        List<MBusiness> items = businesses;

        AdapterBusinessMap adapter = new AdapterBusinessMap(items, this);
        LinearLayoutManager horizontalLayout;

        horizontalLayout = new LinearLayoutManager(
                DashboardCustomerActivity.this, LinearLayoutManager.HORIZONTAL,
                false);
        recyclerView.setLayoutManager(horizontalLayout);

        adapter.setOnItemClickListener(new AdapterBusinessMap.OnItemClickListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onItemClick(View view, MBusiness obj, int position) {
                Intent i = new Intent(DashboardCustomerActivity.this, BusinessDetailsActivity.class);
                i.putExtra("business", (Serializable) obj);
                startActivity(i);
            }
        });

        recyclerView.setAdapter(adapter);

    }

    @Override
    public void onBackPressed() {
        if (this.drawer.isDrawerOpen(GravityCompat.START)) {
            this.drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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
                    final ProcessDialog errorDialog = new ProcessDialog(DashboardCustomerActivity.this);
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
                    final ProcessDialog errorDialog = new ProcessDialog(DashboardCustomerActivity.this);
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