package com.customerogo.app.activity;

import static com.customerogo.app.utility.SessionManager.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
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

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;

public class SignUpActivity extends BaseActivity implements GetResult.MyListener {

    @BindView(R.id.ed_firstname)
    public EditText edFirstname;
    @BindView(R.id.ed_lastname)
    public EditText edLastname;
    @BindView(R.id.ed_eamil)
    public EditText edEamil;
    @BindView(R.id.ed_phone)
    public EditText edPhone;
    @BindView(R.id.ed_password)
    public EditText edPassword;
    @BindView(R.id.show_pass_btn)
    public ImageView showPassBtn;
    @BindView(R.id.ed_referal)
    public EditText edReferal;

    @BindView(R.id.btn_create)
    public TextView btnCreate;
    @BindView(R.id.btn_login)
    public TextView btnLogin;

    @BindView(R.id.spinner)
    public EditText ccode;

    SessionManager sessionManager;
    CustPrograssbar custPrograssbar;
    User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);
        custPrograssbar = new CustPrograssbar();
        sessionManager = new SessionManager(this);
        user=sessionManager.getUserDetails();

        edPhone.setText(""+user.getMobile());
        ccode.setText(""+user.getCcode());



    }

    private void createUser() {

        custPrograssbar.prograssCreate(this);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("fname", edFirstname.getText().toString());
            jsonObject.put("lname", edLastname.getText().toString());
            jsonObject.put("ccode", user.getCcode());
            jsonObject.put("email", edEamil.getText().toString());
            jsonObject.put("mobile", user.getMobile());
            jsonObject.put("password", edPassword.getText().toString());
            jsonObject.put("refercode", edReferal.getText().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        RequestBody bodyRequest = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
        Call<JsonObject> call = APIClient.getInterface().createUser(bodyRequest);
        GetResult getResult = new GetResult();
        getResult.setMyListener(this);
        getResult.callForLogin(call, "2");

    }


    @Override
    public void callback(JsonObject result, String callNo) {
        try {
            custPrograssbar.closePrograssBar();
            if (callNo.equalsIgnoreCase("2")) {
                Gson gson = new Gson();

                Login loginUser = gson.fromJson(result.toString(), Login.class);
                Toast.makeText(this, loginUser.getResponseMsg(), Toast.LENGTH_LONG).show();
                if (loginUser.getResult().equalsIgnoreCase("true")) {
                    sessionManager.setBooleanData(login, true);
                    sessionManager.setUserDetails( loginUser.getUserLogin());
                    startActivity(new Intent(this, HomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @OnClick({R.id.btn_create, R.id.btn_login, R.id.txt_teram})
    public void onBindClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_create) {
            if (validationCreate()) {
                createUser();

            }


        } else if (id == R.id.btn_login) {
            finish();
        } else if (id == R.id.txt_teram) {
            startActivity(new Intent(this, HelpActivity.class));
        }
    }


    public boolean validationCreate() {
        if (TextUtils.isEmpty(edFirstname.getText().toString())) {
            edFirstname.setError("Enter Name");
            return false;
        }
        if (TextUtils.isEmpty(edLastname.getText().toString())) {
            edLastname.setError("Enter Mobile");
            return false;
        }

        if (TextUtils.isEmpty(edEamil.getText().toString())) {
            edEamil.setError("Enter Password");
            return false;
        }
        if (TextUtils.isEmpty(edPassword.getText().toString())) {
            edPassword.setError("Enter Password");
            return false;
        }

        return true;
    }

    public void ShowHidePass(View view) {

        if (view.getId() == R.id.show_pass_btn) {

            if (edPassword.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())) {
                ((ImageView) (view)).setImageResource(R.drawable.hidden);

                //Show Password
                edPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                ((ImageView) (view)).setImageResource(R.drawable.eye);

                //Hide Password
                edPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());

            }
        }


    }


}