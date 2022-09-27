package com.appbuildersworld.zedtourerjava.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.appbuildersworld.zedtourerjava.activities.customer.CartActivity;
import com.appbuildersworld.zedtourerjava.R;
import com.appbuildersworld.zedtourerjava.connectivity.Constant;
import com.appbuildersworld.zedtourerjava.fragment.FragmentDrinkAlcGrid;
import com.appbuildersworld.zedtourerjava.fragment.FragmentDrinkGrid;
import com.appbuildersworld.zedtourerjava.fragment.FragmentFoodGrid;
import com.appbuildersworld.zedtourerjava.interfaces.RetrofitInterface;
import com.appbuildersworld.zedtourerjava.models.MProductDrinkAlc;
import com.appbuildersworld.zedtourerjava.models.MProductDrinkNonAlc;
import com.appbuildersworld.zedtourerjava.models.MProductFood;
import com.appbuildersworld.zedtourerjava.ui.ProcessDialog;
import com.appbuildersworld.zedtourerjava.utils.Tools;
import com.google.android.material.tabs.TabLayout;

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


public class ProductsAdminActivity extends AppCompatActivity {

    private ProcessDialog connectionFailureDialog;
    private ProcessDialog globalErrorDialog;
    private ProcessDialog getBusinessProductsPDialog;
    private int clickCount = 0;
    private String response;
    public View parent_view;

    private ViewPager view_pager;
    private TabLayout tab_layout;
    private String productsJSON;
    private int businessId;
    private JSONArray productCategories;
    private JSONArray productsBusiness;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products_admin);
        parent_view = findViewById(R.id.parent_view);

        setupViews();

        SharedPreferences sharedpreferences = getSharedPreferences("userPrefs", Context.MODE_PRIVATE);
        String productsObj = sharedpreferences.getString("products", "");
        businessId = sharedpreferences.getInt("businessId", 0);
        Log.d("NNN", "Business ID: " + businessId);
        try {
            JSONArray products = new JSONArray(productsObj);
            if (products.length() > 0) {
                getBusinessProducts(productsObj);
            } else {

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        initToolbar();

    }

    private void setupViews() {
        getBusinessProductsPDialog = new ProcessDialog(this);
        getBusinessProductsPDialog.setTouchCancel(false);
        getBusinessProductsPDialog.setCancelable(true);
        connectionFailureDialog = new ProcessDialog(this);
        globalErrorDialog = new ProcessDialog(this);
    }

    private void getBusinessProducts(String products) {
        Log.d("NNN", "Getting business products");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constant.baseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        final RetrofitInterface retrofitInterface = retrofit.create(RetrofitInterface.class);
        final retrofit2.Call remoteCall = retrofitInterface.getBusinessProducts(products, businessId);
        getBusinessProductsPDialog.showProcessingDialog("Retrieving Your Products and Services.. Please wait..");

        getBusinessProductsPDialog.setOnCloseDialogListener(new ProcessDialog.CloseDialogClickListener() {
            @Override
            public void onCloseDialogClick() {
                getBusinessProductsPDialog.setCancelable(true);
                getBusinessProductsPDialog.dismissDialog();
                remoteCall.cancel();

            }
        });
        getBusinessProductsPDialog.setOnBackPressedListener(new ProcessDialog.BackPressedListener() {
            @Override
            public void onBackPressedClick() {
                getBusinessProductsPDialog.setCancelable(true);
                getBusinessProductsPDialog.dismissDialog();
                remoteCall.cancel();
            }
        });

        remoteCall.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(retrofit2.Call<ResponseBody> classCall, final Response<ResponseBody> netResponse) {

                getBusinessProductsPDialog.dismissDialog();

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
                        JSONObject message = responseJObject.getJSONObject("message");

                        productCategories = message.getJSONArray("businessProductCategories");
                        productsBusiness = message.getJSONArray("products");

                        if (productCategories.length() > 0) {
                            getBusinessProductsPDialog.dismissDialog();


                            initComponent();


                        } else {
                            getBusinessProductsPDialog.dismissDialog();

                            final ProcessDialog failureDialog = new ProcessDialog(ProductsAdminActivity.this);

                            failureDialog.showResponseDialog("You do not have any products and services at this time", "Continue");
                            failureDialog.setCancelable(true);
                            failureDialog.setTouchCancel(false);
                            failureDialog.setOnCloseDialogListener(new ProcessDialog.CloseDialogClickListener() {
                                @Override
                                public void onCloseDialogClick() {
                                    failureDialog.dismissDialog();
                                    onBackPressed();
                                }
                            });
                            failureDialog.setOnActionListener(new ProcessDialog.ActionClickListener() {
                                @Override
                                public void onActionClick() {
                                    failureDialog.dismissDialog();
                                    onBackPressed();

                                }
                            });
                            failureDialog.setOnBackPressedListener(new ProcessDialog.BackPressedListener() {
                                @Override
                                public void onBackPressedClick() {
                                    failureDialog.dismissDialog();
                                    onBackPressed();

                                }
                            });

                        }
                    } catch (final Exception e) {
                        getBusinessProductsPDialog.dismissDialog();

                        showErrorDialog(e);
                        globalErrorDialog.setOnActionListener(new ProcessDialog.ActionClickListener() {
                            @Override
                            public void onActionClick() {
                                globalErrorDialog.dismissDialog();
                                getBusinessProducts(products);
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
                    getBusinessProductsPDialog.dismissDialog();
                    showConnectionFailureDialog(t);
                    connectionFailureDialog.setOnActionListener(new ProcessDialog.ActionClickListener() {
                        @Override
                        public void onActionClick() {
                            connectionFailureDialog.dismissDialog();
                            getBusinessProducts(products);
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

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Products & Services");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setSystemBarColor(this);
    }

    private void initComponent() {
        view_pager = (ViewPager) findViewById(R.id.view_pager);
        setupViewPager(view_pager);

        tab_layout = (TabLayout) findViewById(R.id.tab_layout);
        tab_layout.setupWithViewPager(view_pager);
    }

    private void setupViewPager(ViewPager viewPager) {
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());

        try {
            List<MProductFood> foodList = new ArrayList<>();
            List<MProductDrinkNonAlc> nonAlcList = new ArrayList<>();
            List<MProductDrinkAlc> alcList = new ArrayList<>();

            for (int i = 0; i < productCategories.length(); i++) {
                String currentCategoryName = productCategories.getJSONObject(i).getString("categoryName");
                int currentCategoryId = productCategories.getJSONObject(i).getInt("categoryId");

                for (int j = 0; j < productsBusiness.length(); j++) {

                    int currentCategoryProductsId = productsBusiness.getJSONObject(j).getInt("productCategory");

                    if (currentCategoryName.equals("Food")) {
                        //Log.d("NNN", "Current Category: " + productCategories.getJSONObject(i).getString("categoryName"));
                        /// Log.d("NNN", "Current Product: " + productsBusiness.getJSONObject(i).getString("foodName"));
                        if (currentCategoryId != currentCategoryProductsId)
                            continue;
                        MProductFood m = new MProductFood();
                        m.setProductId(productsBusiness.getJSONObject(j).getInt("productId"));
                        m.setDescription(productsBusiness.getJSONObject(j).getString("description"));
                        // m.setFoodName(productsBusiness.getJSONObject(j).getString("foodName"));
                        m.setProductCategory(productsBusiness.getJSONObject(j).getInt("productCategory"));
                        m.setPrice(productsBusiness.getJSONObject(j).getInt("price"));
                        m.setImageUrl(productsBusiness.getJSONObject(j).getString("imageUrl"));
                        foodList.add(m);
                    }

                    if (currentCategoryName.equals("Drink (Non-Alcoholic)")) {
                        if (currentCategoryId != currentCategoryProductsId)
                            continue;
                        MProductDrinkNonAlc m = new MProductDrinkNonAlc();
                        m.setProductId(productsBusiness.getJSONObject(j).getInt("productId"));
                        m.setDescription(productsBusiness.getJSONObject(j).getString("description"));
                        m.setProductName(productsBusiness.getJSONObject(j).getString("productName"));
                        m.setProductCategoryId(productsBusiness.getJSONObject(j).getInt("productCategory"));
                        m.setPrice(productsBusiness.getJSONObject(j).getInt("price"));
                        m.setImageUrl(productsBusiness.getJSONObject(j).getString("imageUrl"));
                        nonAlcList.add(m);
                    }

                    if (currentCategoryName.equals("Drink (Alcoholic)")) {
                        if (currentCategoryId != currentCategoryProductsId)
                            continue;
                        MProductDrinkAlc m = new MProductDrinkAlc();
                        m.setProductId(productsBusiness.getJSONObject(j).getInt("productId"));
                        m.setDescription(productsBusiness.getJSONObject(j).getString("description"));
                        m.setProductName(productsBusiness.getJSONObject(j).getString("productName"));
                        m.setProductCategoryId(productsBusiness.getJSONObject(j).getInt("productCategory"));
                        m.setPrice(productsBusiness.getJSONObject(j).getInt("price"));
                        m.setImageUrl(productsBusiness.getJSONObject(j).getString("imageUrl"));
                        alcList.add(m);
                    }
                }

                if (currentCategoryName.equals("Food")) {
                    Log.d("NNN", "Adding food");
                    adapter.addFragment(FragmentFoodGrid.newInstance(foodList), productCategories.getJSONObject(i).getString("categoryName"));
                }
                if (currentCategoryName.equals("Drink (Non-Alcoholic)")) {
                    Log.d("NNN", "Adding non alcohol drinks");
                    adapter.addFragment(FragmentDrinkGrid.newInstance(nonAlcList), productCategories.getJSONObject(i).getString("categoryName"));
                }
                if (currentCategoryName.equals("Drink (Alcoholic)")) {
                    Log.d("NNN", "Adding alcohol drinks");
                    adapter.addFragment(FragmentDrinkAlcGrid.newInstance(alcList), productCategories.getJSONObject(i).getString("categoryName"));
                }
            }

            Log.d("NNN", "Food list size: " + foodList.size());
            Log.d("NNN", "Drink list size: " + nonAlcList.size());
            Log.d("NNN", "Drink alc list size: " + alcList.size());


        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("NNN", "JSON Error: " + e.getLocalizedMessage());
        }

       /* adapter.addFragment(FragmentProductGrid.newInstance(), "MEN");
        adapter.addFragment(FragmentProductGrid.newInstance(), "YOUNG BOYS");
        adapter.addFragment(FragmentProductGrid.newInstance(), "YOUNG GIRLS");
        adapter.addFragment(FragmentProductGrid.newInstance(), "ALL TIME");*/
        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else {
            Intent i = new Intent(ProductsAdminActivity.this, CartActivity.class);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public SectionsPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {

            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
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
                    final ProcessDialog errorDialog = new ProcessDialog(ProductsAdminActivity.this);
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
                    final ProcessDialog errorDialog = new ProcessDialog(ProductsAdminActivity.this);
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
