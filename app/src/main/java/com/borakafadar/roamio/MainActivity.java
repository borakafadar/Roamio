package com.borakafadar.roamio;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainer;

import com.borakafadar.roamio.App.Save.SaveManager;
import com.borakafadar.roamio.App.User;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {

    private final String FINE_LOCATION_PERMISSION = Manifest.permission.ACCESS_FINE_LOCATION;
    private final String COARSE_LOCATION_PERMISSION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private final String BACKGROUND_LOCATION_PERMISSION = Manifest.permission.ACCESS_BACKGROUND_LOCATION;
    private final String NOTIFICATION_PERMISSION = Manifest.permission.POST_NOTIFICATIONS;
    private final String FOREGROUND_SERVICE_PERMISSION = Manifest.permission.FOREGROUND_SERVICE;
    private final int PERMISSION_REQUEST_CODE = 100;



    //TODO check what is geofencing
    //https://developer.android.com/develop/sensors-and-location/location/geofencing

    //TODO check background work for background location tracking

    //TODO make a AlertDialog Builder for unrepeated code

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        SaveManager.getUserCount(this,new SaveManager.UserCallback(){
            @Override
            public void onUserLoaded(User user) {
                //nothing
            }
            @Override
            public void onUserCountLoaded(int count) {
                if(count == 0){
                    //welcome activity startup
                    Toast.makeText(MainActivity.this, "The user not found", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
                    startActivity(intent);
                }
            }
        });

//
//        FrameLayout fragmentContainer = findViewById(R.id.fragment_container);
//        NavigationBarView bottomNavMenu = findViewById(R.id.bottomNavigationView);
//        int screenHeight = getResources().getDisplayMetrics().heightPixels;
//
//        //%89 percent main fragments %11 percent bottomNavMenu
//        int topPartHeight = (int) (screenHeight * 0.89f);
//        int bottomPartHeight = (int) (screenHeight * 0.11f);
//
//        Log.d("heights",topPartHeight+" "+bottomPartHeight);
//        if(bottomPartHeight < 320){
//            bottomPartHeight = 320;
//            topPartHeight = screenHeight - bottomPartHeight;
//        }
//
//        ViewGroup.LayoutParams fragmentContainerLayoutParams = fragmentContainer.getLayoutParams();
//        fragmentContainerLayoutParams.height = topPartHeight;
//        fragmentContainer.setLayoutParams(fragmentContainerLayoutParams);
//
//        ViewGroup.LayoutParams bottomNavMenuLayoutParams = bottomNavMenu.getLayoutParams();
//        bottomNavMenuLayoutParams.height = bottomPartHeight;
//        bottomNavMenu.setLayoutParams(bottomNavMenuLayoutParams);






        Toast.makeText(this, "The app is open", Toast.LENGTH_SHORT).show();

        //ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},1);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
        NavigationBarView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(this);


        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                requestPermissions();
            }
        }, 1000);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        Fragment selectedFragment = null;
        int itemID = item.getItemId();

        if(itemID == R.id.homeMenu){
            selectedFragment = new HomeFragment();
            //Toast.makeText(this, "Home clicked", Toast.LENGTH_SHORT).show();
        }
        else if(itemID == R.id.tripsMenu){
            selectedFragment = new TripsFragment();
            //Toast.makeText(this, "Trips clicked", Toast.LENGTH_SHORT).show();
        }
        else if(itemID == R.id.accountMenu){
            selectedFragment = new AccountFragment();
            //Toast.makeText(this, "Account clicked", Toast.LENGTH_SHORT).show();
        } else {
            return false;
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();

        return true;
    }


    public void requestPermissions(){
        if(checkSelfPermission(FINE_LOCATION_PERMISSION) == PackageManager.PERMISSION_GRANTED || checkSelfPermission(COARSE_LOCATION_PERMISSION) == PackageManager.PERMISSION_GRANTED){
            Log.d("permissions","Fine and/or coarse location permission granted");
        } else if(shouldShowRequestPermissionRationale(FINE_LOCATION_PERMISSION)){
            View dialogView = LayoutInflater.from(this).inflate(R.layout.alert_dialog, null);
            TextView textView = dialogView.findViewById(R.id.alertDialogTextView);
            textView.setText("Location permission is required to track your location and your trips, don't worry we won't share your location with anyone because your data is only stored in your device");

            AlertDialog alertDialog = new MaterialAlertDialogBuilder(this).setTitle("Location permission").setView(dialogView)
                    .setCancelable(false).setPositiveButton("OK", (dialog, which) -> {
                        requestPermissions(new String[]{FINE_LOCATION_PERMISSION},PERMISSION_REQUEST_CODE);
                        dialog.dismiss();
                    }).setNegativeButton("Cancel", (dialog, which) -> {
                        dialog.dismiss();
                    }).create();

            alertDialog.show();

        } else{
            requestPermissions(new String[]{FINE_LOCATION_PERMISSION,COARSE_LOCATION_PERMISSION,NOTIFICATION_PERMISSION,FOREGROUND_SERVICE_PERMISSION},PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == PERMISSION_REQUEST_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Log.d("permissions","Fine location permission granted");
            } else{
                Log.d("permissions","Fine location permission denied");
            }
        } else if(!shouldShowRequestPermissionRationale(FINE_LOCATION_PERMISSION)){

            AlertDialog alertDialog = new MaterialAlertDialogBuilder(this).setTitle("Permission Required").setMessage("This feature is unavailable, please enable location permission in settings")
                    .setCancelable(false)
                    .setNegativeButton("Cancel", (dialog, which) -> {
                      dialog.dismiss();
                    }).setPositiveButton("Settings", (dialog, which) -> {
                        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                        dialog.dismiss();
                    }).create();

            alertDialog.show();
        }
        else{
            requestPermissions();
        }
    }


}