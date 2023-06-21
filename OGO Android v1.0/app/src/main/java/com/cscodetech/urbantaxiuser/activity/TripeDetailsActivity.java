package com.customerogo.app.activity;

import static com.customerogo.app.MyApplication.mContext;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.customerogo.app.R;
import com.customerogo.app.model.RestResponse;
import com.customerogo.app.model.RideDetails;
import com.customerogo.app.model.TripeDetails;
import com.customerogo.app.model.User;
import com.customerogo.app.retrofit.APIClient;
import com.customerogo.app.retrofit.GetResult;
import com.customerogo.app.utility.CustPrograssbar;
import com.customerogo.app.utility.SessionManager;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
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

public class TripeDetailsActivity extends BaseActivity implements GetResult.MyListener, OnMapReadyCallback {

    @BindView(R.id.txt_date)
    public TextView txtDate;
    @BindView(R.id.txt_orders)
    public TextView txtOrders;

    @BindView(R.id.lvl_rating)
    public LinearLayout lvlrating;

    @BindView(R.id.img_rider)
    public ImageView imgRider;
    @BindView(R.id.img_carimeg)
    public ImageView imgCarimeg;
    @BindView(R.id.txt_cartype)
    public TextView txtCartype;
    @BindView(R.id.txt_total)
    public TextView txtTotal;
    @BindView(R.id.txt_pickuptime)
    public TextView txtPickuptime;
    @BindView(R.id.txt_droptime)
    public TextView txtDroptime;
    @BindView(R.id.txt_pickup)
    public TextView txtPickup;
    @BindView(R.id.txt_drop)
    public TextView txtDrop;
    @BindView(R.id.txt_ttotal)
    public TextView txtTtotal;
    @BindView(R.id.txt_wallet)
    public TextView txtWallet;
    @BindView(R.id.txt_copun)
    public TextView txtCopun;
    @BindView(R.id.txt_totalpay)
    public TextView txtTotalpay;
    @BindView(R.id.ptype)
    public TextView ptype;
    @BindView(R.id.txt_ptypeamount)
    public TextView txtPtypeamount;
    @BindView(R.id.img_back)
    public ImageView imgBack;
    @BindView(R.id.btn_review)
    public TextView btnReview;
    @BindView(R.id.txt_rating)
    public RatingBar txtRating;

    CustPrograssbar custPrograssbar;
    String oid;
    SessionManager sessionManager;
    SupportMapFragment mapFragment;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tripe_details);
        ButterKnife.bind(this);
        custPrograssbar = new CustPrograssbar();
        sessionManager = new SessionManager(this);
        user = sessionManager.getUserDetails();
        oid = getIntent().getStringExtra("order_id");

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);


        getOrderDetails();
    }

    private void getOrderDetails() {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("orderid", oid);


            RequestBody bodyRequest = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
            Call<JsonObject> call = APIClient.getInterface().tripeDetails(bodyRequest);
            GetResult getResult = new GetResult();
            getResult.setMyListener(this);
            getResult.callForLogin(call, "1");
            custPrograssbar.prograssCreate(this);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    RideDetails details;

    @Override
    public void callback(JsonObject result, String callNo) {
        try {
            custPrograssbar.closePrograssBar();
            if (callNo.equalsIgnoreCase("1")) {
                Gson gson = new Gson();
                TripeDetails tripeDetails = gson.fromJson(result.toString(), TripeDetails.class);

                if (tripeDetails.getResult().equalsIgnoreCase("true")) {
                    details = tripeDetails.getRideDetails();

                    if (details.getOStatus().equalsIgnoreCase("Completed") && details.getIsRate() == 0) {

                        lvlrating.setVisibility(View.GONE);
                        btnReview.setVisibility(View.VISIBLE);

                    } else if (details.getIsRate() == 1) {
                        lvlrating.setVisibility(View.VISIBLE);
                        Glide.with(mContext).load(APIClient.baseUrl + "/" + details.getRiderImg()).thumbnail(Glide.with(mContext).load(R.drawable.cars)).into(imgRider);

                        txtRating.setRating(Float.parseFloat(details.getRiderRate()));
                    } else {
                        lvlrating.setVisibility(View.GONE);
                        btnReview.setVisibility(View.GONE);

                    }

                    txtCartype.setText("" + details.getCarType() + ". " + details.getOrderid());
                    txtOrders.setText("" + details.getOrderid());
                    Glide.with(mContext).load(APIClient.baseUrl + "/" + details.getCarImg()).thumbnail(Glide.with(mContext).load(R.drawable.cars)).into(imgCarimeg);
                    txtDate.setText("" + details.getOrderDate());
                    txtTotal.setText("" + details.getOrderDate());
                    txtPickuptime.setText("" + details.getPickTime());
                    txtPickup.setText("" + details.getPickAddress());
                    txtDroptime.setText("" + details.getDropTime());
                    txtDrop.setText("" + details.getDropAddress());
                    txtTtotal.setText(sessionManager.getStringData(SessionManager.currency) + details.getTotalAmt());
                    txtWallet.setText(sessionManager.getStringData(SessionManager.currency) + details.getWallAmt());
                    txtCopun.setText(sessionManager.getStringData(SessionManager.currency) + details.getCouAmt());
                    txtTotalpay.setText(sessionManager.getStringData(SessionManager.currency) + details.getTotalAmt());
                    txtPtypeamount.setText(sessionManager.getStringData(SessionManager.currency) + details.getTotalAmt());

                    ptype.setText("" + details.getPMethodName());
                    mapFragment.getMapAsync(this);

                }
            } else if (callNo.equalsIgnoreCase("3")) {
                custPrograssbar.closePrograssBar();
                Gson gson = new Gson();
                RestResponse response = gson.fromJson(result.toString(), RestResponse.class);
                Toast.makeText(this, response.getResponseMsg(), Toast.LENGTH_SHORT).show();
                finish();


            }
        } catch (Exception e) {
e.printStackTrace();
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (details != null) {

            googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(details.getPickLat(), details.getPickLong()))
                    .title("Pick")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pickup_map_pin)));

            googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(details.getDropLat(), details.getDropLong()))
                    .title("Drop")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_drop_map_pin)));


            CameraUpdate center =
                    CameraUpdateFactory.newLatLng(new LatLng(details.getDropLat(), details.getDropLong()));
            CameraUpdate zoom = CameraUpdateFactory.newLatLngZoom(new LatLng(details.getPickLat(), details.getPickLong()), 15);

            googleMap.moveCamera(center);
            googleMap.animateCamera(zoom);
            googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style));

        }


    }

    @OnClick({R.id.img_back, R.id.btn_review})

    public void onBindClick(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.btn_review:
                bottonRatingLocation();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + view.getId());
        }
    }

    public void bottonRatingLocation() {


        BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(this);
        View sheetView = getLayoutInflater().inflate(R.layout.custome_rating, null);
        ImageView imgProfile = sheetView.findViewById(R.id.img_profile);
        TextView txtRidername = sheetView.findViewById(R.id.txt_ridername);
        TextView btnConti = sheetView.findViewById(R.id.btn_conti);
        EditText edRview = sheetView.findViewById(R.id.ed_rview);


        RatingBar rb = sheetView.findViewById(R.id.rating);


        txtRidername.setText("" + details.getRiderName());
        Glide.with(this).load(APIClient.baseUrl + "/" + details.getRiderImg()).thumbnail(Glide.with(this).load(R.drawable.user)).into(imgProfile);


        btnConti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendRating(edRview.getText().toString(), String.valueOf(rb.getRating()));
            }
        });
        mBottomSheetDialog.setContentView(sheetView);
        mBottomSheetDialog.show();
    }

    private void sendRating(String rating, String star) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("orderid", oid);
            jsonObject.put("uid", user.getId());
            jsonObject.put("rider_rate", star);
            jsonObject.put("rider_text", rating);


            RequestBody bodyRequest = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
            Call<JsonObject> call = APIClient.getInterface().rateUpdate(bodyRequest);
            GetResult getResult = new GetResult();
            getResult.setMyListener(this);
            getResult.callForLogin(call, "3");
            custPrograssbar.prograssCreate(TripeDetailsActivity.this);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}