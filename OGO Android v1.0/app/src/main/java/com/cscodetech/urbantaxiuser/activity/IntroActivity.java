package com.customerogo.app.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.customerogo.app.R;
import com.customerogo.app.fregment.Info1Fragment;
import com.customerogo.app.fregment.Info2Fragment;
import com.customerogo.app.fregment.Info3Fragment;
import com.customerogo.app.model.User;
import com.customerogo.app.utility.AutoScrollViewPager;
import com.customerogo.app.utility.SessionManager;
import com.customerogo.app.utility.Utility;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class IntroActivity extends BaseActivity {

    @BindView(R.id.flexibleIndicator)
    DotsIndicator flexibleIndicator;
    int selectPage = 0;
    SessionManager sessionManager;
    public static AutoScrollViewPager vpPager;
    MyPagerAdapter adapterViewPager;
    public static TextView btnNext;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        ButterKnife.bind(this);
        btnNext = findViewById(R.id.btn_next);
        vpPager = findViewById(R.id.vpPager);
        sessionManager = new SessionManager(IntroActivity.this);
        requestPermissions(new String[]{Manifest.permission.CALL_PHONE, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 101);
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            Log.e("Set","set permission");

        }

        if(!Utility.hasGPSDevice(this)){
            Utility.enableLoc(this);

        }
        adapterViewPager = new MyPagerAdapter(getSupportFragmentManager());
        if (sessionManager.getBooleanData(SessionManager.login) && sessionManager.getBooleanData(SessionManager.intro)) {
            startActivity(new Intent(IntroActivity.this, HomeActivity.class));
            finish();
        }
        vpPager.setAdapter(adapterViewPager);
        vpPager.startAutoScroll();
        vpPager.setInterval(2000);
        vpPager.setCycle(true);
        vpPager.setStopScrollWhenTouch(true);
        DotsIndicator extensiblePageIndicator = (DotsIndicator) findViewById(R.id.flexibleIndicator);
        extensiblePageIndicator.setViewPager(vpPager);

        vpPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.e("data", "jsadlj");
            }

            @Override
            public void onPageSelected(int position) {
                selectPage = position;

                if (position == 0 || position == 1) {

                    btnNext.setText(getResources().getString(R.string.continues));
                } else if (position == 2) {

                    btnNext.setText(getResources().getString(R.string.getstarted));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                Log.e("sjlkj", "sjahdal");
            }
        });


    }

    @OnClick({R.id.btn_next})
    public void onClick(View view) {
        if (view.getId() == R.id.btn_next) {
            if (selectPage == 0) {
                vpPager.setCurrentItem(1);
            } else if (selectPage == 1) {
                vpPager.setCurrentItem(2);
            } else if (selectPage == 2) {
                User user = new User();
                user.setId("0");
                user.setFname("Test");
                user.setEmail("test@gmail.com");
                user.setMobile("1020304050");

                sessionManager.setUserDetails( user);
                sessionManager.setBooleanData(SessionManager.intro, true);
                startActivity(new Intent(IntroActivity.this, LoginActivity.class));
                finish();
            }
        }
    }

    public class MyPagerAdapter extends FragmentPagerAdapter {
        private int numItems = 3;

        public MyPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public int getCount() {
            return numItems;
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    return Info1Fragment.newInstance();
                case 1:
                    return Info2Fragment.newInstance();
                case 2:
                    return Info3Fragment.newInstance();
                default:
                    return null;
            }

        }

        @Override
        public CharSequence getPageTitle(int position) {
            Log.e("page", "" + position);
            return "Page " + position;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            return fragment;
        }

    }

}
