package com.appbuildersworld.zedtourerjava.activities.customer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.appbuildersworld.zedtourerjava.R;
import com.appbuildersworld.zedtourerjava.connectivity.Constant;
import com.appbuildersworld.zedtourerjava.interfaces.RetrofitInterface;
import com.appbuildersworld.zedtourerjava.ui.ProcessDialog;
import com.appbuildersworld.zedtourerjava.utils.Tools;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;


public class CartActivity extends AppCompatActivity {

    private ProcessDialog connectionFailureDialog;
    private ProcessDialog globalErrorDialog;
    private ProcessDialog remoteCallPDialog;
    private int clickCount = 0;
    private String response;

    private AppCompatButton bPlaceOrder;
    private TextView tvProductName, tvTotal, tvPrice;
    private ImageView image;

    private String productName;
    private String category;
    private String imageUrl;
    private String description;
    private double price;
    private double total;
    private int productId;
    private int quantity;
    private int businessId;
    private int customerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        initToolbar();

        Intent i = getIntent();
        productName = i.getStringExtra("productName");
        category = i.getStringExtra("category");
        imageUrl = i.getStringExtra("imageUrl");
        description = i.getStringExtra("description");
        price = i.getDoubleExtra("price", 0);
        total = i.getDoubleExtra("amount", 0);
        productId = i.getIntExtra("productId", 0);
        quantity = i.getIntExtra("quantity", 0);
        businessId = i.getIntExtra("businessId", 0);

        remoteCallPDialog = new ProcessDialog(this);
        remoteCallPDialog.setTouchCancel(false);
        remoteCallPDialog.setCancelable(true);

        connectionFailureDialog = new ProcessDialog(this);
        globalErrorDialog = new ProcessDialog(this);

        SharedPreferences sharedpreferences = getSharedPreferences("userPrefs", Context.MODE_PRIVATE);
        customerId = sharedpreferences.getInt("customerId", 0);



        image = findViewById(R.id.image);
        tvProductName = findViewById(R.id.tvProductName);
        tvTotal = findViewById(R.id.tvTotal);
        tvPrice = findViewById(R.id.tvPrice);

        tvProductName.setText(productName);
        tvPrice.setText("" + price);
        tvTotal.setText("" + total);

        Tools.displayImageOriginal(this, image, Constant.baseURL + "/files/images/" + imageUrl);

        bPlaceOrder = findViewById(R.id.bPlaceOrder);
        bPlaceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                JSONObject order = new JSONObject();
                try {
                    order.put("productName", productName);
                    JSONObject cart = new JSONObject();

                    cart.put("price", price);
                    cart.put("total", total);
                    cart.put("quantity", quantity);
                    cart.put("productId", productId);
                    cart.put("category", "Drink (Alcoholic)");
                    order.put("cart", cart.toString());

                    order.put("businessId", businessId);
                    order.put("customerId", customerId);
                    order.put("status", "Awaiting Confirmation");

                    placeOrder(order.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //showOrderSuccessDialog();
            }
        });
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setSystemBarColor(this, R.color.grey_10);
        Tools.setSystemBarLight(this);
    }

    private void placeOrder(String json) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constant.baseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        final RetrofitInterface retrofitInterface = retrofit.create(RetrofitInterface.class);
        final retrofit2.Call remoteCall = retrofitInterface.placeOrder(json);
        remoteCallPDialog.showProcessingDialog("Placing Order.. Please wait..");

        remoteCallPDialog.setOnCloseDialogListener(new ProcessDialog.CloseDialogClickListener() {
            @Override
            public void onCloseDialogClick() {
                remoteCallPDialog.setCancelable(true);
                remoteCallPDialog.dismissDialog();
                remoteCall.cancel();

            }
        });
        remoteCallPDialog.setOnBackPressedListener(new ProcessDialog.BackPressedListener() {
            @Override
            public void onBackPressedClick() {
                remoteCallPDialog.setCancelable(true);
                remoteCallPDialog.dismissDialog();
                remoteCall.cancel();
            }
        });

        remoteCall.enqueue(new retrofit2.Callback<ResponseBody>() {
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
                        boolean error = responseJObject.getBoolean("error");

                        if (error) {

                        } else {
                            int orderId = responseJObject.getInt("message");
                            showOrderSuccessDialog(orderId);
                        }

                    } catch (final Exception e) {
                        remoteCallPDialog.dismissDialog();

                        showErrorDialog(e);
                        globalErrorDialog.setOnActionListener(new ProcessDialog.ActionClickListener() {
                            @Override
                            public void onActionClick() {
                                globalErrorDialog.dismissDialog();
                                placeOrder(json);
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
                            placeOrder(json);
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
                    final ProcessDialog errorDialog = new ProcessDialog(CartActivity.this);
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
                    final ProcessDialog errorDialog = new ProcessDialog(CartActivity.this);
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


    private void showOrderSuccessDialog(int orderId) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_info_order_success);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;


        ((AppCompatButton) dialog.findViewById(R.id.bContinue)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(CartActivity.this, CustomerOrderListActivity.class);
                i.putExtra("orderId", orderId);
                i.putExtra("businessId", businessId);
                startActivity(i);
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_setting, menu);
        Tools.changeMenuIconColor(menu, getResources().getColor(R.color.grey_60));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else {
            Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
}
