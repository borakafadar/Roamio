package com.borakafadar.roamio;

import androidx.fragment.app.FragmentActivity;

import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.borakafadar.roamio.App.Save.SaveManager;
import com.borakafadar.roamio.App.Save.TripEntity;
import com.borakafadar.roamio.App.Trip;
import com.borakafadar.roamio.App.TripSegment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.borakafadar.roamio.databinding.ActivityTripMapsBinding;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.ArrayList;
import java.util.List;

public class TripMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityTripMapsBinding binding;
    private TripEntity tripEntity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityTripMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.trips_map);
        mapFragment.getMapAsync(this);


        Bundle bundle = getIntent().getExtras();
        if(bundle == null) {
            return;
        }

        int tripID = bundle.getInt("TRIP_ID");


        getTripFromDatabase(tripID);

        TextView title = findViewById(R.id.pastTripTitleTextView);

        Handler handler = new Handler(Looper.getMainLooper());
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if(tripEntity != null){
                    title.setText(tripEntity.getTitle());
                    updatePolylines(tripEntity.getTripSegments());
                    Log.d("tripLog", tripEntity.toString());
                }
            }
        };
        handler.postDelayed(runnable, 1000);


        //Map view container settings
        FrameLayout mainContentContainer = findViewById(R.id.past_trip_main_content_container);
        int screenHeight = getResources().getDisplayMetrics().heightPixels;

        //80% of the screen is the map
        int targetHeight = (int) (screenHeight * 0.80f);

        ViewGroup.LayoutParams mainContentContainerParams = mainContentContainer.getLayoutParams();
        mainContentContainerParams.height = targetHeight;
        mainContentContainer.setLayoutParams(mainContentContainerParams);


        //bottom sheet view settings
        View bottomSheet = findViewById(R.id.trip_bottom_sheet_design);
        BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        //int peekHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,200,getResources().getDisplayMetrics()); // 200dp fixed
        //%20 percent of the view
        int peekHeight = (int) (screenHeight * 0.20f);
        bottomSheetBehavior.setPeekHeight(peekHeight);
        bottomSheetBehavior.setHideable(false);



        new Thread(() -> {

        });


    }


    /**
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
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    public void updatePolylines(ArrayList<TripSegment> tripSegments){
        String test ="";
        for(TripSegment segment : tripSegments){
            ArrayList<LatLng> pointsLatLng = new ArrayList<>();
            for(Location l : segment.getLocations()){
                pointsLatLng.add(new LatLng(l.getLatitude(),l.getLongitude()));
            }

            PolylineOptions polylineOptions = new PolylineOptions();
            polylineOptions.addAll(pointsLatLng).width(10).visible(true).color(Color.BLUE)
                    .startCap(new RoundCap()).endCap(new RoundCap())
                    .jointType(JointType.ROUND);
            Polyline routePolyline = mMap.addPolyline(polylineOptions);
            test += segment.getLocations() +"\n";
        }
        TextView title = findViewById(R.id.pastTripTitleTextView);
        title.setText(tripEntity.getTitle() + "\n" + test);
    }

    public void getTripFromDatabase(int tripID){
        SaveManager.getTripByID(this, tripID , new SaveManager.TripsCallback(){
            @Override
            public void onTripsLoaded(List<TripEntity> trips) {
                //nothing
            }
            @Override
            public void onTripLoaded(TripEntity trip) {
                tripEntity = trip;
            }
        });

    }
}