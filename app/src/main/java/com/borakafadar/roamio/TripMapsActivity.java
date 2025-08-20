package com.borakafadar.roamio;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;

import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.borakafadar.roamio.App.Save.SaveManager;
import com.borakafadar.roamio.App.Save.TripEntity;
import com.borakafadar.roamio.App.TripSegment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.borakafadar.roamio.databinding.ActivityTripMapsBinding;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

public class TripMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityTripMapsBinding binding;
    private TripEntity tripEntity;
    private int tripID;

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

        tripID = bundle.getInt("TRIP_ID");


        getTripFromDatabase(tripID);

        TextView title = findViewById(R.id.pastTripTitleTextView);
        TextView comments = findViewById(R.id.pastTripCommentTextView);
        TextView date = findViewById(R.id.pastTripDateTextView);
        TextView time = findViewById(R.id.pastTripTimeTextView);
        TextView distance = findViewById(R.id.pastTripDistanceTextView);

        Button editTitleButton = findViewById(R.id.pastTripEditTitleButton);

        editTitleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateTripTitle();
            }
        });

        Button editCommentsButton = findViewById(R.id.pastTripCommentButton);

        editCommentsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateTripComments();
            }
        });

        Button deleteTripButton = findViewById(R.id.pastTripDeleteTripButton);

        deleteTripButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteTrip();
            }
        });



        Handler handler = new Handler(Looper.getMainLooper());
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if(tripEntity != null){
                    title.setText(tripEntity.getTitle());
                    comments.setText(tripEntity.getComments());
                    date.setText(tripEntity.getDate());
                    time.setText(tripEntity.getDuration());
                    distance.setText(String.format("%.2f", tripEntity.getDistance()) + " km");


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
        getTripFromDatabase(tripID);

        LatLng bilkentUniversity = new LatLng(39.867349, 32.750255);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(bilkentUniversity));

        Handler handler = new Handler(Looper.getMainLooper());
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if(tripEntity != null){
                    updateCamera(tripEntity.getTripSegments());
                }
            }
        };
        handler.postDelayed(runnable, 1000);
    }

    public void updatePolylines(ArrayList<TripSegment> tripSegments){
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
        }

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

    public void updateCamera(ArrayList<TripSegment> tripSegments){
        double longitude = 0;
        double latitude = 0;
        int locationSize = 0;
        //FIXME fix camera
        for(TripSegment segment : tripSegments){
            for(Location l : segment.getLocations()){
                longitude+=l.getLongitude();
                latitude+=l.getLatitude();
                locationSize++;
            }
        }
        longitude /= locationSize;
        latitude /= locationSize;

        LatLng currentLocation = new LatLng(latitude, longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(12));

    }

    public void updateTripTitle(){
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_input, null);
        TextInputEditText editText = view.findViewById(R.id.tripDialogEditText);
        TextInputLayout textInputLayout = view.findViewById(R.id.tripDialogTextInputLayout);
        textInputLayout.setHint("Enter Trip Title");
        editText.setText(tripEntity.getTitle());

        AlertDialog alertDialog = new MaterialAlertDialogBuilder(this).setTitle("Change Trip Title")
                .setView(view).setPositiveButton("OK", (dialog, which) -> {
                    TextView title = findViewById(R.id.pastTripTitleTextView);
                    title.setText(editText.getText().toString());
                    tripEntity.setTitle(editText.getText().toString());

                    SaveManager.updateTrip(this, tripEntity);
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss();
                }).create();

        alertDialog.show();
    }

    public void updateTripComments(){
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_input, null);
        TextInputEditText editText = view.findViewById(R.id.tripDialogEditText);
        TextInputLayout textInputLayout = view.findViewById(R.id.tripDialogTextInputLayout);
        textInputLayout.setHint("Enter Trip Comments");
        editText.setText(tripEntity.getComments());

        AlertDialog alertDialog = new MaterialAlertDialogBuilder(this).setTitle("Change Trip Comments")
                .setView(view).setPositiveButton("OK", (dialog, which) -> {
                    TextView comments = findViewById(R.id.pastTripCommentTextView);
                    comments.setText(editText.getText().toString());
                    tripEntity.setComments(editText.getText().toString());

                    SaveManager.updateTrip(this, tripEntity);
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss();
                }).create();

        alertDialog.show();
    }

    public void deleteTrip(){
        View view = LayoutInflater.from(this).inflate(R.layout.alert_dialog, null);
        TextView textView = view.findViewById(R.id.alertDialogTextView);
        textView.setText(R.string.trip_delete_confirmation_text);

        AlertDialog alertDialog = new MaterialAlertDialogBuilder(this).setTitle("Delete Trip").setView(view)
                .setPositiveButton("Yes", (dialog, which) -> {
                    SaveManager.deleteTrip(TripMapsActivity.this,tripEntity);
                    finish();
                })
                .setNegativeButton("No", (dialog, which) -> {
                    dialog.dismiss();
                }).create();

        alertDialog.show();
    }




}