package com.customerogo.app.activity;


import static com.customerogo.app.utility.SessionManager.currency;
import static com.customerogo.app.utility.SessionManager.wallet;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.customerogo.app.R;
import com.customerogo.app.model.Payment;
import com.customerogo.app.model.PaymentItem;
import com.customerogo.app.model.RestResponse;
import com.customerogo.app.model.User;
import com.customerogo.app.model.Wallet;
import com.customerogo.app.model.WalletitemItem;
import com.customerogo.app.retrofit.APIClient;
import com.customerogo.app.retrofit.GetResult;
import com.customerogo.app.utility.CustPrograssbar;
import com.customerogo.app.utility.SessionManager;
import com.customerogo.app.utility.Utility;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

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

public class WalletActivity extends BaseActivity implements GetResult.MyListener {


    @BindView(R.id.txt_total)
    TextView txtTotal;
    @BindView(R.id.txt_addmunny)
    TextView txtAddmunny;
    @BindView(R.id.recy_transaction)
    RecyclerView recyTransaction;
    @BindView(R.id.lvl_notfound)
    LinearLayout lvlNotfound;

    SessionManager sessionManager;
    CustPrograssbar custPrograssbar;
    User user;
    public static boolean walletUp = false;
    List<PaymentItem> paymentList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mywallet);
        ButterKnife.bind(this);
        custPrograssbar = new CustPrograssbar();
        sessionManager = new SessionManager(this);
        user = sessionManager.getUserDetails();
        txtTotal.setText("" + sessionManager.getStringData(currency) + sessionManager.getFloatData(wallet));

        recyTransaction.setLayoutManager(new GridLayoutManager(this, 1));
        recyTransaction.setItemAnimator(new DefaultItemAnimator());
        getHistry();

    }

    private void getHistry() {
        custPrograssbar.prograssCreate(WalletActivity.this);
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("uid", user.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        RequestBody bodyRequest = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
        Call<JsonObject> call = APIClient.getInterface().walletReport(bodyRequest);
        GetResult getResult = new GetResult();
        getResult.setMyListener(this);
        getResult.callForLogin(call, "1");

    }
    String walletAmount="";

    public void addmounny(Context context) {


        Activity activity = (Activity) context;
        BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(activity);
        View rootView = activity.getLayoutInflater().inflate(R.layout.addmonny_layout, null);
        mBottomSheetDialog.setContentView(rootView);
        EditText edName = rootView.findViewById(R.id.ed_name);
        TextView txtClick = rootView.findViewById(R.id.btn_create);
        txtClick.setOnClickListener(v -> {

            if (TextUtils.isEmpty(edName.getText().toString())) {
                edName.setError("Enter amount");

            } else {
                mBottomSheetDialog.cancel();
                walletAmount=edName.getText().toString();
                bottonPaymentList();

            }


        });
        mBottomSheetDialog.show();


    }

    private void getPaymentList() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("uid", user.getId());


            RequestBody bodyRequest = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
            Call<JsonObject> call = APIClient.getInterface().paymentlist(bodyRequest);
            GetResult getResult = new GetResult();
            getResult.setMyListener(this);
            getResult.callForLogin(call, "2");
            custPrograssbar.prograssCreate(this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void addAmount() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("uid", user.getId());
            jsonObject.put("wallet", walletAmount);

            RequestBody bodyRequest = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
            Call<JsonObject> call = APIClient.getInterface().walletUpdate(bodyRequest);
            GetResult getResult = new GetResult();
            getResult.setMyListener(this);
            getResult.callForLogin(call, "3");
            custPrograssbar.prograssCreate(this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void bottonPaymentList() {
        BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(this);
        View sheetView = getLayoutInflater().inflate(R.layout.custome_payment, null);
        LinearLayout listView = sheetView.findViewById(R.id.lvl_list);
        LinearLayout lvlWallat = sheetView.findViewById(R.id.lvl_wallat);
        TextView txtTotal = sheetView.findViewById(R.id.txt_total);
        txtTotal.setVisibility(View.GONE);
        lvlWallat.setVisibility(View.GONE);
        for (int i = 0; i < paymentList.size(); i++) {
            LayoutInflater inflater = LayoutInflater.from(this);
            PaymentItem paymentItem = paymentList.get(i);
            View view = inflater.inflate(R.layout.custome_paymentitem, null);
            ImageView imageView = view.findViewById(R.id.img_icon);
            TextView txtTitle = view.findViewById(R.id.txt_title);
            TextView txtSubtitel = view.findViewById(R.id.txt_subtitel);
            txtTitle.setText("" + paymentList.get(i).getmTitle());
            txtSubtitel.setText("" + paymentList.get(i).getSubtitle());
            Glide.with(this).load(APIClient.baseUrl + "/" + paymentList.get(i).getmImg()).thumbnail(Glide.with(this).load(R.drawable.eye)).into(imageView);
            int finalI = i;
            view.setOnClickListener(v -> {
                Utility.paymentId = paymentList.get(finalI).getmId();
                try {
                    switch (paymentList.get(finalI).getmTitle()) {
                        case "Razorpay":
                            int temtoal = Integer.parseInt(walletAmount);
                            mBottomSheetDialog.cancel();
                            startActivity(new Intent(WalletActivity.this, RazerpayActivity.class).putExtra("amount", temtoal).putExtra("detail", paymentItem));
                            break;
                        case "Cash On Delivery":

                            mBottomSheetDialog.cancel();
                            break;
                        case "Paypal":
                            mBottomSheetDialog.cancel();
                            startActivity(new Intent(WalletActivity.this, PaypalActivity.class).putExtra("amount", Double.parseDouble(walletAmount)).putExtra("detail", paymentItem));
                            break;
                        case "Stripe":
                            mBottomSheetDialog.cancel();
                            startActivity(new Intent(WalletActivity.this, StripPaymentActivity.class).putExtra("amount", Double.parseDouble(walletAmount)).putExtra("detail", paymentItem));
                            break;

                        case "FlutterWave":
                            mBottomSheetDialog.cancel();
                            startActivity(new Intent(WalletActivity.this, FlutterwaveActivity.class).putExtra("amount", Double.parseDouble(walletAmount)));
                            break;
                        case "Paytm":
                            mBottomSheetDialog.cancel();
                            startActivity(new Intent(WalletActivity.this, PaytmActivity.class).putExtra("amount", Double.parseDouble(walletAmount)));
                            break;
                        case "SenangPay":
                            mBottomSheetDialog.cancel();
                            startActivity(new Intent(WalletActivity.this, SenangpayActivity.class).putExtra("amount", Double.parseDouble(walletAmount)).putExtra("detail", paymentItem));
                            break;
                        case "PayStack":
                            int temtoal1 = (int) Math.round(Double.parseDouble(walletAmount));
                            mBottomSheetDialog.cancel();
                            startActivity(new Intent(WalletActivity.this, PaystackActivity.class).putExtra("amount", temtoal1).putExtra("detail", paymentItem));
                            break;
                        default:
                            break;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            listView.addView(view);
        }
        mBottomSheetDialog.setContentView(sheetView);
        mBottomSheetDialog.show();
    }



    @OnClick({R.id.img_back, R.id.txt_addmunny})
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.txt_addmunny:
                getPaymentList();
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
                Wallet walletHistry = gson.fromJson(result.toString(), Wallet.class);
                sessionManager.setFloatData(wallet, Integer.parseInt(walletHistry.getWallets()));
                txtTotal.setText("" + sessionManager.getStringData(currency) + sessionManager.getFloatData(wallet));
                if (walletHistry.getResult().equalsIgnoreCase("true")) {
                    if (walletHistry.getWalletitem().isEmpty()) {
                        recyTransaction.setVisibility(View.GONE);
                        lvlNotfound.setVisibility(View.VISIBLE);
                    } else {
                        HistryAdp histryAdp = new HistryAdp(walletHistry.getWalletitem());
                        recyTransaction.setAdapter(histryAdp);
                    }

                } else {
                    recyTransaction.setVisibility(View.GONE);
                    lvlNotfound.setVisibility(View.VISIBLE);
                }
            } else if (callNo.equalsIgnoreCase("2")) {
                Gson gson = new Gson();
                Payment payment = gson.fromJson(result.toString(), Payment.class);
                if (payment.getResult().equalsIgnoreCase("true")) {
                    paymentList = new ArrayList<>();
                    for (int i = 0; i < payment.getPaymentdata().size(); i++) {
                        if (payment.getPaymentdata().get(i).getpShow().equalsIgnoreCase("1")) {
                            paymentList.add(payment.getPaymentdata().get(i));
                        }
                    }
                    addmounny(WalletActivity.this);

                }
            } else if (callNo.equalsIgnoreCase("3")) {
                Gson gson = new Gson();
                RestResponse response = gson.fromJson(result.toString(), RestResponse.class);
                Toast.makeText(this, response.getResponseMsg(), Toast.LENGTH_SHORT).show();
                walletUp = true;
                if (response.getResult().equalsIgnoreCase("true")) {
                    finish();
                }
            }
        } catch (Exception e) {
            e.toString();

        }

    }

    public class HistryAdp extends RecyclerView.Adapter<HistryAdp.MyViewHolder> {
        private List<WalletitemItem> list;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView txtDate;
            public TextView txtMessage;

            public TextView txtAmount;

            public MyViewHolder(View view) {
                super(view);
                txtDate = (TextView) view.findViewById(R.id.txt_date);
                txtMessage = (TextView) view.findViewById(R.id.txt_message);

                txtAmount = (TextView) view.findViewById(R.id.txt_amount);
            }
        }

        public HistryAdp(List<WalletitemItem> list) {
            this.list = list;

        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_histry, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, int position) {

            WalletitemItem category = list.get(position);
            holder.txtDate.setText("" + category.getTdate());
            holder.txtMessage.setText(category.getMessage());

            if (category.getStatus().equalsIgnoreCase("Credit")) {
                holder.txtAmount.setTextColor(getResources().getColor(R.color.green));
                holder.txtAmount.setText("+" + sessionManager.getStringData(currency) + category.getAmt());
            } else {
                holder.txtAmount.setText("-" + sessionManager.getStringData(currency) + category.getAmt());
                holder.txtAmount.setTextColor(getResources().getColor(R.color.red));
            }


        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (walletUp) {
            walletUp = false;
            getHistry();
        }
        if (Utility.paymentsucsses == 1) {
            Utility.paymentsucsses = 0;
            addAmount();


        }
    }
}