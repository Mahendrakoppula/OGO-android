package com.customerogo.app.activity;

import static android.content.ContentValues.TAG;
import static com.customerogo.app.utility.SessionManager.language;
import static com.customerogo.app.utility.Utility.isMarkerOutsideCircle;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.customerogo.app.R;
import com.customerogo.app.adepter.AddressAdapter;
import com.customerogo.app.adepter.AutoCompleteAdapter;
import com.customerogo.app.model.Address;
import com.customerogo.app.model.Home;
import com.customerogo.app.model.Markers;
import com.customerogo.app.model.NearCar;
import com.customerogo.app.model.User;
import com.customerogo.app.retrofit.APIClient;
import com.customerogo.app.retrofit.GetResult;
import com.customerogo.app.utility.CustPrograssbar;
import com.customerogo.app.utility.MyHelper;
import com.customerogo.app.utility.SessionManager;
import com.customerogo.app.utility.Utility;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;

public class HomeActivity extends BaseActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener, GetResult.MyListener {

    @BindView(R.id.lvl_whereareyou)
    public LinearLayout lvlWhereareyou;

    @BindView(R.id.txt_daddress)
    public TextView txtAddress;
    private GoogleMap map;

    private PlacesClient placesClient;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location lastKnownLocation;
    private boolean locationPermissionGranted;

    AutoCompleteTextView autoCompleteTextView;
    AutoCompleteTextView autoCompleteTextViewDrop;
    AutoCompleteAdapter adapter;
    AutoCompleteAdapter adapterDrop;

    private FirebaseFirestore db;

    SessionManager sessionManager;
    SupportMapFragment mapFragment;
    CustPrograssbar custPrograssbar;

    HashMap<String, Marker> hashmaplist = new HashMap<>();
    DrawerLayout dLayout;
    User user;
    MyHelper myHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        myHelper = new MyHelper(this);
        custPrograssbar = new CustPrograssbar();
        sessionManager = new SessionManager(HomeActivity.this);
        dLayout = (DrawerLayout) findViewById(R.id.drawer);

        if (!Utility.hasGPSDevice(this)) {
            Utility.enableLoc(this);

        } else {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
            user = sessionManager.getUserDetails();
            mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);


            if (!Places.isInitialized()) {
                Places.initialize(getApplicationContext(), getString(R.string.api_key));
            }
            placesClient = Places.createClient(this);

            db = FirebaseFirestore.getInstance();

            db.collection("Admin")
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
                                        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                            @Override
                                            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                                                @Nullable FirebaseFirestoreException e) {

                                                if (snapshot.exists()) {

                                                    NearCar user = snapshot.toObject(NearCar.class);
                                                    Log.e("User Data", "-->" + user.getName());
                                                    Log.e("User Data", "-->" + user.getId());


                                                    Marker marker1 = hashmaplist.get(user.getId());

                                                    if (marker1 != null)
                                                        marker1.remove();

                                                    final Bitmap[] bmp = new Bitmap[1];


                                                    Thread thread = new Thread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            URL url;
                                                            try {
                                                                url = new URL(APIClient.baseUrl + user.getCarimage());
                                                                bmp[0] = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                                                                bmp[0] = Bitmap.createScaledBitmap(bmp[0], 150, 100, false);
                                                                Log.e("URL-->", "--" + url);
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }
                                                            runOnUiThread(new Runnable() {
                                                                @Override
                                                                public void run() {


                                                                    Marker markerss = map.addMarker(new MarkerOptions()
                                                                            .title(user.getName())
                                                                            .position(new LatLng(user.getLats(), user.getLogs()))
                                                                            .icon(BitmapDescriptorFactory.fromBitmap(bmp[0])));


                                                                    Markers markers = new Markers();
                                                                    markers.setNearCar(user);
                                                                    markers.setMarker(markerss);
                                                                    hashmaplist.put(user.getId(), markerss);

                                                                }
                                                            });
                                                        }
                                                    });
                                                    thread.start();


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


            // get the bottom sheet view
            NavigationView navigationView = findViewById(R.id.container);
            navigationView.setNavigationItemSelectedListener(this);
            View headerLayout = navigationView.getHeaderView(0);
            TextView name = headerLayout.findViewById(R.id.txt_name);
            TextView mobile = headerLayout.findViewById(R.id.txt_mobile);

            name.setText("" + user.getFname());
            mobile.setText("" + user.getCcode() + user.getMobile());

            getHome();
        }


    }

    private void getHome() {
        custPrograssbar.prograssCreate(this);
        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("uid", user.getId());

        } catch (Exception e) {
            e.printStackTrace();
        }
        RequestBody bodyRequest = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
        Call<JsonObject> call = APIClient.getInterface().homeData(bodyRequest);
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
                Home home = gson.fromJson(result.toString(), Home.class);

                sessionManager.setStringData(SessionManager.currency, home.getHomeData().getCurrency());
                sessionManager.setStringData(language, home.getHomeData().getAppLan());
                Log.e("sssssss", "" + sessionManager.getStringData(language));
            }
        } catch (Exception e) {
            Log.e("erro", "-->" + e.toString());
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.map = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        this.map.setMyLocationEnabled(true);
        this.map.getUiSettings().setMyLocationButtonEnabled(true);

        View mapView = mapFragment.getView();
        if (mapView != null &&
                mapView.findViewById(1) != null) {
            // Get the button view
            @SuppressLint("ResourceType") View locationButton = ((View) mapView.findViewById(1).getParent()).findViewById(2);
            // and next place it, on bottom right (as Google Maps app)
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                    locationButton.getLayoutParams();
            // position on right bottom
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(0, 0, 80, 60);
        }


        this.map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {

                if (cameraPosition.target.longitude != 0.0) {

                    Geocoder geocoder;
                    List<android.location.Address> addresses;
                    geocoder = new Geocoder(HomeActivity.this, Locale.getDefault());
                    try {
                        addresses = geocoder.getFromLocation(cameraPosition.target.latitude, cameraPosition.target.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                        String addressa = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                        String city = addresses.get(0).getLocality();

                        String postalCode = addresses.get(0).getPostalCode();
                        String knownName = addresses.get(0).getFeatureName();

                        txtAddress.setText("" + addressa);
                        Address address = new Address();
                        address.setId("");
                        address.setAddres(addressa + city + postalCode);
                        address.setTitle(knownName);
                        address.setLats(cameraPosition.target.latitude);
                        address.setLongs(cameraPosition.target.longitude);
                        sessionManager.setAddresspic(address);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }


            }
        });


        getLocationPermission();
        // [END_EXCLUDE]

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();


    }

    private void carSetMap(Double lats, Double longs) {

        custPrograssbar.prograssCreate(HomeActivity.this);

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
                                    Log.e("currunt--", " " + lats);
                                    Log.e("car--", " " + coordinate.latitude);

                                    if (istru && user.isIsavailable()) {

                                        Geocoder geocoder;
                                        List<android.location.Address> addresses;
                                        geocoder = new Geocoder(HomeActivity.this, Locale.getDefault());
                                        try {
                                            addresses = geocoder.getFromLocation(lats, longs, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                                            String addressa = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                                            String city = addresses.get(0).getLocality();

                                            String postalCode = addresses.get(0).getPostalCode();
                                            String knownName = addresses.get(0).getFeatureName();

                                            txtAddress.setText("" + addressa);
                                            Address address = new Address();
                                            address.setId("");
                                            address.setAddres(addressa + city + postalCode);
                                            address.setTitle(knownName);
                                            address.setLats(lats);
                                            address.setLongs(longs);
                                            sessionManager.setAddresspic(address);

                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }

                                        final Bitmap[] bmp = new Bitmap[1];
                                        Thread thread = new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                URL url;
                                                try {
                                                    url = new URL(APIClient.baseUrl + user.getCarimage());
                                                    bmp[0] = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                                                    bmp[0] = Bitmap.createScaledBitmap(bmp[0], 150, 100, false);
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


                                                        custPrograssbar.closePrograssBar();
                                                        Markers markers = new Markers();
                                                        markers.setNearCar(user);
                                                        markers.setMarker(marker);


                                                        hashmaplist.put(user.getId(), marker);
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
                            Toast.makeText(HomeActivity.this, "No data found in Database", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(HomeActivity.this, "Fail to get the data.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateLocationUI() {
        if (map == null) {
            return;
        }
        try {
            if (locationPermissionGranted) {
                map.setMyLocationEnabled(true);
                map.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                map.setMyLocationEnabled(false);
                map.getUiSettings().setMyLocationButtonEnabled(false);
                lastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    boolean mapisLoad = false;

    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (locationPermissionGranted) {
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            lastKnownLocation = task.getResult();
                            if (lastKnownLocation != null) {
                                map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(lastKnownLocation.getLatitude(),
                                                lastKnownLocation.getLongitude()), 14));
                                mapisLoad = true;
                                carSetMap(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());


                            }
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            map.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(new LatLng(-33.8523341, 151.2106085), 14));
                            map.getUiSettings().setMyLocationButtonEnabled(false);

                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }


    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
        }
    }


    public void bottonSearchLocation() {


        BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(this);
        View sheetView = getLayoutInflater().inflate(R.layout.custome_locationsearch, null);
        TextView btnConti = sheetView.findViewById(R.id.btn_conti);
        RecyclerView recyclerView = sheetView.findViewById(R.id.recyview);
        LinearLayoutManager mLayoutManager2 = new LinearLayoutManager(this);
        mLayoutManager2.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLayoutManager2);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        autoCompleteTextView = sheetView.findViewById(R.id.txt_pick);
        autoCompleteTextViewDrop = sheetView.findViewById(R.id.txt_drop);
        autoCompleteTextViewDrop.setDropDownVerticalOffset(0);
        autoCompleteTextView.setThreshold(1);
        autoCompleteTextView.setOnItemClickListener(autocompleteClickListener);
        adapter = new AutoCompleteAdapter(this, placesClient);
        autoCompleteTextView.setAdapter(adapter);
        autoCompleteTextView.setText(txtAddress.getText().toString() + "");

        autoCompleteTextViewDrop.setThreshold(1);
        autoCompleteTextViewDrop.setOnItemClickListener(autocompleteClickListenerDrop);
        adapterDrop = new AutoCompleteAdapter(this, placesClient);
        autoCompleteTextViewDrop.setAdapter(adapterDrop);

        recyclerView.setAdapter(new AddressAdapter(HomeActivity.this, myHelper.getCData()));

        btnConti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(autoCompleteTextViewDrop.getText().toString())) {

                    myHelper.insertData(sessionManager.getAddresspi());
                    myHelper.insertData(sessionManager.getAddressDrop());
                    startActivity(new Intent(HomeActivity.this, SelectCarActivity.class));

                } else {
                    autoCompleteTextViewDrop.setError("");
                }
            }
        });
        mBottomSheetDialog.setContentView(sheetView);
        mBottomSheetDialog.show();
    }

    @OnClick({R.id.lvl_whereareyou, R.id.img_menu})
    public void onBindClick(View view) {
        int id = view.getId();
        if (id == R.id.lvl_whereareyou) {
            bottonSearchLocation();
        } else if (id == R.id.img_menu) {
            dLayout.openDrawer(GravityCompat.START);
        }
    }


    private final AdapterView.OnItemClickListener autocompleteClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            try {
                final AutocompletePrediction item = adapter.getItem(i);
                String placeID = null;
                if (item != null) {
                    placeID = item.getPlaceId();
                }

//                To specify which data types to return, pass an array of Place.Fields in your FetchPlaceRequest
//                Use only those fields which are required.

                List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS
                        , Place.Field.LAT_LNG);

                FetchPlaceRequest request = null;
                if (placeID != null) {
                    request = FetchPlaceRequest.builder(placeID, placeFields)
                            .build();
                }

                if (request != null) {
                    placesClient.fetchPlace(request).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onSuccess(FetchPlaceResponse task) {

                            autoCompleteTextView.setText(task.getPlace().getName() + "," + task.getPlace().getAddress());
                            Address address = new Address();
                            address.setId("1");
                            address.setLats(task.getPlace().getLatLng().latitude);
                            address.setLongs(task.getPlace().getLatLng().longitude);
                            address.setTitle(task.getPlace().getName());
                            address.setAddres(task.getPlace().getAddress());
                            sessionManager.setAddresspic(address);
                            Log.e("task.etName()", "" + task.getPlace().getName());

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            e.printStackTrace();
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };


    private final AdapterView.OnItemClickListener autocompleteClickListenerDrop = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            try {
                final AutocompletePrediction item = adapterDrop.getItem(i);
                String placeID = null;
                if (item != null) {
                    placeID = item.getPlaceId();
                }

//                To specify which data types to return, pass an array of Place.Fields in your FetchPlaceRequest
//                Use only those fields which are required.

                List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS
                        , Place.Field.LAT_LNG);

                FetchPlaceRequest request = null;
                if (placeID != null) {
                    request = FetchPlaceRequest.builder(placeID, placeFields)
                            .build();
                }

                if (request != null) {
                    placesClient.fetchPlace(request).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onSuccess(FetchPlaceResponse task) {

                            autoCompleteTextViewDrop.setText(task.getPlace().getName() + "," + task.getPlace().getAddress());
                            Address address = new Address();
                            address.setId("1");
                            address.setLats(task.getPlace().getLatLng().latitude);
                            address.setLongs(task.getPlace().getLatLng().longitude);
                            address.setTitle(task.getPlace().getName());
                            address.setAddres(task.getPlace().getAddress());
                            sessionManager.setAddressDrop(address);
                            Log.e("task.etName()", "" + task.getPlace().getName());

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            e.printStackTrace();
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu1:
                startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
                break;
            case R.id.menu2:
                startActivity(new Intent(HomeActivity.this, TripHistoryActivity.class));

                break;
            case R.id.menu3:
                startActivity(new Intent(HomeActivity.this, NotificationActivity.class));
                break;
            case R.id.menu4:
                startActivity(new Intent(HomeActivity.this, ReferActivity.class));

                break;
            case R.id.menu5:
                startActivity(new Intent(HomeActivity.this, HelpActivity.class));

                break;
            case R.id.menu6:
                startActivity(new Intent(HomeActivity.this, WalletActivity.class));

                break;
            case R.id.menu7:
                sessionManager.logoutUser();
                startActivity(new Intent(this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                finish();

                break;
            case R.id.menu8:

                startActivity(new Intent(HomeActivity.this, FaqActivity.class));

                break;
            default:
                throw new IllegalStateException("Unexpected value: " + item.getItemId());
        }

        return false;
    }


}