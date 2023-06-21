package com.customerogo.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.customerogo.app.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingActivity extends AppCompatActivity {

    @BindView(R.id.lvl_profile)
    public LinearLayout lvlProfile;
    @BindView(R.id.lvl_triphistry)
    public LinearLayout lvlTriphistry;
    @BindView(R.id.lvl_notificatio)
    public LinearLayout lvlNotificatio;
    @BindView(R.id.lvl_refer)
    public LinearLayout lvlRefer;
    @BindView(R.id.lvl_ratesus)
    public LinearLayout lvlRatesus;
    @BindView(R.id.lvl_logout)
    public LinearLayout lvlLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);

    }

    @OnClick({R.id.lvl_profile, R.id.lvl_triphistry, R.id.lvl_notificatio, R.id.lvl_refer, R.id.lvl_ratesus, R.id.lvl_logout})

    public void onBindClick(View view) {
        switch (view.getId()) {
            case R.id.lvl_profile:
                startActivity(new Intent(SettingActivity.this, ProfileActivity.class));
                break;
            case R.id.lvl_triphistry:

                startActivity(new Intent(SettingActivity.this, TripHistoryActivity.class));

                break;
            case R.id.lvl_notificatio:
                break;
            case R.id.lvl_refer:
                break;
            case R.id.lvl_ratesus:
                break;
            case R.id.lvl_logout:
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + view.getId());
        }
    }
}