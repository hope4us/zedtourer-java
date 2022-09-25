package com.appbuildersworld.zedtourerjava;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;

import com.appbuildersworld.zedtourerjava.activities.MapsActivity;
import com.appbuildersworld.zedtourerjava.connectivity.Constant;
import com.appbuildersworld.zedtourerjava.interfaces.RetrofitInterface;
import com.appbuildersworld.zedtourerjava.models.MBusiness;
import com.appbuildersworld.zedtourerjava.models.MProductCategory;
import com.appbuildersworld.zedtourerjava.models.MUser;
import com.appbuildersworld.zedtourerjava.ui.ProcessDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class BusinessSignUpActivity extends AppCompatActivity {

    private ProcessDialog connectionFailureDialog;
    private ProcessDialog globalErrorDialog;
    private ProcessDialog getCategoriesPDialog;
    private int clickCount = 0;
    private String response;

    private Button bOpenMap;
    private Button bRegisterBusiness;
    private ChipGroup cgProductCategories;
    private TextInputEditText tieBusinessName;
    private TextInputEditText tieOwnerName;
    private TextInputEditText tiePhone;
    private TextInputEditText tiePassword;



    private JSONArray productSelection;

    private double latitude = -15.390113;
    private double longitude = 28.322519;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_sign_up);

        setupViews();
    }

    public void setupViews() {

        getCategoriesPDialog = new ProcessDialog(this);
        getCategoriesPDialog.setTouchCancel(false);
        getCategoriesPDialog.setCancelable(true);
        connectionFailureDialog = new ProcessDialog(this);
        globalErrorDialog = new ProcessDialog(this);

        bOpenMap = findViewById(R.id.bOpenMap);
        bOpenMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(BusinessSignUpActivity.this, MapsActivity.class);
                startActivity(i);
            }
        });

        bRegisterBusiness = findViewById(R.id.bRegisterBusiness);
        bRegisterBusiness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MBusiness m = new MBusiness();
                m.setBusinessName(tieBusinessName.getText().toString());
                JSONObject coordinates = new JSONObject();
                try {
                    coordinates.put("latitude", latitude);
                    coordinates.put("longitude", longitude);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                m.setLocationCoordinates(coordinates.toString());
                m.setProducts(productSelection.toString());

                JSONObject user = new JSONObject();
                try {
                    user.put("names", tieOwnerName.getText().toString());
                    user.put("phone", tiePhone.getText().toString());
                    user.put("password", tiePassword.getText().toString());
                    user.put("userType", 1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                m.setUser(user.toString());

                Gson gson = new Gson();
                String j = gson.toJson(m);

                Intent i = new Intent(BusinessSignUpActivity.this, VerificationActivity.class);
                i.putExtra("business", (Serializable) m);
                i.putExtra("userType", 1);
                startActivity(i);
            }
        });

        cgProductCategories = findViewById(R.id.cgProductCategories);
        productSelection = new JSONArray();
        getProductCategories();

        tieBusinessName = (TextInputEditText) findViewById(R.id.tieBusinessName);
        tieOwnerName = (TextInputEditText) findViewById(R.id.tieOwnerName);
        tiePhone = (TextInputEditText) findViewById(R.id.tiePhone);
        tiePassword = (TextInputEditText) findViewById(R.id.tiePassword);

    }

    private void getProductCategories() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constant.baseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        final RetrofitInterface retrofitInterface = retrofit.create(RetrofitInterface.class);
        final retrofit2.Call getCategoriesCall = retrofitInterface.getProductCategories();
        getCategoriesPDialog.showProcessingDialog("Retrieving Products and Services.. Please wait..");

        getCategoriesPDialog.setOnCloseDialogListener(new ProcessDialog.CloseDialogClickListener() {
            @Override
            public void onCloseDialogClick() {
                getCategoriesPDialog.setCancelable(true);
                getCategoriesPDialog.dismissDialog();
                getCategoriesCall.cancel();

            }
        });
        getCategoriesPDialog.setOnBackPressedListener(new ProcessDialog.BackPressedListener() {
            @Override
            public void onBackPressedClick() {
                getCategoriesPDialog.setCancelable(true);
                getCategoriesPDialog.dismissDialog();
                getCategoriesCall.cancel();
            }
        });

        getCategoriesCall.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(retrofit2.Call<ResponseBody> classCall, final Response<ResponseBody> netResponse) {

                getCategoriesPDialog.dismissDialog();

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
                        JSONArray jsonArray = responseJObject.getJSONArray("message");

                        Log.d("NNN", "Category List Size: " + jsonArray.length());

                        if (jsonArray.length() > 0) {
                            getCategoriesPDialog.dismissDialog();

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                                MProductCategory m = new MProductCategory();
                                m.setProductCategoryId(jsonObject.getInt("productCategoryId"));
                                m.setProductCategoryName(jsonObject.getString("productCategoryName"));

                                cgProductCategories.addView(addChip(m.getProductCategoryId(), m.getProductCategoryName()));
                            }

                        } else {
                            getCategoriesPDialog.dismissDialog();

                            final ProcessDialog getCategoriesFailureDialog = new ProcessDialog(BusinessSignUpActivity.this);

                            getCategoriesFailureDialog.showResponseDialog("There are no product and services available at this time", "Continue");
                            getCategoriesFailureDialog.setCancelable(true);
                            getCategoriesFailureDialog.setTouchCancel(false);
                            getCategoriesFailureDialog.setOnCloseDialogListener(new ProcessDialog.CloseDialogClickListener() {
                                @Override
                                public void onCloseDialogClick() {
                                    getCategoriesFailureDialog.dismissDialog();
                                    onBackPressed();
                                }
                            });
                            getCategoriesFailureDialog.setOnActionListener(new ProcessDialog.ActionClickListener() {
                                @Override
                                public void onActionClick() {
                                    getCategoriesFailureDialog.dismissDialog();
                                    onBackPressed();

                                }
                            });
                            getCategoriesFailureDialog.setOnBackPressedListener(new ProcessDialog.BackPressedListener() {
                                @Override
                                public void onBackPressedClick() {
                                    getCategoriesFailureDialog.dismissDialog();
                                    onBackPressed();

                                }
                            });

                        }
                    } catch (final Exception e) {
                        getCategoriesPDialog.dismissDialog();

                        showErrorDialog(e);
                        globalErrorDialog.setOnActionListener(new ProcessDialog.ActionClickListener() {
                            @Override
                            public void onActionClick() {
                                globalErrorDialog.dismissDialog();
                                getProductCategories();
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
                    getCategoriesPDialog.dismissDialog();
                    showConnectionFailureDialog(t);
                    connectionFailureDialog.setOnActionListener(new ProcessDialog.ActionClickListener() {
                        @Override
                        public void onActionClick() {
                            connectionFailureDialog.dismissDialog();
                            getProductCategories();
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

    private Chip addChip(int categoryId, String categoryName) {
        Chip chip = new Chip(this, null, R.style.Widget_Material3_Chip_Filter);
        chip.setText(categoryName);
        chip.setChipBackgroundColorResource(R.color.zed_light);
        chip.setTextColor(getResources().getColor(R.color.white));
        // chip.setId(ViewCompat.generateViewId());
        chip.setChipIconResource(R.drawable.ic_baseline_check_24);
        chip.setChipIconTintResource(R.color.white);
        chip.setChipIconVisible(false);
        chip.setCloseIconTintResource(R.color.white);
        chip.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean checked = false;
                int index = 0;
                for (int i = 0; i < productSelection.length(); i++) {
                    try {
                        if (productSelection.getInt(i) == categoryId) {
                            checked = true;
                            index = i;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if (checked) {
                    productSelection.remove(index);
                    chip.setChipIconVisible(false);
                    chip.setCloseIconVisible(false);
                    chip.setChipBackgroundColorResource(R.color.zed_light);

                } else {

                }
                Log.d("NNN", "Selection Size: " + productSelection.length());
            }
        });


        chip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean checked = false;

                for (int i = 0; i < productSelection.length(); i++) {
                    try {
                        if (productSelection.getInt(i) == categoryId) {
                            checked = true;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if (checked) {


                } else {
                    chip.setChipBackgroundColorResource(R.color.green_400);
                    chip.setChipIconVisible(true);
                    chip.setCloseIconVisible(true);
                    productSelection.put(categoryId);
                }
                Log.d("NNN", "Selection Size: " + productSelection.length());

            }
        });
        return chip;
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
                    final ProcessDialog errorDialog = new ProcessDialog(BusinessSignUpActivity.this);
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
                    final ProcessDialog errorDialog = new ProcessDialog(BusinessSignUpActivity.this);
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