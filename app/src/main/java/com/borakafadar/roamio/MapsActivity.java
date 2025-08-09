package com.borakafadar.roamio;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.*;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.borakafadar.roamio.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;




public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, OnMyLocationButtonClickListener, OnMyLocationClickListener {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    //Location services etc.
    private FusedLocationProviderClient fusedLocationProviderClient;

    // A config file for the FusedLocationProviderClient
    private LocationRequest locationRequest;

    private LocationCallback locationCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //TODO: change intervals into constants
        locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000).setMinUpdateIntervalMillis(3000).build();


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);

                Location location = locationResult.getLastLocation();

                LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.addMarker(new MarkerOptions().title("Here is your current location").position(currentLocation));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
                    //arbitrary number, subject to change
                mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
            }
        };




        //Map view container settings
        FrameLayout mainContentContainer = findViewById(R.id.main_content_container);
        int screenHeight = getResources().getDisplayMetrics().heightPixels;

        //80% of the screen is the map
        int targetHeight = (int) (screenHeight * 0.80f);

        ViewGroup.LayoutParams mainContentContainerParams = mainContentContainer.getLayoutParams();
        mainContentContainerParams.height = targetHeight;
        mainContentContainer.setLayoutParams(mainContentContainerParams);


        //bottom sheet view settings
        View bottomSheet = findViewById(R.id.bottom_sheet_design);
        BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        //int peekHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,200,getResources().getDisplayMetrics()); // 200dp fixed
        //%20 percent of the view
        int peekHeight = (int) (screenHeight * 0.20f);
        bottomSheetBehavior.setPeekHeight(peekHeight);
        bottomSheetBehavior.setHideable(false);


        //I CAN STORE LONGITUDE AND LATITUDE IN ARRAY LIST THEN PLACE EVERYTHING ON THE MAP
        //like in an 2d arraylist
    }

    /** TODO: change this comment
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.addCircle(new CircleOptions().center(sydney).fillColor(Color.BLACK).visible(true).strokeWidth(300).clickable(true));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);
        mMap.setMyLocationEnabled(true);

        //USE POLYLINES FOR DIFFERENT ROUTES
        // Polyline

        updateGPS(mMap);



        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback,null);

    }

    private void updateGPS(GoogleMap googleMap) {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //TODO: implement
            //if the user declines the permission
            System.out.println("no permission");
        } else {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if(location != null) {
                        LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        googleMap.addMarker(new MarkerOptions().title("Here is your current location").position(currentLocation));
                        googleMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
                        //arbitrary number, subject to change
                        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                    }
                }
            });

        }
    }

    private void updatePolyline(Location location){

    }


    @Override
    public boolean onMyLocationButtonClick() {
        //Toast.makeText(this, "Current Location Button Clicked", Toast.LENGTH_SHORT).show();
        return false;
    }


    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current Location" + location, Toast.LENGTH_SHORT).show();
    }
}