package com.appbuildersworld.zedtourerjava.activities.customer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.appbuildersworld.zedtourerjava.R;
import com.appbuildersworld.zedtourerjava.adapter.AdapterCustomerOrderList;
import com.appbuildersworld.zedtourerjava.adapter.CustomerListAdapter;
import com.appbuildersworld.zedtourerjava.connectivity.Constant;
import com.appbuildersworld.zedtourerjava.interfaces.RetrofitInterface;
import com.appbuildersworld.zedtourerjava.models.MBusinessOrder;
import com.appbuildersworld.zedtourerjava.ui.ProcessDialog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class CustomerOrderListActivity extends AppCompatActivity {

    private ProcessDialog connectionFailureDialog;
    private ProcessDialog globalErrorDialog;
    private ProcessDialog getItemPDialog;
    private int clickCount = 0;
    private String response;
    private RecyclerView recyclerView;
    private AdapterCustomerOrderList mAdapter;

    List<MBusinessOrder> orders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_order_list);

        getItemPDialog = new ProcessDialog(this);
        getItemPDialog.setTouchCancel(false);
        getItemPDialog.setCancelable(true);
        connectionFailureDialog = new ProcessDialog(this);
        globalErrorDialog = new ProcessDialog(this);

        SharedPreferences sharedpreferences = getSharedPreferences("userPrefs", Context.MODE_PRIVATE);
        int customerId = sharedpreferences.getInt("customerId", 0);

        orders = new ArrayList<>();
        getOrdersByCustomer(customerId);

    }

    private void getOrdersByCustomer(int customerId) {
        Log.d("NNN", "Retrieving orders");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constant.baseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        final RetrofitInterface retrofitInterface = retrofit.create(RetrofitInterface.class);
        final retrofit2.Call remoteCall = retrofitInterface.getCustomerOrders(customerId);
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
                                getOrdersByCustomer(customerId);
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
                            getOrdersByCustomer(customerId);
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
                Intent i = new Intent(CustomerOrderListActivity.this, CustomerOrderDetailsActivity.class);
                i.putExtra("orderId", obj.getOrderId());
                i.putExtra("businessId", obj.getBusinessId());
                i.putExtra("customerId", obj.getCustomerId());
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
                    final ProcessDialog errorDialog = new ProcessDialog(CustomerOrderListActivity.this);
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
                    final ProcessDialog errorDialog = new ProcessDialog(CustomerOrderListActivity.this);
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