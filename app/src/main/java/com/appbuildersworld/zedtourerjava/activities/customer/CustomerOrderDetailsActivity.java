package com.appbuildersworld.zedtourerjava.activities.customer;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.appbuildersworld.zedtourerjava.CashierActivity;
import com.appbuildersworld.zedtourerjava.PaymentSuccessActivity;
import com.appbuildersworld.zedtourerjava.R;
import com.appbuildersworld.zedtourerjava.WalletActivity;
import com.appbuildersworld.zedtourerjava.activities.AddCashierActivity;
import com.appbuildersworld.zedtourerjava.connectivity.Constant;
import com.appbuildersworld.zedtourerjava.interfaces.RetrofitInterface;
import com.appbuildersworld.zedtourerjava.models.MBusinessOrder;
import com.appbuildersworld.zedtourerjava.ui.ProcessDialog;
import com.balysv.materialripple.MaterialRippleLayout;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class CustomerOrderDetailsActivity extends AppCompatActivity {

    private ProcessDialog connectionFailureDialog;
    private ProcessDialog globalErrorDialog;
    private ProcessDialog remoteCallPDialog;
    private ProcessDialog makePaymentPDialog;

    private int clickCount = 0;
    private String response;
    private int orderId;
    private int customerId;
    private int businessId;
    private double orderAmount;
    private MBusinessOrder businessOrder;

    private TextView tvTotalPrice, tvAllItemsTotal,
            tvInvoiceCode, tvInvoiceDate, tvCartItems, tvItemTotal, tvMrStatus;
    private AppCompatButton bMakePayment;
    private MaterialRippleLayout mrStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_invoice);

        makePaymentPDialog = new ProcessDialog(this);
        makePaymentPDialog.setTouchCancel(false);
        makePaymentPDialog.setCancelable(true);

        Intent i = getIntent();
        orderId = i.getIntExtra("orderId", 0);
        customerId = i.getIntExtra("customerId", 0);
        businessId = i.getIntExtra("businessId", 0);

        Log.d("NNN", "Order ID: " + orderId);

        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        tvAllItemsTotal = findViewById(R.id.tvAllItemsTotal);
        tvInvoiceCode = findViewById(R.id.tvInvoiceCode);
        tvInvoiceDate = findViewById(R.id.tvInvoiceDate);
        tvCartItems = findViewById(R.id.tvCartItems);
        tvItemTotal = findViewById(R.id.tvItemTotal);
        tvMrStatus = findViewById(R.id.tvMrStatus);

        mrStatus = findViewById(R.id.mrStatus);

        remoteCallPDialog = new ProcessDialog(this);
        remoteCallPDialog.setTouchCancel(false);
        remoteCallPDialog.setCancelable(true);
        connectionFailureDialog = new ProcessDialog(this);
        globalErrorDialog = new ProcessDialog(this);

        bMakePayment = findViewById(R.id.bMakePayment);
        bMakePayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject orderDetails = new JSONObject();
                try {
                    orderDetails.put("orderId", orderId);
                    orderDetails.put("businessId", businessId);
                    orderDetails.put("customerId", customerId);
                    orderDetails.put("orderAmount", orderAmount);


                    makePayment(orderDetails.toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        getOrder();
    }

    private void getOrder() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constant.baseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        final RetrofitInterface retrofitInterface = retrofit.create(RetrofitInterface.class);
        final retrofit2.Call getIemtCall = retrofitInterface.getOrder(orderId);
        remoteCallPDialog.showProcessingDialog("Getting order.. Please wait..");

        remoteCallPDialog.setOnCloseDialogListener(new ProcessDialog.CloseDialogClickListener() {
            @Override
            public void onCloseDialogClick() {
                remoteCallPDialog.setCancelable(true);
                remoteCallPDialog.dismissDialog();
                getIemtCall.cancel();

            }
        });
        remoteCallPDialog.setOnBackPressedListener(new ProcessDialog.BackPressedListener() {
            @Override
            public void onBackPressedClick() {
                remoteCallPDialog.setCancelable(true);
                remoteCallPDialog.dismissDialog();
                getIemtCall.cancel();
            }
        });

        getIemtCall.enqueue(new retrofit2.Callback<ResponseBody>() {
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
                        JSONObject jsonObject = responseJObject.getJSONObject("message");

                        businessOrder = new MBusinessOrder();
                        businessOrder.setOrderCart(jsonObject.getString("cart"));
                        JSONObject cart = new JSONObject(businessOrder.getOrderCart());
                        businessOrder.setCustomerNames(jsonObject.getString("customerNames"));
                        businessOrder.setCustomerId(jsonObject.getInt("customerId"));
                        businessOrder.setOrderDate(jsonObject.getString("orderDate"));
                        businessOrder.setPaymentDate(jsonObject.getString("paymentDate"));
                        businessOrder.setCashierId(jsonObject.getInt("cashierId"));
                        businessOrder.setBusinessId(jsonObject.getInt("businessId"));
                        businessOrder.setBusinessName(jsonObject.getString("businessName"));
                        businessOrder.setStatus(jsonObject.getString("status"));

                        tvTotalPrice.setText("ZMW " + cart.getString("total"));
                        tvAllItemsTotal.setText("ZMW " + cart.getString("total"));
                        tvInvoiceCode.setText(businessOrder.getInvoiceCode());
                        tvInvoiceDate.setText(businessOrder.getOrderDate());
                        tvCartItems.setText(cart.getString("quantity") + " " + cart.getString("productName") + " @ " + cart.getString("price"));
                        tvItemTotal.setText(cart.getString("total"));

                        orderAmount = Double.parseDouble(cart.getString("total"));

                        if (businessOrder.getStatus().equals("Awaiting Confirmation")) {
                            mrStatus.setVisibility(View.VISIBLE);
                            bMakePayment.setVisibility(View.GONE);
                            tvMrStatus.setText(businessOrder.getStatus());
                        } else if (businessOrder.getStatus().equals("Awaiting Payment")) {
                            mrStatus.setVisibility(View.GONE);
                            bMakePayment.setVisibility(View.VISIBLE);
                        } else if (businessOrder.getStatus().equals("Complete")) {
                            mrStatus.setVisibility(View.VISIBLE);
                            tvMrStatus.setText(businessOrder.getStatus());
                            bMakePayment.setVisibility(View.GONE);

                        }


                    } catch (final Exception e) {
                        remoteCallPDialog.dismissDialog();

                        showErrorDialog(e);
                        globalErrorDialog.setOnActionListener(new ProcessDialog.ActionClickListener() {
                            @Override
                            public void onActionClick() {
                                globalErrorDialog.dismissDialog();
                                getOrder();
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
                            getOrder();
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

    private void makePayment(String json) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constant.baseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        final RetrofitInterface retrofitInterface = retrofit.create(RetrofitInterface.class);
        final retrofit2.Call remoteCall = retrofitInterface.makePayment(json);
        makePaymentPDialog.showProcessingDialog("Processing.. Please wait..");

        makePaymentPDialog.setOnCloseDialogListener(new ProcessDialog.CloseDialogClickListener() {
            @Override
            public void onCloseDialogClick() {
                makePaymentPDialog.setCancelable(true);
                makePaymentPDialog.dismissDialog();
                remoteCall.cancel();

            }
        });
        makePaymentPDialog.setOnBackPressedListener(new ProcessDialog.BackPressedListener() {
            @Override
            public void onBackPressedClick() {
                makePaymentPDialog.setCancelable(true);
                makePaymentPDialog.dismissDialog();
                remoteCall.cancel();
            }
        });

        remoteCall.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(retrofit2.Call<ResponseBody> classCall, final Response<ResponseBody> netResponse) {

                makePaymentPDialog.dismissDialog();

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
                            if (responseJObject.getString("message").equals("INSUFFICIENT BALANCE")) {
                                showFailDialog();
                            } else {

                            }
                        } else {
                            Intent i = new Intent(CustomerOrderDetailsActivity.this, PaymentSuccessActivity.class);
                            i.putExtra("orderId", orderId);
                            startActivity(i);

                        }

                    } catch (final Exception e) {
                        makePaymentPDialog.dismissDialog();

                        showErrorDialog(e);
                        globalErrorDialog.setOnActionListener(new ProcessDialog.ActionClickListener() {
                            @Override
                            public void onActionClick() {
                                globalErrorDialog.dismissDialog();
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
                    makePaymentPDialog.dismissDialog();
                    showConnectionFailureDialog(t);
                    connectionFailureDialog.setOnActionListener(new ProcessDialog.ActionClickListener() {
                        @Override
                        public void onActionClick() {
                            connectionFailureDialog.dismissDialog();

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

    private void showFailDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_warning);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        ((TextView) dialog.findViewById(R.id.title)).setText("Insufficient Balance");
        ((TextView) dialog.findViewById(R.id.content)).setText("Click the button below to recharge");

        ((AppCompatButton) dialog.findViewById(R.id.bt_close)).setText("Recharge");
        ((AppCompatButton) dialog.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent i = new Intent(CustomerOrderDetailsActivity.this, WalletActivity.class);
                startActivity(i);

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
                    final ProcessDialog errorDialog = new ProcessDialog(CustomerOrderDetailsActivity.this);
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
                    final ProcessDialog errorDialog = new ProcessDialog(CustomerOrderDetailsActivity.this);
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