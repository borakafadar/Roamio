package com.borakafadar.roamio;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AccountFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccountFragment extends Fragment {


    private static final String PERMISSION_LOCATION = Manifest.permission_group.LOCATION;
    //TODO: ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission_group.LOCATION},540);
    //migrate the permissions to these
    private static final int PERMISSION_REQUEST_CODE = 540;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AccountFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AccountFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AccountFragment newInstance(String param1, String param2) {
        AccountFragment fragment = new AccountFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        view.findViewById(R.id.permissionButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestRuntimePermission();
            }
        });

        return view;
    }

    private void requestRuntimePermission() {
        if (ActivityCompat.checkSelfPermission(this.getActivity(), PERMISSION_LOCATION) == PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this.getActivity(), "Permission for background location has already been granted.", Toast.LENGTH_SHORT).show();
        } else if (shouldShowRequestPermissionRationale(PERMISSION_LOCATION)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
            builder.setMessage("This app requires background location activity to track trips when you are not in the app.").setTitle("Permission Required").setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions(AccountFragment.this.getActivity(),new String[]{PERMISSION_LOCATION},PERMISSION_REQUEST_CODE);
                    dialog.dismiss();
                }
            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            builder.show();
        }else{
            ActivityCompat.requestPermissions(this.getActivity(), new String[]{PERMISSION_LOCATION},PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == PERMISSION_REQUEST_CODE){
            Toast.makeText(this.getActivity(), "Request has been granted.", Toast.LENGTH_SHORT).show();
        } else if(!shouldShowRequestPermissionRationale(PERMISSION_LOCATION)){
            AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
            builder.setMessage("You cannot use this app. Get out").setTitle("Permission Required").setCancelable(false).setNegativeButton("Close",(dialog, which) -> dialog.dismiss()).setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package",AccountFragment.this.getActivity().getPackageName(),null);
                    intent.setData(uri);
                    startActivity(intent);

                    dialog.dismiss();
                }
            });
        } else {
            requestRuntimePermission();
        }
    }
}