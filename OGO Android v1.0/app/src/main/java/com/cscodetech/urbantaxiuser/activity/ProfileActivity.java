package com.customerogo.app.activity;

import static com.customerogo.app.utility.SessionManager.intro;
import static com.customerogo.app.utility.SessionManager.login;
import static com.customerogo.app.utility.SessionManager.wallet;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.customerogo.app.R;
import com.customerogo.app.model.Login;
import com.customerogo.app.model.User;
import com.customerogo.app.retrofit.APIClient;
import com.customerogo.app.retrofit.GetResult;
import com.customerogo.app.utility.CustPrograssbar;
import com.customerogo.app.utility.SessionManager;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;

public class ProfileActivity extends BaseActivity implements GetResult.MyListener {

    @BindView(R.id.img_back)
    public ImageView imgBack;

    @BindView(R.id.ed_firstname)
    public EditText edFirstname;
    @BindView(R.id.ed_lastname)
    public EditText edLastname;
    @BindView(R.id.ed_eamil)
    public EditText edEamil;
    @BindView(R.id.ed_password)
    public EditText edPassword;
    @BindView(R.id.ed_phone)
    public TextView edPhone;
    @BindView(R.id.btn_update)
    public TextView btnUpdate;
    SessionManager sessionManager;
    User user;
    CustPrograssbar custPrograssbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
        sessionManager = new SessionManager(this);
        custPrograssbar = new CustPrograssbar();
        user = sessionManager.getUserDetails();

        edFirstname.setText("" + user.getFname());
        edLastname.setText("" + user.getLname());
        edEamil.setText("" + user.getEmail());
        edPassword.setText("" + user.getPassword());
        edPhone.setText("" + user.getCcode() + user.getMobile());

    }

    public boolean validation() {
        if (edFirstname.getText().toString().isEmpty()) {
            edFirstname.setError("Enter First Name");
            return false;
        }
        if (edLastname.getText().toString().isEmpty()) {
            edLastname.setError("Enter Last Name");
            return false;
        }
        if (edEamil.getText().toString().isEmpty()) {
            edEamil.setError("Enter Email");
            return false;
        }
        return true;
    }

    private void updateUser() {
        custPrograssbar.prograssCreate(ProfileActivity.this);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("uid", user.getId());
            jsonObject.put("fname", edFirstname.getText().toString());
            jsonObject.put("lname", edLastname.getText().toString());
            jsonObject.put("password", edPassword.getText().toString());
            RequestBody bodyRequest = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
            Call<JsonObject> call = APIClient.getInterface().getUpdate(bodyRequest);
            GetResult getResult = new GetResult();
            getResult.setMyListener(this);
            getResult.callForLogin(call, "1");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @OnClick({R.id.img_back, R.id.btn_update})
    public void onBindClick(View view) {
        int id = view.getId();
        if (id == R.id.img_back) {
            finish();
        }
        if (id == R.id.btn_update && validation()) {
            updateUser();
        }
    }

    @Override
    public void callback(JsonObject result, String callNo) {
        try {
            if (callNo.equalsIgnoreCase("1")) {
                Gson gson = new Gson();
                Login loginUser = gson.fromJson(result.toString(), Login.class);
                Toast.makeText(this, loginUser.getResponseMsg(), Toast.LENGTH_LONG).show();
                if (loginUser.getResult().equalsIgnoreCase("true")) {

                    sessionManager.setUserDetails(loginUser.getUserLogin());
                    sessionManager.setFloatData(wallet, Float.parseFloat(loginUser.getUserLogin().getWallet()));
                    sessionManager.setBooleanData(login, true);
                    sessionManager.setBooleanData(intro, true);

                    startActivity(new Intent(this, HomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                }
            }
        } catch (Exception e) {
                e.printStackTrace();
        }
    }
}