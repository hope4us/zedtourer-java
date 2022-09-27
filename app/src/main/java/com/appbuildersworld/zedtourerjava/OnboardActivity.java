package com.appbuildersworld.zedtourerjava;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appbuildersworld.zedtourerjava.activities.LoginActivity;
import com.appbuildersworld.zedtourerjava.activities.admin.DashboardBusinessActivity;
import com.appbuildersworld.zedtourerjava.activities.cashier.CashierOrderListActivity;
import com.appbuildersworld.zedtourerjava.activities.customer.DashboardCustomerActivity;

import org.json.JSONException;
import org.json.JSONObject;


public class OnboardActivity extends AppCompatActivity {

    private static final int MAX_STEP = 4;

    private ViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    private Button btn_got_it;
    private String title_array[] = {
            "Wanna chill?",
            "You like the place?",
            "Like a drink or snack?",
            "Welcome to Zed Tourer!"
    };
    private String description_array[] = {
            "Check out for places near you",
            "Get directions there",
            "Pay for it here",
            "You No.1 app for local touring",
    };
    private int about_images_array[] = {
            R.drawable.ic_baseline_local_bar_24,
            R.drawable.ic_baseline_gps_fixed_24,
            R.drawable.ic_baseline_account_balance_wallet_24,
            R.drawable.zed_tourer_logo_transparent
    };
    private int color_array[] = {
            R.color.zed_main,
            R.color.zed_main,
            R.color.zed_main,
            R.color.zed_main
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboard);

        SharedPreferences sharedpreferences = getSharedPreferences("userPrefs", Context.MODE_PRIVATE);
        if (sharedpreferences == null) {
            Intent i = new Intent(OnboardActivity.this, LoginActivity.class);
            startActivity(i);
            finish();
        } else {
            String user = sharedpreferences.getString("user", "");
            if (user.equals("")) {
                Intent i = new Intent(OnboardActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            } else {
                JSONObject userObj = null;
                try {
                    userObj = new JSONObject(user);
                    int userType = userObj.getInt("userType");
                    Intent i;
                    switch (userType) {

                        case 1:
                             i = new Intent(OnboardActivity.this, DashboardBusinessActivity.class);
                            startActivity(i);
                            finish();
                            break;

                        case 2:
                             i = new Intent(OnboardActivity.this, DashboardCustomerActivity.class);
                            startActivity(i);
                            finish();
                            break;

                        case 3:
                            i = new Intent(OnboardActivity.this, CashierOrderListActivity.class);
                            startActivity(i);
                            finish();
                            break;

                        default:
                            i = new Intent(OnboardActivity.this, LoginActivity.class);
                            startActivity(i);
                            finish();
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }


        initComponent();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    private void initComponent() {
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        btn_got_it = (Button) findViewById(R.id.btn_got_it);

        // adding bottom dots
        bottomProgressDots(0);

        myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        btn_got_it.setVisibility(View.GONE);
        btn_got_it.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(OnboardActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        });

        ((Button) findViewById(R.id.btn_skip)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(OnboardActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    private void bottomProgressDots(int current_index) {
        LinearLayout dotsLayout = (LinearLayout) findViewById(R.id.layoutDots);
        ImageView[] dots = new ImageView[MAX_STEP];

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new ImageView(this);
            int width_height = 15;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(width_height, width_height));
            params.setMargins(10, 10, 10, 10);
            dots[i].setLayoutParams(params);
            dots[i].setImageResource(R.drawable.shape_circle);
            dots[i].setColorFilter(getResources().getColor(R.color.overlay_dark_30), PorterDuff.Mode.SRC_IN);
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0) {
            dots[current_index].setImageResource(R.drawable.shape_circle);
            dots[current_index].setColorFilter(getResources().getColor(R.color.grey_10), PorterDuff.Mode.SRC_IN);
        }
    }

    //  viewpager change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(final int position) {
            bottomProgressDots(position);
            if (position == title_array.length - 1) {
                btn_got_it.setVisibility(View.VISIBLE);
            } else {
                btn_got_it.setVisibility(View.GONE);
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    /**
     * View pager adapter
     */
    public class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;

        public MyViewPagerAdapter() {
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutInflater.inflate(R.layout.item_card_onboard, container, false);
            ((TextView) view.findViewById(R.id.title)).setText(title_array[position]);
            ((TextView) view.findViewById(R.id.subtitle)).setText(description_array[position]);
            ((ImageView) view.findViewById(R.id.image)).setImageResource(about_images_array[position]);
            ((RelativeLayout) view.findViewById(R.id.lyt_parent)).setBackgroundColor(getResources().getColor(color_array[position]));
            container.addView(view);

            return view;
        }

        @Override
        public int getCount() {
            return title_array.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }
}