package com.borakafadar.roamio;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.borakafadar.roamio.App.LocationService;
import com.borakafadar.roamio.App.Save.Converter;
import com.borakafadar.roamio.App.Save.SaveManager;
import com.borakafadar.roamio.App.Trip;
import com.borakafadar.roamio.App.TripSegment;
import com.borakafadar.roamio.App.User;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.*;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.borakafadar.roamio.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.ArrayList;
import java.util.Locale;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, OnMyLocationButtonClickListener, OnMyLocationClickListener {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    //Location services etc.
    private FusedLocationProviderClient fusedLocationProviderClient;

    // A config file for the FusedLocationProviderClient
    private LocationRequest locationRequest;

    private LocationCallback locationCallback;
    private Trip currentTrip;
    private boolean pauseTrip;
    private Polyline routePolyline;
    private User user;
    private boolean isFollowingMyLocation = true;

    private final BroadcastReceiver locationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if("LOCATION_UPDATE".equals(intent.getAction())){
                Location location = intent.getParcelableExtra("location");
                Log.d("locationBroadcast", "Location received: " + location+" Intent: "+ intent);
                if(location != null){

                    if(!pauseTrip){
                        currentTrip.getLatestSegment().addLocation(location);
                    }

                    updatePolyline();

                    TextView tripTimeTextView = findViewById(R.id.tripTimeTextView);
                    tripTimeTextView.setText(currentTrip.getDuration());

                    TextView tripDistanceTextView = findViewById(R.id.tripDistanceTextView);
                    //tripDistanceTextView.setText(currentTrip.getDistanceString());
                    tripDistanceTextView.setText(String.format(Locale.ENGLISH,"%.2f km",currentTrip.getDistance()));

                    LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    if(isFollowingMyLocation){
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation)); //to focus on the moving part
                        //arbitrary number, subject to change
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(15)); //to animate the camera
                    }
                    //mMap.addMarker(new MarkerOptions().title("Here is your current location").position(currentLocation));

                }
            }
        }
    };




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //TODO: check permission requests

        currentTrip = new Trip();
        currentTrip.stopTrip(false);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());



        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        startLocationService();




        getUser();




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


        //TODO:initialize the buttons on a separate method

        pauseTrip = false;
        Button pauseTripButton = findViewById(R.id.pauseTripButton);

        pauseTripButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                pauseTrip = !pauseTrip;

                if(pauseTrip){
                    //TODO: do more stuff here
                    int tintColor = ContextCompat.getColor(MapsActivity.this, R.color.roamio_green);
                    pauseTripButton.setBackgroundTintList(ColorStateList.valueOf(tintColor));
                    pauseTripButton.setText(R.string.resume_trip_text);

                    currentTrip.stopTrip(true);
                    currentTrip.changeSegments(new TripSegment());

                } else{
                    int tintColor = ContextCompat.getColor(MapsActivity.this, R.color.pause_button_grey);
                    pauseTripButton.setBackgroundTintList(ColorStateList.valueOf(tintColor));
                    pauseTripButton.setText(R.string.pause_trip_text);

                    currentTrip.stopTrip(false);
                }
            }
        });

        Button endTripButton = findViewById(R.id.endTripButton);

        endTripButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder= new AlertDialog.Builder(MapsActivity.this);
                builder.setMessage(R.string.end_trip_confirm_text).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //i do not need it to do anything for now
                    }
                }).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        
                        Intent intent = new Intent(MapsActivity.this,MainActivity.class);
                        startActivity(intent);

                        SaveManager.saveTrip(MapsActivity.this, currentTrip);
                        user.addTrip(Converter.tripToTripEntity(currentTrip));
                        user.setTripEntitiesToJson();

                        SaveManager.updateUser(MapsActivity.this, user);

                        MapsActivity.this.finish();
                    }
                }).setTitle("End Trip?");
                builder.show();
            }
        });


        //implementing the pause button
        //for calculating the total distance when the user clicks the pause button
        //i can calculate the total distance when the user clicks the pause button
        //and then i can go onto a new array then calculate the total distance
        //then add the old distance to the new distance
        //maybe use a 2d arraylist? like first store the locations in the arraylist
        //then store that arraylist when the user clicks the pause button
        //then use the big arraylist to access the locations arraylists then calculate
        //each of them and add them together
        //but i think this would be slow as hell so maybe another solution


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



        LatLng bilkentUniversity = new LatLng(39.867349, 32.750255);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(bilkentUniversity));

        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);

        mMap.setOnCameraMoveStartedListener(new OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int reason) {
                if(reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE){
                    isFollowingMyLocation = false;
                }
            }
        });


        //USE POLYLINES FOR DIFFERENT ROUTES
        // Polyline


        //fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);




        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        //fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback,null);

    }


    private void updatePolyline(){
//        ArrayList<LatLng> pointsLatLng = new ArrayList<>();
//        for(Location l : currentTrip.getLocations()){
//            pointsLatLng.add(new LatLng(l.getLatitude(),l.getLongitude()));
//        }
//
//        PolylineOptions polylineOptions = new PolylineOptions();
//        polylineOptions.addAll(pointsLatLng).width(10).visible(true).color(Color.BLUE);
//        routePolyline = mMap.addPolyline(polylineOptions);

        if(routePolyline != null){
            routePolyline.remove();
        }

        for(TripSegment tripSegment : currentTrip.getTripSegments()){
            ArrayList<LatLng> pointsLatLng = new ArrayList<>();
            for(Location l : tripSegment.getLocations()){
                pointsLatLng.add(new LatLng(l.getLatitude(),l.getLongitude()));
            }

            PolylineOptions polylineOptions = new PolylineOptions();
            polylineOptions.addAll(pointsLatLng).width(10).visible(true).color(Color.BLUE)
                    .startCap(new RoundCap()).endCap(new RoundCap())
                    .jointType(JointType.ROUND);
            routePolyline = mMap.addPolyline(polylineOptions);
        }
    }


    @Override
    public boolean onMyLocationButtonClick() {
        //Toast.makeText(this, "Current Location Button Clicked", Toast.LENGTH_SHORT).show();
        isFollowingMyLocation=true;
        return false;
    }


    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current Location" + location, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart(){
        super.onStart();
        IntentFilter filter =  new IntentFilter("LOCATION_UPDATE");
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            registerReceiver(locationReceiver,filter, Context.RECEIVER_NOT_EXPORTED);
        }else{
            registerReceiver(locationReceiver,filter, Context.RECEIVER_NOT_EXPORTED);
        }
    }
    @Override
    protected void onStop(){
        super.onStop();
        //unregisterReceiver(locationReceiver);
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        unregisterReceiver(locationReceiver);
        stopLocationService();
    }

    public void getUser(){
        SaveManager.getUser(this, new SaveManager.UserCallback() {
            @Override
            public void onUserLoaded(User user) {
                MapsActivity.this.user = user;
                //user.setTripEntitiesFromJson();
                user.setTripEntitiesFromTripsTable(MapsActivity.this);
            }

            @Override
            public void onUserCountLoaded(int count) {
                //nothing
            }
        });
    }

    private void startLocationService(){
        Intent intent = new Intent(this, LocationService.class);
        ContextCompat.startForegroundService(this,intent);
    }
    private void stopLocationService(){
        Intent intent = new Intent(this, LocationService.class);
        stopService(intent);
    }

    private void initializeButtons(){

    }


}