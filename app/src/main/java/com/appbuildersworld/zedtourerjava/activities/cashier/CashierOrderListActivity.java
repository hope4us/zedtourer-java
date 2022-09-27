package com.appbuildersworld.zedtourerjava.activities.cashier;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.appbuildersworld.zedtourerjava.R;
import com.appbuildersworld.zedtourerjava.WalletActivity;
import com.appbuildersworld.zedtourerjava.activities.LoginActivity;
import com.appbuildersworld.zedtourerjava.activities.customer.CustomerOrderListActivity;
import com.appbuildersworld.zedtourerjava.activities.customer.DashboardCustomerActivity;
import com.appbuildersworld.zedtourerjava.adapter.AdapterCustomerOrderList;
import com.appbuildersworld.zedtourerjava.connectivity.Constant;
import com.appbuildersworld.zedtourerjava.interfaces.RetrofitInterface;
import com.appbuildersworld.zedtourerjava.models.MBusinessOrder;
import com.appbuildersworld.zedtourerjava.ui.ProcessDialog;
import com.appbuildersworld.zedtourerjava.utils.Tools;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class CashierOrderListActivity extends AppCompatActivity {

    private ProcessDialog connectionFailureDialog;
    private ProcessDialog globalErrorDialog;
    private ProcessDialog getItemPDialog;
    private int clickCount = 0;
    private String response;
    private RecyclerView recyclerView;
    private AdapterCustomerOrderList mAdapter;
    private int orderId;

    private RelativeLayout rlNoItem;

    private TextView tvBusinessName, tvDate, tvAmount;
    private CardView cvBill;
    private DrawerLayout drawer;
    private ImageView ivDrawerBtn;
    private TextView tvCustomerNames, tvCustomerPhone;


    List<MBusinessOrder> orders;
    private int businessId;
    private int cashierId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cashier_order_list);

        NavigationView nav_view = (NavigationView) findViewById(R.id.nav_view_cashier);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout_cashier);
        View headerView = nav_view.getHeaderView(0);
        tvCustomerNames = (TextView) headerView.findViewById(R.id.tvCustomerNames);
        tvCustomerPhone = (TextView) headerView.findViewById(R.id.tvCustomerPhone);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.openDrawer(GravityCompat.START);
            }
        });
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Orders");
        Tools.setSystemBarColor(this);

        getItemPDialog = new ProcessDialog(this);
        getItemPDialog.setTouchCancel(false);
        getItemPDialog.setCancelable(true);
        connectionFailureDialog = new ProcessDialog(this);
        globalErrorDialog = new ProcessDialog(this);

        SharedPreferences sharedpreferences = getSharedPreferences("userPrefs", Context.MODE_PRIVATE);
        businessId = sharedpreferences.getInt("businessId", 0);
        cashierId = sharedpreferences.getInt("cashierId", 0);

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
                if (item.getTitle().equals("All Orders")) {
                    Intent i = new Intent(CashierOrderListActivity.this, CashierOrderListActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                    finish();
                }
                if (item.getTitle().equals("My Orders")) {
                      Toast.makeText(getApplicationContext(), "Implements all orders accepted by the active cashier", Toast.LENGTH_SHORT).show();
                }
                if (item.getTitle().equals("Logout")) {
                    showLogoutDialog();
                }
                drawer.closeDrawers();
                return true;
            }
        });


        Log.d("NNN", "Business ID: " + businessId);
        Log.d("NNN", "Cashier ID: " + cashierId);

        orders = new ArrayList<>();
        getOrdersByBusiness(businessId);

    }

    private void showLogoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Logout");
        builder.setPositiveButton("LOGOUT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                SharedPreferences settings = CashierOrderListActivity.this.getSharedPreferences("userPrefs", Context.MODE_PRIVATE);
                settings.edit().clear().commit();
                Intent intent = new Intent(CashierOrderListActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("CANCEL", null);
        builder.show();
    }

    private void getOrdersByBusiness(int businessId) {
        Log.d("NNN", "Retrieving orders");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constant.baseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        final RetrofitInterface retrofitInterface = retrofit.create(RetrofitInterface.class);
        final retrofit2.Call remoteCall = retrofitInterface.getBusinessOrders(businessId);
        getItemPDialog.showProcessingDialog("Getting orders.. Please wait..");

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

                        JSONArray ordersJSON = responseJObject.getJSONArray("message");
                        for (int i = 0; i < ordersJSON.length(); i++) {
                            JSONObject jsonObject = ordersJSON.getJSONObject(i);
                            MBusinessOrder businessOrder = new MBusinessOrder();
                            businessOrder.setOrderId(jsonObject.getInt("orderId"));
                            businessOrder.setOrderCart(jsonObject.getString("cart"));
                            businessOrder.setCustomerNames(jsonObject.getString("customerNames"));
                            businessOrder.setCustomerId(jsonObject.getInt("customerId"));
                            businessOrder.setOrderDate(jsonObject.getString("orderDate"));
                            businessOrder.setPaymentDate(jsonObject.getString("paymentDate"));
                            businessOrder.setCashierId(jsonObject.getInt("cashierId"));
                            businessOrder.setBusinessId(jsonObject.getInt("businessId"));
                            businessOrder.setInvoiceCode(jsonObject.getString("invoiceCode"));
                            businessOrder.setBusinessName(jsonObject.getString("businessName"));
                            businessOrder.setStatus(jsonObject.getString("status"));


                            orders.add(businessOrder);
                        }

                        if (orders.size() > 0) {
                            getItemPDialog.dismissDialog();
                            initComponent();

                        } else {
                            getItemPDialog.dismissDialog();

                        }
                    } catch (final Exception e) {
                        getItemPDialog.dismissDialog();

                        showErrorDialog(e);
                        globalErrorDialog.setOnActionListener(new ProcessDialog.ActionClickListener() {
                            @Override
                            public void onActionClick() {
                                globalErrorDialog.dismissDialog();
                                getOrdersByBusiness(businessId);
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
                            getOrdersByBusiness(businessId);
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
        recyclerView = (RecyclerView) findViewById(R.id.rvCustomerOrderList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        List<MBusinessOrder> items = orders;

        //set data and list adapter
        mAdapter = new AdapterCustomerOrderList(this, items);
        recyclerView.setAdapter(mAdapter);

        // on item list clicked
        mAdapter.setOnItemClickListener(new AdapterCustomerOrderList.OnItemClickListener() {
            @Override
            public void onItemClick(View view, MBusinessOrder obj, int position) {
                Intent i = new Intent(CashierOrderListActivity.this, CashierOrderDetailsActivity.class);
                i.putExtra("orderId", obj.getOrderId());
                i.putExtra("cashierId", cashierId);
                startActivity(i);
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
                    final ProcessDialog errorDialog = new ProcessDialog(CashierOrderListActivity.this);
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
                    final ProcessDialog errorDialog = new ProcessDialog(CashierOrderListActivity.this);
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