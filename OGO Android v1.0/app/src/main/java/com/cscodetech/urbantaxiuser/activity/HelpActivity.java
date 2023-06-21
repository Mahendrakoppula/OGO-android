package com.customerogo.app.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.customerogo.app.R;
import com.customerogo.app.model.Help;
import com.customerogo.app.model.Pages;
import com.customerogo.app.model.User;
import com.customerogo.app.retrofit.APIClient;
import com.customerogo.app.retrofit.GetResult;
import com.customerogo.app.utility.CustPrograssbar;
import com.customerogo.app.utility.SessionManager;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;

public class HelpActivity extends BaseActivity implements GetResult.MyListener {

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.img_back)
    ImageView imgBack;

    @BindView(R.id.recycler_product)
    RecyclerView recyclerProduct;
    SessionManager sessionManager;
    CustPrograssbar custPrograssbar;
    User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        ButterKnife.bind(this);
        sessionManager = new SessionManager(this);
        user = sessionManager.getUserDetails();
        custPrograssbar = new CustPrograssbar();

        LinearLayoutManager mLayoutManager2 = new LinearLayoutManager(this);
        mLayoutManager2.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerProduct.setLayoutManager(mLayoutManager2);
        recyclerProduct.setItemAnimator(new DefaultItemAnimator());
        getPagelist();
    }

    private void getPagelist() {
        custPrograssbar.prograssCreate(this);
        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("uid", user.getId());

        } catch (Exception e) {
            e.printStackTrace();
        }
        RequestBody bodyRequest = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
        Call<JsonObject> call = APIClient.getInterface().pagelist(bodyRequest);
        GetResult getResult = new GetResult();
        getResult.setMyListener(this);
        getResult.callForLogin(call, "1");

    }

    @Override
    public void callback(JsonObject result, String callNo) {
        try {
            custPrograssbar.closePrograssBar();
            if (callNo.equalsIgnoreCase("1")) {
                Gson gson = new Gson();
                Help help = gson.fromJson(result.toString(), Help.class);
                if (help.getResult().equalsIgnoreCase("true")) {
                    recyclerProduct.setAdapter(new MyFaqAdepter(help.getPagelist()));
                }

            }
        } catch (Exception e) {
            e.toString();

        }
    }

    public class MyFaqAdepter extends RecyclerView.Adapter<MyFaqAdepter.ViewHolder> {
        private List<Pages> orderData;

        public MyFaqAdepter(List<Pages> orderData) {
            this.orderData = orderData;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                      int viewType) {

            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.halp_item, parent, false);
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder,
                                     int position) {
            Log.e("position", "" + position);
            Pages order = orderData.get(position);
            holder.txtTital.setText("" + order.getTitle());


            holder.lvlClick.setOnClickListener(v -> startActivity(new Intent(HelpActivity.this,HelpDetailsActivity.class).putExtra("title",order.getTitle()).putExtra("desc",order.getDescription())));
        }

        @Override
        public int getItemCount() {
            return orderData.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.txt_tital)
            TextView txtTital;
            @BindView(R.id.lvl_click)
            LinearLayout lvlClick;


            public ViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }
        }
    }

    @OnClick(R.id.img_back)
    public void onClick() {
        finish();
    }
}