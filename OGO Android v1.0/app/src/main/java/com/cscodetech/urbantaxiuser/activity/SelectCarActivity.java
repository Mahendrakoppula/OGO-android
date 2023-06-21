package com.customerogo.app.activity;

import static com.customerogo.app.utility.SessionManager.coupon;
import static com.customerogo.app.utility.SessionManager.couponid;
import static com.customerogo.app.utility.SessionManager.currency;
import static com.customerogo.app.utility.SessionManager.wallet;
import static com.customerogo.app.utility.Utility.distance;
import static com.customerogo.app.utility.Utility.drawTextToBitmap;
import static com.customerogo.app.utility.Utility.getUrl;
import static com.customerogo.app.utility.Utility.isMarkerOutsideCircle;
import static com.customerogo.app.utility.Utility.paymentId;
import static com.customerogo.app.utility.Utility.paymentsucsses;
import static com.customerogo.app.utility.Utility.tragectionID;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.customerogo.app.R;
import com.customerogo.app.adepter.WheelerAdapter;
import com.customerogo.app.map.FetchURL;
import com.customerogo.app.map.TaskLoadedCallback;
import com.customerogo.app.model.Address;
import com.customerogo.app.model.NearCar;
import com.customerogo.app.model.PaymentdataItem;
import com.customerogo.app.model.RestResponse;
import com.customerogo.app.model.User;
import com.customerogo.app.model.Vehicle;
import com.customerogo.app.model.VehicleDataItem;
import com.customerogo.app.retrofit.APIClient;
import com.customerogo.app.retrofit.GetResult;
import com.customerogo.app.utility.CustPrograssbar;
import com.customerogo.app.utility.SessionManager;
import com.customerogo.app.utility.Utility;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;

public class SelectCarActivity extends BaseActivity implements OnMapReadyCallback, TaskLoadedCallback, WheelerAdapter.RecyclerTouchListener, GetResult.MyListener {


    @BindView(R.id.txt_ptitel)
    public TextView txtPtitel;
    @BindView(R.id.txt_paddress)
    public TextView txtPaddress;
    @BindView(R.id.txt_dtitel)
    public TextView txtDtitel;
    @BindView(R.id.txt_daddress)
    public TextView txtDaddress;
    @BindView(R.id.recyviewwheel)
    public RecyclerView recyviewwheel;
    @BindView(R.id.btn_order)
    public TextView btnOrder;
    @BindView(R.id.btn_ordernot)
    public TextView btnOrderNot;
    @BindView(R.id.txt_applycode)
    public TextView txtApplycode;
    @BindView(R.id.animation_coupon)
    public ImageView animationCoupon;
    SessionManager sessionManager;
    Address pickAdddress;
    Address dropAdddress;
    private GoogleMap map;

    private FirebaseFirestore db;
    private ArrayList<NearCar> coursesArrayList;
    User user;
    private Polyline currentPolyline;

    List<PaymentdataItem> paymentList = new ArrayList<>();
    double tWallet = 0;
    double itmeprice = 0;
    double totalprice = 0;
    double time = 0;
    double dist = 0;
    CustPrograssbar custPrograssbar;

    VehicleDataItem wheelitem = null;
    List<Marker> markerslist = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_car);
        ButterKnife.bind(this);
        custPrograssbar = new CustPrograssbar();
        db = FirebaseFirestore.getInstance();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        sessionManager = new SessionManager(this);
        user = sessionManager.getUserDetails();

        LinearLayoutManager mLayoutManager2 = new LinearLayoutManager(this);
        mLayoutManager2.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyviewwheel.setLayoutManager(mLayoutManager2);
        recyviewwheel.setItemAnimator(new DefaultItemAnimator());

        pickAdddress = sessionManager.getAddresspi();
        dropAdddress = sessionManager.getAddressDrop();
        txtPtitel.setText("" + pickAdddress.getTitle());
        txtPaddress.setText("" + pickAdddress.getAddres());
        txtDtitel.setText("" + dropAdddress.getTitle());
        txtDaddress.setText("" + dropAdddress.getAddres());
        txtApplycode.setText(getResources().getString(R.string.apply_coupon));
        sessionManager.setIntData(coupon, 0);
        getVehicle();
        dist = dist + distance(pickAdddress.getLats(), pickAdddress.getLongs(), dropAdddress.getLats(), dropAdddress.getLongs());

    }


    private void placeOrder() {
        StringBuilder riderlist = new StringBuilder(100);
        for (int i = 0; i < coursesArrayList.size(); i++) {
            if (i == 0) {
                riderlist.append(coursesArrayList.get(i).getId());

            } else {
                riderlist.append("," + coursesArrayList.get(i).getId());

            }
            Log.e("TEMP", "-->" + coursesArrayList.get(i).getId());
            Log.e("riderlist", "-->" + riderlist);
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("uid", user.getId());
            jsonObject.put("pick_address", pickAdddress.getTitle() + "," + pickAdddress.getAddres());
            jsonObject.put("drop_address", dropAdddress.getTitle() + "," + dropAdddress.getAddres());
            jsonObject.put("pick_lat", pickAdddress.getLats());
            jsonObject.put("pick_long", pickAdddress.getLongs());
            jsonObject.put("drop_lat", dropAdddress.getLats());
            jsonObject.put("drop_long", dropAdddress.getLongs());
            jsonObject.put("noti_rider_list", riderlist);
            jsonObject.put("total_amt", totalprice);
            jsonObject.put("cou_amt", sessionManager.getIntData(coupon));
            jsonObject.put("wall_amt", tWallet);
            jsonObject.put("car_type", coursesArrayList.get(0).getType());
            jsonObject.put("car_img", coursesArrayList.get(0).getCarimage());
            jsonObject.put("cou_id", sessionManager.getIntData(couponid));
            jsonObject.put("transaction_id", tragectionID);
            jsonObject.put("p_method_id", paymentId);

            RequestBody bodyRequest = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
            Call<JsonObject> call = APIClient.getInterface().getOrderNow(bodyRequest);
            GetResult getResult = new GetResult();
            getResult.setMyListener(this);
            getResult.callForLogin(call, "2");
            custPrograssbar.prograssCreate(SelectCarActivity.this);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void getVehicle() {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("uid", user.getId());

            RequestBody bodyRequest = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
            Call<JsonObject> call = APIClient.getInterface().vehicleList(bodyRequest);
            GetResult getResult = new GetResult();
            getResult.setMyListener(this);
            getResult.callForLogin(call, "1");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void callback(JsonObject result, String callNo) {
        try {
            if (callNo.equalsIgnoreCase("1")) {
                Gson gson = new Gson();
                Vehicle vehicle = gson.fromJson(result.toString(), Vehicle.class);
                recyviewwheel.setAdapter(new WheelerAdapter(this, vehicle.getVehicleData(), this, dist));
                paymentList = vehicle.getPaymentdata();
                sessionManager.setFloatData(wallet, Float.parseFloat(vehicle.getWallet()));


            } else if (callNo.equalsIgnoreCase("2")) {
                Gson gson = new Gson();

                RestResponse response = gson.fromJson(result.toString(), RestResponse.class);
                if (response.getResult().equals("true")) {
                    Toast.makeText(this, response.getResponseMsg(), Toast.LENGTH_SHORT).show();
                    sessionManager.setFloatData(wallet, Float.parseFloat(response.getWallet()));
                    sessionManager.setAddresspic(new Address());
                    sessionManager.setAddressDrop(new Address());
                    startActivity(new Intent(SelectCarActivity.this, TrackTripActivity.class).putExtra("order_id", response.getOrderId()));
                    finish();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @OnClick({R.id.btn_order, R.id.lvl_applycode, R.id.img_back})
    public void onBindClick(View view) {
        switch (view.getId()) {
            case R.id.btn_order:
                if (wheelitem != null) {
                    bottonPaymentList();
                } else {
                    Toast.makeText(SelectCarActivity.this, getResources().getString(R.string.chosecar), Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.lvl_applycode:

                if (sessionManager.getIntData(coupon) != 0) {
                    txtApplycode.setText("Apply Coupon");
                    sessionManager.setIntData(coupon, 0);
                    animationCoupon.setImageResource(R.drawable.ic_arrow_gray);
                } else {
                    int temp = (int) Math.round(itmeprice);
                    startActivity(new Intent(SelectCarActivity.this, CoupunActivity.class).putExtra("amount", temp));
                }

                break;
            case R.id.img_back:
                finish();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + view.getId());
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        List<LatLng> latLngs = new ArrayList<>();
        latLngs.add(new LatLng(pickAdddress.getLats(), pickAdddress.getLongs()));
        latLngs.add(new LatLng(dropAdddress.getLats(), dropAdddress.getLongs()));

        Bitmap bitmap = drawTextToBitmap(SelectCarActivity.this, R.drawable.ic_pickup_map_pin, "");
        Bitmap bitmap1 = drawTextToBitmap(SelectCarActivity.this, R.drawable.ic_drop_map_pin, "");

        googleMap.addMarker(new MarkerOptions()
                .position(latLngs.get(0))
                .title("Pick")
                .icon(BitmapDescriptorFactory.fromBitmap(bitmap)));

        googleMap.addMarker(new MarkerOptions()
                .position(latLngs.get(1))
                .title("Drop")
                .icon(BitmapDescriptorFactory.fromBitmap(bitmap1)));


        CameraUpdate center =
                CameraUpdateFactory.newLatLng(latLngs.get(1));
        CameraUpdate zoom = CameraUpdateFactory.newLatLngZoom(latLngs.get(0), 15);

        map.moveCamera(center);
        map.animateCamera(zoom);
        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style));

        carSetMap(pickAdddress.getLats(), pickAdddress.getLongs());

        LatLng place1 = new LatLng(pickAdddress.getLats(), pickAdddress.getLongs());
        LatLng place2 = new LatLng(dropAdddress.getLats(), dropAdddress.getLongs());

        new FetchURL(SelectCarActivity.this).execute(getUrl(place1, place2, "driving"), "driving");

    }


    @Override
    public void onClickWheelerInfo(VehicleDataItem item, int position) {
        wheelitem = item;
        sessionManager.setIntData(coupon, 0);
        if (dist <= item.getUkms()) {
            itmeprice = item.getUprice();
        } else {
            double km = dist - item.getUkms();
            itmeprice = item.getUprice() + (km * item.getAprice());
        }
        time = dist * item.getTimetaken();

        for (int i = 0; i < markerslist.size(); i++) {
            markerslist.get(i).remove();
        }
        markerslist = new ArrayList<>();
        for (int i = 0; i < coursesArrayList.size(); i++) {
            NearCar nearCar = coursesArrayList.get(i);

            if (item.getTitle().equalsIgnoreCase(nearCar.getType())) {

                final Bitmap[] bmp = new Bitmap[1];
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        URL url;
                        try {
                            url = new URL(APIClient.baseUrl + nearCar.getCarimage());
                            bmp[0] = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                            bmp[0] = Bitmap.createScaledBitmap(bmp[0], 120, 120, false);
                            Log.e("URL-->", "--" + url);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                Marker marker = map.addMarker(new MarkerOptions()
                                        .title(nearCar.getName())
                                        .position(new LatLng(nearCar.getLats(), nearCar.getLogs()))
                                        .icon(BitmapDescriptorFactory.fromBitmap(bmp[0])));

                                markerslist.add(marker);
                                btnOrder.setVisibility(View.VISIBLE);
                                btnOrderNot.setVisibility(View.GONE);

                            }
                        });
                    }
                });
                thread.start();
            }


        }
        if (markerslist.size() == 0) {
            btnOrder.setVisibility(View.GONE);
            btnOrderNot.setVisibility(View.VISIBLE);
        }

    }


    private void carSetMap(Double lats, Double longs) {
        coursesArrayList = new ArrayList<>();


        db.collection("Admin").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot d : list) {
                                Log.e("currunt--", " " + d.toString());

                                NearCar user = d.toObject(NearCar.class);
                                if (user.getLats() != null && user.getLogs() != null) {


                                    LatLng coordinate = new LatLng(user.getLats(), user.getLogs());
                                    boolean istru = isMarkerOutsideCircle(new LatLng(lats, longs), coordinate);
                                    Log.e("currunt--", " " + lats);
                                    Log.e("car--", " " + user.isIsavailable());

                                    if (istru && user.isIsavailable()) {

                                        coursesArrayList.add(user);
                                        final Bitmap[] bmp = new Bitmap[1];
                                        Thread thread = new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                URL url;
                                                try {
                                                    url = new URL(APIClient.baseUrl + user.getCarimage());
                                                    bmp[0] = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                                                    bmp[0] = Bitmap.createScaledBitmap(bmp[0], 120, 120, false);
                                                    Log.e("URL-->", "--" + url);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Marker marker;
                                                        if (bmp[0] != null) {

                                                            marker = map.addMarker(new MarkerOptions()
                                                                    .title(user.getName())
                                                                    .position(coordinate)
                                                                    .icon(BitmapDescriptorFactory.fromBitmap(bmp[0])));
                                                        } else {
                                                            marker = map.addMarker(new MarkerOptions()
                                                                    .title(user.getName())
                                                                    .position(coordinate)
                                                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.cars)));
                                                        }
                                                        markerslist.add(marker);


                                                    }
                                                });
                                            }
                                        });
                                        thread.start();
                                    }
                                }


                            }
                            if (coursesArrayList.size() == 0) {
                                Toast.makeText(SelectCarActivity.this, "Sorry Not available cars", Toast.LENGTH_LONG).show();
                            }
                            Log.e("size", "-->" + coursesArrayList.size());

                        } else {
                            // if the snapshot is empty we are displaying a toast message.
                            Toast.makeText(SelectCarActivity.this, "No data found in Database", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(SelectCarActivity.this, "Fail to get the data.", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    public void bottonPaymentList() {
        BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(this);
        View sheetView = getLayoutInflater().inflate(R.layout.custome_payment, null);
        LinearLayout listView = sheetView.findViewById(R.id.lvl_list);
        TextView txtBooktrip = sheetView.findViewById(R.id.btn_booktrip);
        LinearLayout lvlWallat = sheetView.findViewById(R.id.lvl_wallat);
        if (sessionManager.getFloatData(wallet) == 0) {
            lvlWallat.setVisibility(View.GONE);
        }
        Switch swich = sheetView.findViewById(R.id.swich);
        TextView txtTotal = sheetView.findViewById(R.id.txt_total);
        TextView txtWallet = sheetView.findViewById(R.id.txt_wallet);
        totalprice = itmeprice - sessionManager.getIntData(coupon);
        txtTotal.setText(getResources().getString(R.string.total) + sessionManager.getStringData(currency) +" "+ totalprice);
        txtWallet.setText(getResources().getString(R.string.my_wallet) + "(" + sessionManager.getStringData(currency) + sessionManager.getFloatData(wallet) + ") ");
        txtBooktrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utility.paymentId = "5";
                placeOrder();
            }
        });
        swich.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    if (sessionManager.getFloatData(wallet) < totalprice) {
                        double t = totalprice - sessionManager.getFloatData(wallet);
                        txtTotal.setText(getResources().getString(R.string.itemtotal) + sessionManager.getStringData(currency) + t);
                        tWallet = sessionManager.getFloatData(wallet);
                        txtBooktrip.setVisibility(View.GONE);

                    } else {
                        double t = sessionManager.getFloatData(wallet) - totalprice;
                        txtWallet.setText(getResources().getString(R.string.my_wallet) + "(" + sessionManager.getStringData(currency) + t + ")");
                        txtTotal.setText(getResources().getString(R.string.itemtotal) + sessionManager.getStringData(currency) + "0");
                        tWallet = totalprice;
                        listView.setVisibility(View.GONE);
                        txtBooktrip.setVisibility(View.VISIBLE);
                    }
                } else {
                    txtTotal.setText(getResources().getString(R.string.itemtotal) + sessionManager.getStringData(currency) + totalprice);
                    txtWallet.setText(getResources().getString(R.string.my_wallet) + " (" + sessionManager.getStringData(currency) + sessionManager.getFloatData(wallet) + ")");
                    listView.setVisibility(View.VISIBLE);
                    txtBooktrip.setVisibility(View.GONE);
                    tWallet = 0;
                }
            }
        });
        for (int i = 0; i < paymentList.size(); i++) {
            LayoutInflater inflater = LayoutInflater.from(SelectCarActivity.this);
            PaymentdataItem paymentItem = paymentList.get(i);
            View view = inflater.inflate(R.layout.custome_paymentitem, null);
            ImageView imageView = view.findViewById(R.id.img_icon);
            TextView txtTitle = view.findViewById(R.id.txt_title);
            TextView txtSubtitel = view.findViewById(R.id.txt_subtitel);
            txtTitle.setText("" + paymentItem.getTitle());
            txtSubtitel.setText("" + paymentItem.getSubtitle());
            Glide.with(SelectCarActivity.this).load(APIClient.baseUrl + "/" + paymentItem.getImg()).thumbnail(Glide.with(SelectCarActivity.this).load(R.drawable.cars)).into(imageView);

            view.setOnClickListener(v -> {
                Utility.paymentId = paymentItem.getId();
                try {
                    switch (paymentItem.getTitle()) {
                        case "Razorpay":
                            int temtoal = (int) Math.round(totalprice);
                            mBottomSheetDialog.cancel();
                            startActivity(new Intent(SelectCarActivity.this, RazerpayActivity.class).putExtra("amount", temtoal).putExtra("detail", (Parcelable) paymentItem));
                            break;
                        case "Cash On Delivery":
                            placeOrder();

                            mBottomSheetDialog.cancel();
                            break;
                        case "Paypal":
                            mBottomSheetDialog.cancel();
                            startActivity(new Intent(SelectCarActivity.this, PaypalActivity.class).putExtra("amount", totalprice).putExtra("detail", (Parcelable) paymentItem));
                            break;
                        case "Stripe":
                            mBottomSheetDialog.cancel();
                            startActivity(new Intent(SelectCarActivity.this, StripPaymentActivity.class).putExtra("amount", totalprice).putExtra("detail", (Parcelable) paymentItem));
                            break;
                        case "FlutterWave":
                            mBottomSheetDialog.cancel();
                            startActivity(new Intent(SelectCarActivity.this, FlutterwaveActivity.class).putExtra("amount", totalprice));
                            break;
                        case "Paytm":
                            mBottomSheetDialog.cancel();
                            startActivity(new Intent(SelectCarActivity.this, PaytmActivity.class).putExtra("amount", totalprice));
                            break;
                        case "SenangPay":
                            mBottomSheetDialog.cancel();
                            startActivity(new Intent(SelectCarActivity.this, SenangpayActivity.class).putExtra("amount", totalprice).putExtra("detail", (Parcelable) paymentItem));
                            break;
                        case "PayStack":
                            int temtoal1 = (int) Math.round(totalprice);
                            mBottomSheetDialog.cancel();
                            startActivity(new Intent(SelectCarActivity.this, PaystackActivity.class).putExtra("amount", temtoal1).putExtra("detail", (Parcelable) paymentItem));
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


    @Override
    public void onTaskDone(Object... values) {

        if (currentPolyline != null)
            currentPolyline.remove();
        currentPolyline = map.addPolyline((PolylineOptions) values[0]);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (sessionManager.getIntData(coupon) != 0) {
            txtApplycode.setText(getResources().getString(R.string.save) + sessionManager.getStringData(currency) + sessionManager.getIntData(coupon));
            animationCoupon.setImageResource(R.drawable.ic_remove_coupon);
        } else {
            txtApplycode.setText(getResources().getString(R.string.apply_coupon));
            sessionManager.setIntData(coupon, 0);
            animationCoupon.setImageResource(R.drawable.ic_arrow_gray);
        }

        if (paymentsucsses == 1) {
            paymentsucsses = 0;
            placeOrder();
        }
    }

}