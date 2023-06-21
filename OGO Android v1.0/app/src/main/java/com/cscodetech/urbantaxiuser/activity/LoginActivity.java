package com.customerogo.app.activity;

import static com.customerogo.app.utility.SessionManager.intro;
import static com.customerogo.app.utility.SessionManager.login;
import static com.customerogo.app.utility.SessionManager.wallet;
import static com.customerogo.app.utility.Utility.isvarification;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.customerogo.app.R;
import com.customerogo.app.model.Contry;
import com.customerogo.app.model.CountryCodeItem;
import com.customerogo.app.model.Login;
import com.customerogo.app.model.RestResponse;
import com.customerogo.app.model.User;
import com.customerogo.app.retrofit.APIClient;
import com.customerogo.app.retrofit.GetResult;
import com.customerogo.app.utility.CustPrograssbar;
import com.customerogo.app.utility.SessionManager;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;

public class LoginActivity extends BaseActivity implements GetResult.MyListener {

    @BindView(R.id.spinner)
    public Spinner spinner;
    @BindView(R.id.ed_username)
    public EditText edUsername;
    @BindView(R.id.ed_password)
    public EditText edPassword;
    @BindView(R.id.show_pass_btn)
    public ImageView showPassBtn;
    @BindView(R.id.rlt_password)
    public RelativeLayout rltPassword;
    @BindView(R.id.btn_login)
    public TextView btnLogin;
    @BindView(R.id.btn_signup)
    public TextView btnSignup;
    List<CountryCodeItem> cCodes = new ArrayList<>();
    String codeSelect;
    CustPrograssbar custPrograssbar;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        sessionManager = new SessionManager(this);
        custPrograssbar = new CustPrograssbar();
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                codeSelect = cCodes.get(position).getCcode();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        getCode();

    }

    private void getCode() {
        JSONObject jsonObject = new JSONObject();

        RequestBody bodyRequest = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
        Call<JsonObject> call = APIClient.getInterface().getCodelist(bodyRequest);
        GetResult getResult = new GetResult();
        getResult.setMyListener(this);
        getResult.callForLogin(call, "1");

    }


    private void checkMobile() {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("mobile", edUsername.getText().toString());
            jsonObject.put("ccode", codeSelect);

            RequestBody bodyRequest = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
            Call<JsonObject> call = APIClient.getInterface().mobileCheck(bodyRequest);
            GetResult getResult = new GetResult();
            getResult.setMyListener(this);
            getResult.callForLogin(call, "2");
            custPrograssbar.prograssCreate(LoginActivity.this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void forgotMobile() {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("mobile", edUsername.getText().toString());
            jsonObject.put("ccode", codeSelect);

            RequestBody bodyRequest = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
            Call<JsonObject> call = APIClient.getInterface().mobileCheck(bodyRequest);
            GetResult getResult = new GetResult();
            getResult.setMyListener(this);
            getResult.callForLogin(call, "4");
            custPrograssbar.prograssCreate(LoginActivity.this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void login() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("mobile", edUsername.getText().toString());
            jsonObject.put("ccode", codeSelect);
            jsonObject.put("password", edPassword.getText().toString());

            RequestBody bodyRequest = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
            Call<JsonObject> call = APIClient.getInterface().userLogin(bodyRequest);
            GetResult getResult = new GetResult();
            getResult.setMyListener(this);
            getResult.callForLogin(call, "3");
            custPrograssbar.prograssCreate(LoginActivity.this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
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


    @OnClick({R.id.btn_login, R.id.btn_signup, R.id.txt_forgot})
    public void onBindClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                if (rltPassword.getVisibility() == View.VISIBLE) {
                    login();
                } else {
                    checkMobile();
                }
                break;
            case R.id.btn_signup:
                if (!TextUtils.isEmpty(edUsername.getText().toString())) {
                    checkMobile();
                } else {
                    edUsername.setError("");
                }

                break;
            case R.id.txt_forgot:
                if (!TextUtils.isEmpty(edUsername.getText().toString())) {
                    forgotMobile();
                } else {
                    edUsername.setError("");
                }

                break;
            default:
                throw new IllegalStateException("Unexpected value: " + view.getId());
        }
    }

    @Override
    public void callback(JsonObject result, String callNo) {

        try {
            custPrograssbar.closePrograssBar();
            if (callNo.equalsIgnoreCase("1")) {
                Gson gson = new Gson();
                Contry contry = gson.fromJson(result.toString(), Contry.class);
                cCodes = contry.getCountryCode();
                List<String> arealist = new ArrayList<>();
                for (int i = 0; i < cCodes.size(); i++) {
                    if (cCodes.get(i).getStatus().equalsIgnoreCase("1")) {
                        arealist.add(cCodes.get(i).getCcode());
                    }
                }
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, arealist);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
                spinner.setAdapter(dataAdapter);
            } else if (callNo.equalsIgnoreCase("2")) {
                Gson gson = new Gson();
                RestResponse response = gson.fromJson(result.toString(), RestResponse.class);
                if (response.getResult().equals("true")) {
                    Toast.makeText(this, "Number is not register", Toast.LENGTH_SHORT).show();
                    isvarification = 1;
                    User user = new User();
                    user.setCcode(codeSelect);
                    user.setMobile("" + edUsername.getText().toString());
                    sessionManager.setUserDetails(user);
                    startActivity(new Intent(this, SMSCodeActivity.class));

                } else {

                    rltPassword.setVisibility(View.VISIBLE);

                }
            } else if (callNo.equalsIgnoreCase("3")) {
                Gson gson = new Gson();
                Login loginUser = gson.fromJson(result.toString(), Login.class);
                Toast.makeText(this, loginUser.getResponseMsg(), Toast.LENGTH_LONG).show();
                if (loginUser.getResult().equalsIgnoreCase("true")) {

                    sessionManager.setUserDetails(loginUser.getUserLogin());
                    sessionManager.setFloatData(wallet, Float.parseFloat(loginUser.getUserLogin().getWallet()));
                    sessionManager.setBooleanData(login, true);
                    sessionManager.setBooleanData(intro, true);

                    OneSignal.sendTag("userid", loginUser.getUserLogin().getId());

                    startActivity(new Intent(this, HomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                }
            } else if (callNo.equalsIgnoreCase("4")) {
                Gson gson = new Gson();
                RestResponse response = gson.fromJson(result.toString(), RestResponse.class);
                if (response.getResult().equals("true")) {
                    Toast.makeText(this, "Number is not register", Toast.LENGTH_SHORT).show();


                } else {

                    isvarification = 0;
                    User user = new User();
                    user.setCcode(codeSelect);
                    user.setMobile("" + edUsername.getText().toString());
                    sessionManager.setUserDetails(user);
                    startActivity(new Intent(this, SMSCodeActivity.class));

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}