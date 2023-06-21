package com.customerogo.app.activity;

import static android.content.ContentValues.TAG;
import static com.customerogo.app.utility.Utility.drawTextToBitmap;
import static com.customerogo.app.utility.Utility.getUrl;
import static com.customerogo.app.utility.Utility.isMarkerOutsideCircle;

import android.animation.IntEvaluator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.location.Location;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.customerogo.app.R;
import com.customerogo.app.map.FetchURL;
import com.customerogo.app.map.TaskLoadedCallback;
import com.customerogo.app.model.Markers;
import com.customerogo.app.model.NearCar;
import com.customerogo.app.model.Order;
import com.customerogo.app.model.RestResponse;
import com.customerogo.app.model.User;
import com.customerogo.app.retrofit.APIClient;
import com.customerogo.app.retrofit.GetResult;
import com.customerogo.app.utility.CustPrograssbar;
import com.customerogo.app.utility.SessionManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;

public class TrackTripActivity extends BaseActivity implements OnMapReadyCallback, GetResult.MyListener, TaskLoadedCallback {

    @BindView(R.id.txt_title)
    public TextView txtTitle;
    @BindView(R.id.txt_suntitle)
    public TextView txtSuntitle;
    @BindView(R.id.txt_time)
    public TextView txtTime;
    @BindView(R.id.lvl_time)
    public LinearLayout lvlTime;
    @BindView(R.id.txt_registerno)
    public TextView txtRegisterno;
    @BindView(R.id.img_profile)
    public ImageView imgProfile;
    @BindView(R.id.txt_ridername)
    public TextView txtRidername;

    @BindView(R.id.lvl_d)
    public LinearLayout lvlD;
    @BindView(R.id.btn_cancel)
    public TextView btnCancel;
    @BindView(R.id.btn_details)
    public TextView btnDetails;
    @BindView(R.id.txt_tripotp)
    public TextView txtTripotp;
    @BindView(R.id.prograssbar)
    public ProgressBar progressBar;
    @BindView(R.id.lvl_waiting)
    public LinearLayout lvlWaiting;
    @BindView(R.id.lvl_otp)
    public LinearLayout lvlOtp;
    @BindView(R.id.btn_review)
    public TextView btnReview;
    String oid;
    CustPrograssbar custPrograssbar;
    GoogleMap gMap;
    private Polyline currentPolyline;

    private FirebaseFirestore db;

HashMap<String, Marker> hashmaplist = new HashMap<>();

    private FusedLocationProviderClient fusedLocationProviderClient;
    private int progressStatus = 0;
    private Handler handler = new Handler();
    SessionManager sessionManager;
    User user;
    SupportMapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_trip);
        ButterKnife.bind(this);
        db = FirebaseFirestore.getInstance();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        sessionManager = new SessionManager(this);
        user = sessionManager.getUserDetails();
        custPrograssbar = new CustPrograssbar();
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);


        oid = getIntent().getStringExtra("order_id");
        Log.e("id", "-->" + oid);


        mapFragment.getMapAsync(this);
        getOrder();
    }

    private void getOrder() {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("orderid", oid);


            RequestBody bodyRequest = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
            Call<JsonObject> call = APIClient.getInterface().getOrderMaps(bodyRequest);
            GetResult getResult = new GetResult();
            getResult.setMyListener(this);
            getResult.callForLogin(call, "1");
            custPrograssbar.prograssCreate(TrackTripActivity.this);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void getOrdercancel() {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("order_id", oid);
            jsonObject.put("uid", user.getId());


            RequestBody bodyRequest = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
            Call<JsonObject> call = APIClient.getInterface().orderCancle(bodyRequest);
            GetResult getResult = new GetResult();
            getResult.setMyListener(this);
            getResult.callForLogin(call, "2");
            custPrograssbar.prograssCreate(TrackTripActivity.this);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    Order order;

    @Override
    public void callback(JsonObject result, String callNo) {
        try {
            if (callNo.equalsIgnoreCase("1")) {


                custPrograssbar.closePrograssBar();
                Gson gson = new Gson();
                order = gson.fromJson(result.toString(), Order.class);
                if (order.getResult().equalsIgnoreCase("true")) {


                    txtTitle.setText("" + order.getMapinfo().getTitle());
                    Log.e("Stap-->", "" + order.getMapinfo().getRequestStep());
                    switch (order.getMapinfo().getRequestStep()) {
                        case 1:
                            txtSuntitle.setText("" + order.getMapinfo().getSubtitle());
                            lvlD.setVisibility(View.GONE);
                            lvlTime.setVisibility(View.VISIBLE);
                            gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(order.getMapinfo().getPickLat(), order.getMapinfo().getPickLong()), 14));
                            updateStatusForFirbase();
                            carSetMap(order.getMapinfo().getPickLat(), order.getMapinfo().getPickLong());
                            drawCircle(new LatLng(order.getMapinfo().getPickLat(), order.getMapinfo().getPickLong()));

                            if (30 > order.getMapinfo().getRequestArriveSeconds()) {

                                new Thread(() -> {
                                    while (progressStatus < 30) {
                                        progressStatus += 1;
                                        try {
                                            Thread.sleep(1000);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        //Update the progress bar
                                        handler.post(() -> {

                                            progressBar.setProgress(progressStatus);

                                        });
                                    }
                                }).start();


                                new CountDownTimer(30000, 1000) {

                                    public void onTick(long millisUntilFinished) {
                                        txtTime.setText("seconds remaining: " + millisUntilFinished / 1000);

                                        // logic to set the EditText could go here
                                    }

                                    public void onFinish() {

                                        btnCancel.setVisibility(View.GONE);
                                        progressBar.setVisibility(View.GONE);
                                        lvlTime.setVisibility(View.GONE);


                                    }

                                }.start();

                                new CountDownTimer(180000, 1000) {

                                    public void onTick(long millisUntilFinished) {
                                        // logic to set the EditText could go here
                                    }

                                    public void onFinish() {

                                        alertbox();


                                    }

                                }.start();

                            } else {
                                btnCancel.setVisibility(View.GONE);
                                progressBar.setVisibility(View.GONE);
                                lvlTime.setVisibility(View.GONE);

                                new CountDownTimer(180000, 1000) {
                                    public void onTick(long millisUntilFinished) {
                                    }

                                    public void onFinish() {
                                        alertbox();
                                    }
                                }.start();

                            }


                            break;
                        case 2:

                            updateCarForFirbase(order.getMapinfo().getRiderid());
                            lvlD.setVisibility(View.VISIBLE);
                            lvlTime.setVisibility(View.GONE);
                            txtTripotp.setText("" + order.getMapinfo().getOtp());

                            txtRegisterno.setText("CAR NO : " + order.getMapinfo().getVehicleNumber());
                            txtRidername.setText("" + order.getMapinfo().getRiderName());
                            Glide.with(this).load(APIClient.baseUrl + "/" + order.getMapinfo().getRiderImg()).thumbnail(Glide.with(this).load(R.drawable.user)).into(imgProfile);

                            break;
                        case 3:
                            lvlD.setVisibility(View.VISIBLE);
                            lvlTime.setVisibility(View.GONE);
                            txtSuntitle.setText("");
                            updateCarForFirbase(order.getMapinfo().getRiderid());
                            txtTripotp.setText("" + order.getMapinfo().getOtp());
                            txtRegisterno.setText("CAR NO : " + order.getMapinfo().getVehicleNumber());
                            txtRidername.setText("" + order.getMapinfo().getRiderName());
                            Glide.with(this).load(APIClient.baseUrl + "/" + order.getMapinfo().getRiderImg()).thumbnail(Glide.with(this).load(R.drawable.user)).into(imgProfile);


                            break;
                        case 4:


                            txtRegisterno.setText("CAR NO : " + order.getMapinfo().getVehicleNumber());
                            txtRidername.setText("" + order.getMapinfo().getRiderName());
                            Glide.with(this).load(APIClient.baseUrl + "/" + order.getMapinfo().getRiderImg()).thumbnail(Glide.with(this).load(R.drawable.user)).into(imgProfile);
                            updateCarForFirbase(order.getMapinfo().getRiderid());
                            lvlD.setVisibility(View.VISIBLE);
                            lvlOtp.setVisibility(View.GONE);
                            lvlTime.setVisibility(View.GONE);
                            txtSuntitle.setText("");


                            break;

                    }


                }
            } else if (callNo.equalsIgnoreCase("2")) {
                Gson gson = new Gson();
                RestResponse response = gson.fromJson(result.toString(), RestResponse.class);
                if (response.getResult().equals("true")) {
                    Toast.makeText(this, response.getResponseMsg(), Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(TrackTripActivity.this, HomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    finish();
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

    private void drawCircle(LatLng point) {


        GradientDrawable d = new GradientDrawable();
        d.setShape(GradientDrawable.OVAL);
        d.setSize(5000, 5000);
        d.setColor(0x555751FF);
        d.setStroke(5, Color.TRANSPARENT);

        Bitmap bitmap = Bitmap.createBitmap(d.getIntrinsicWidth()
                , d.getIntrinsicHeight()
                , Bitmap.Config.ARGB_8888);

        // Convert the drawable to bitmap
        Canvas canvas = new Canvas(bitmap);
        d.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        d.draw(canvas);

        // Radius of the circle
        final int radius = 1000;


        final GroundOverlay circle = gMap.addGroundOverlay(new GroundOverlayOptions()
                .position(point, 2 * radius).image(BitmapDescriptorFactory.fromBitmap(bitmap)));


        ValueAnimator valueAnimator = new ValueAnimator();
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.setRepeatMode(ValueAnimator.RESTART);
        valueAnimator.setIntValues(0, radius);
        valueAnimator.setDuration(3000);
        valueAnimator.setEvaluator(new IntEvaluator());
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float animatedFraction = valueAnimator.getAnimatedFraction();
                circle.setDimensions(animatedFraction * radius * 2);
            }
        });
        gMap.addMarker(new MarkerOptions()
                .title("Me")
                .position(point)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pickup_map_pin)));

        valueAnimator.start();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        getDeviceLocation();

    }

    ListenerRegistration listenerRegistration;
    ListenerRegistration singleData;

    public void updateStatusForFirbase() {
        hashmaplist=new HashMap<>();
        listenerRegistration = db.collection("Admin")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w("TAG", "listen:error", e);
                            return;
                        }

                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            switch (dc.getType()) {
                                case ADDED:
                                    Log.d("TAG", "New Msg: " + dc.getDocument().toObject(Message.class));
                                    break;
                                case MODIFIED:
                                    Log.d("TAG", "Modified Msg: " + dc.getDocument().getId());

                                    final DocumentReference docRef = db.collection("Admin").document(dc.getDocument().getId());
                                    singleData = docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                        @Override
                                        public void onEvent(@Nullable DocumentSnapshot snapshot,
                                                            @Nullable FirebaseFirestoreException e) {

                                            if (snapshot.exists()) {

                                                NearCar user = snapshot.toObject(NearCar.class);
                                                if (oid.equalsIgnoreCase(user.getOrderid())) {
                                                    getOrder();
                                                    Log.e("Update Data", "-->" + user.getName());
                                                } else {

                                                    Log.e("User Data", "-->" + user.getName());
                                                    Log.e("User Data", "-->" + user.getId());

                                                    Marker marker1= hashmaplist.get(user.getId());

                                                    Log.e("markerslist", "-->" + marker1.getId());
                                                    marker1.remove();

                                                    final Bitmap[] bmp = new Bitmap[1];


                                                    Thread thread = new Thread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            URL url;
                                                            try {
                                                                url = new URL(APIClient.baseUrl + user.getCarimage());
                                                                bmp[0] = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                                                                bmp[0] = Bitmap.createScaledBitmap(bmp[0], 150,100 , false);
                                                                Log.e("URL-->", "--" + url);
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }
                                                            runOnUiThread(new Runnable() {
                                                                @Override
                                                                public void run() {


                                                                    Marker markerss = gMap.addMarker(new MarkerOptions()
                                                                            .title(user.getName())
                                                                            .position(new LatLng(user.getLats(), user.getLogs()))
                                                                            .icon(BitmapDescriptorFactory.fromBitmap(bmp[0])));



                                                                    Markers markers = new Markers();
                                                                    markers.setNearCar(user);
                                                                    markers.setMarker(markerss);
                                                                    hashmaplist.put(user.getId(),markerss);

                                                                }
                                                            });
                                                        }
                                                    });
                                                    thread.start();

                                                }


                                            }
                                        }
                                    });

                                    break;
                                case REMOVED:
                                    Log.d("TAG", "Removed Msg: " + dc.getDocument().toObject(Message.class));
                                    break;
                            }
                        }

                    }
                });
    }

    public void updateCarForFirbase(String id) {
        if (listenerRegistration != null) {
            listenerRegistration.remove();
        }
        if (singleData != null) {
            singleData.remove();
        }
        Log.e("RID", id);
        final DocumentReference docRef = db.collection("Admin").document(id);
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    Log.e(TAG, "snapshot data: " + snapshot);

                    NearCar user = snapshot.toObject(NearCar.class);
                    Log.e(TAG, "Current data: " + user.getName());
                    Log.e(TAG, "Current data: " + user.getRequest_step());
                    if (user.getLats() != null) {
                        LatLng place1 = new LatLng(user.getLats(), user.getLogs());

                        switch (user.getRequest_step()) {
                            case "1":
                                txtTitle.setText("Search For A Car");
                                txtSuntitle.setText("There are several cars, we are looking at which one will fit best");
                                break;
                            case "2":
                                txtTitle.setText("Will arrive in minutes");
                                txtSuntitle.setText("");
                                gMap.clear();
                                LatLng place2 = new LatLng(order.getMapinfo().getPickLat(), order.getMapinfo().getPickLong());
                                gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place1, 14));

                                drawPath(place1, place2);
                                break;
                            case "3":

                                txtTitle.setText("Waiting for you");
                                txtSuntitle.setText("");
                                gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place1, 14));

                                Bitmap bitmap = drawTextToBitmap(TrackTripActivity.this, R.drawable.cars, "");

                                gMap.addMarker(new MarkerOptions()
                                        .position(place1)
                                        .icon(BitmapDescriptorFactory.fromBitmap(bitmap)));
                                if (currentPolyline != null)
                                    currentPolyline.remove();

                                break;

                            case "4":
                                txtTitle.setText("Your Ride Was Started.");
                                txtSuntitle.setText("");

                                place2 = new LatLng(order.getMapinfo().getDropLat(), order.getMapinfo().getDropLong());
                                drawPath(place1, place2);
                                gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place1, 14));

                                lvlD.setVisibility(View.VISIBLE);
                                lvlOtp.setVisibility(View.GONE);
                                lvlTime.setVisibility(View.GONE);
                                txtSuntitle.setText("");
                                break;
                            case "5":

                                txtTitle.setText("Your Ride Completed Successfully.");
                                txtSuntitle.setText("");
                                btnReview.setVisibility(View.VISIBLE);
                                if (currentPolyline != null)
                                    currentPolyline.remove();

                                place2 = new LatLng(order.getMapinfo().getDropLat(), order.getMapinfo().getDropLong());
                                gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place2, 14));

                                Map<String, Object> city = new HashMap<>();

                                city.put("orderid", "0");
                                city.put("request_step", "0");

                                db.collection("Admin").document(id).update(city);


                                break;
                            case "6":

                                txtSuntitle.setText("");
                                btnReview.setVisibility(View.VISIBLE);
                                if (currentPolyline != null)
                                    currentPolyline.remove();

                                place2 = new LatLng(order.getMapinfo().getDropLat(), order.getMapinfo().getDropLong());
                                gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place2, 14));


                                break;
                        }
                    }

                } else {
                    Log.d(TAG, "Current data: null");
                }
            }
        });
    }

    private void drawPath(LatLng place1, LatLng place2) {


        Bitmap bitmap = drawTextToBitmap(TrackTripActivity.this, R.drawable.cars, "");
        Bitmap bitmap1 = drawTextToBitmap(TrackTripActivity.this, R.drawable.ic_pickup_map_pin, "");

        gMap.addMarker(new MarkerOptions()
                .position(place1)
                .icon(BitmapDescriptorFactory.fromBitmap(bitmap)));

        gMap.addMarker(new MarkerOptions()
                .position(place2)
                .icon(BitmapDescriptorFactory.fromBitmap(bitmap1)));


        CameraUpdate center =
                CameraUpdateFactory.newLatLng(place2);
        CameraUpdate zoom = CameraUpdateFactory.newLatLngZoom(place1, 15);

        gMap.moveCamera(center);
        gMap.animateCamera(zoom);
        gMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(TrackTripActivity.this, R.raw.map_style));


        new FetchURL(TrackTripActivity.this).execute(getUrl(place1, place2, "driving"), "driving");
    }

    private void carSetMap(Double lats, Double longs) {
        hashmaplist = new HashMap<>();

        db.collection("Admin").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot d : list) {
                                NearCar user = d.toObject(NearCar.class);
                                if (user.getLats() != null && user.getLogs() != null) {

                                    LatLng coordinate = new LatLng(user.getLats(), user.getLogs());
                                    boolean istru = isMarkerOutsideCircle(new LatLng(lats, longs), coordinate);
                                    if (istru) {

                                        final Bitmap[] bmp = new Bitmap[1];
                                        Thread thread = new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                URL url;
                                                try {
                                                    url = new URL(APIClient.baseUrl + user.getCarimage());
                                                    bmp[0] = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                                                    bmp[0] = Bitmap.createScaledBitmap(bmp[0], 120, 120, false);
                                                    Log.e("urllll-","--"+url);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {

                                                        Marker markerss;
                                                        if (bmp[0] != null) {
                                                            markerss = gMap.addMarker(new MarkerOptions()
                                                                    .title(user.getName())
                                                                    .position(new LatLng(user.getLats(), user.getLogs()))
                                                                    .icon(BitmapDescriptorFactory.fromBitmap(bmp[0])));
                                                        } else {
                                                            markerss = gMap.addMarker(new MarkerOptions()
                                                                    .title(user.getName())
                                                                    .position(new LatLng(user.getLats(), user.getLogs()))
                                                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.cars)));
                                                        }

                                                         Markers markers = new Markers();
                                                        markers.setNearCar(user);
                                                        markers.setMarker(markerss);
                                                        hashmaplist.put(user.getId(),markerss);

                                                    }
                                                });
                                            }
                                        });
                                        thread.start();
                                    }
                                }
                            }


                        } else {
                            // if the snapshot is empty we are displaying a toast message.
                            Toast.makeText(TrackTripActivity.this, "No data found in Database", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(TrackTripActivity.this, "Fail to get the data.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        final Location[] lastKnownLocation = new Location[1];

        try {
            Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
            locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if (task.isSuccessful()) {
                        // Set the map's camera position to the current location of the device.
                        lastKnownLocation[0] = task.getResult();

                    } else {
                        Log.d(TAG, "Current location is null. Using defaults.");
                        Log.e(TAG, "Exception: %s", task.getException());
                        gMap.moveCamera(CameraUpdateFactory
                                .newLatLngZoom(new LatLng(-33.8523341, 151.2106085), 14));
                        gMap.getUiSettings().setMyLocationButtonEnabled(false);

                    }
                }
            });
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }

    @OnClick({R.id.btn_cancel, R.id.btn_details, R.id.btn_review, R.id.img_back})

    public void onBindClick(View view) {
        switch (view.getId()) {
            case R.id.btn_cancel:

                final Dialog dialog = new Dialog(TrackTripActivity.this);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setContentView(R.layout.custom_dailoglayout);

                TextView btn_cancle = dialog.findViewById(R.id.btn_cancle);


                btn_cancle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        getOrdercancel();
                    }
                });

                dialog.show();

                break;
            case R.id.btn_details:

                startActivity(new Intent(TrackTripActivity.this, TripeDetailsActivity.class).putExtra("order_id", oid));

                break;

            case R.id.btn_review:
                bottonRatingLocation();

                break;
            case R.id.img_back:

                finish();

                break;
            default:
                throw new IllegalStateException("Unexpected value: " + view.getId());
        }
    }

    public void alertbox() {
        final Dialog dialog = new Dialog(TrackTripActivity.this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.custom_researchcar);

        TextView btn_cancle = dialog.findViewById(R.id.btn_cancle);
        TextView btn_continu = dialog.findViewById(R.id.btn_continu);

        btn_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                getOrdercancel();
            }
        });
        btn_continu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setCancelable(false);
        if (!(TrackTripActivity.this).isFinishing()) {
            dialog.show();

        }
    }

    @Override
    public void onTaskDone(Object... values) {

        if (currentPolyline != null)
            currentPolyline.remove();
        currentPolyline = gMap.addPolyline((PolylineOptions) values[0]);

    }

    public void bottonRatingLocation() {


        BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(this);
        View sheetView = getLayoutInflater().inflate(R.layout.custome_rating, null);
        ImageView imgProfile = sheetView.findViewById(R.id.img_profile);
        TextView txtRidername = sheetView.findViewById(R.id.txt_ridername);
        TextView btnConti = sheetView.findViewById(R.id.btn_conti);
        EditText edRview = sheetView.findViewById(R.id.ed_rview);


        RatingBar rb = sheetView.findViewById(R.id.rating);


        txtRidername.setText("" + order.getMapinfo().getRiderName());
        Glide.with(this).load(APIClient.baseUrl + "/" + order.getMapinfo().getRiderImg()).thumbnail(Glide.with(this).load(R.drawable.user)).into(imgProfile);


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
            custPrograssbar.prograssCreate(TrackTripActivity.this);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}