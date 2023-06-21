package com.customerogo.app.activity;


import static com.customerogo.app.utility.Utility.isvarification;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.customerogo.app.R;
import com.customerogo.app.model.User;
import com.customerogo.app.utility.CustPrograssbar;
import com.customerogo.app.utility.SessionManager;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class SMSCodeActivity extends BaseActivity {
    @BindView(R.id.txt_mob)
    TextView txtMob;
    @BindView(R.id.ed_otp1)
    EditText edOtp1;


    @BindView(R.id.btn_reenter)
    TextView btnReenter;
    @BindView(R.id.btn_timer)
    TextView btnTimer;
    private String verificationId;
    private FirebaseAuth mAuth;
    String phonenumber;
    String phonecode;
    CustPrograssbar custPrograssbar;
    SessionManager sessionManager;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smscode);
        ButterKnife.bind(this);
        sessionManager = new SessionManager(SMSCodeActivity.this);
        custPrograssbar = new CustPrograssbar();

        user = sessionManager.getUserDetails();

        mAuth = FirebaseAuth.getInstance();

        sendVerificationCode(user.getCcode() + user.getMobile());
        txtMob.setText("We have sent you an SMS on " + user.getCcode() + " " + user.getMobile() + "\n with 6 digit verification code");
        try {
            new CountDownTimer(60000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    long seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished);
                    btnTimer.setText(seconds + " Second Wait");
                }

                @Override
                public void onFinish() {
                    btnReenter.setVisibility(View.VISIBLE);
                    btnTimer.setVisibility(View.GONE);
                }
            }.start();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithCredential(credential);
    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        switch (isvarification) {
                            case 0:
                                Intent intent = new Intent(SMSCodeActivity.this, ChanegPasswordActivity.class);
                                intent.putExtra("phone", user.getMobile());
                                startActivity(intent);
                                finish();
                                break;
                            case 1:
                                startActivity(new Intent(SMSCodeActivity.this, SignUpActivity.class));
                                finish();
                                break;
                            case 2:
                                break;
                            case 3:
                                finish();
                                break;
                            default:
                                break;
                        }
                    } else {
                        Toast.makeText(SMSCodeActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void sendVerificationCode(String number) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallBack
        );
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationId = s;
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                edOtp1.setText("" + code);

                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            User user1 = new User();
            user1.setId("0");
            user.setCcode("codeSelect");
            user.setFname("CS");
            user.setLname("CODETECH");
            user.setEmail("cscodetech@gmail.com");
            user.setMobile("88888888888");
            user.setPassword("0000000");
            user.setRefercode("");
            sessionManager.setUserDetails(user1);
            Toast.makeText(SMSCodeActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();


        }
    };

    @OnClick({R.id.btn_send, R.id.btn_reenter})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_send:
                if (validation()) {
                    verifyCode(edOtp1.getText().toString());
                }

                break;
            case R.id.btn_reenter:
                btnReenter.setVisibility(View.GONE);
                btnTimer.setVisibility(View.VISIBLE);
                try {
                    new CountDownTimer(60000, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            long seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished);
                            btnTimer.setText(seconds + " Second Wait");
                        }

                        @Override
                        public void onFinish() {
                            btnReenter.setVisibility(View.VISIBLE);
                            btnTimer.setVisibility(View.GONE);
                        }
                    }.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                sendVerificationCode(user.getCcode() + user.getMobile());
                break;
            default:
                break;
        }
    }



    public boolean validation() {

        if (edOtp1.getText().toString().isEmpty()) {
            edOtp1.setError("");
            return false;
        }

        return true;
    }
}
